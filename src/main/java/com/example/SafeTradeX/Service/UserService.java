package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.VerificationType;
import com.example.SafeTradeX.Model.TwoFactorAuth;
import com.example.SafeTradeX.Model.TwoFactorOTP;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Repository.UserRepository;
import com.example.SafeTradeX.Response.AuthResponse;
import com.example.SafeTradeX.Security.jwt.JwtService;
import com.example.SafeTradeX.Utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @Autowired
    private EmailService emailService;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public ResponseEntity<?> login(User user) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getEmail(), user.getPassword()));

            if (authentication.isAuthenticated()) {
                String token = jwtService.generateToken(authentication);

                user = findUserByEmail(user.getEmail());

                if(user.getTwoFactorAuth().isEnable()){
                    AuthResponse authResponse = new AuthResponse();
                    authResponse.setMessage("Two factor auth is enabled");
                    authResponse.setTwoFactorAuthEnabled(true);

                    String otp = OtpUtils.generateOTP();

                    TwoFactorOTP oldTwoFactorOTP =  twoFactorOtpService.findByUserId(user.getId());
                    if(oldTwoFactorOTP != null){
                        twoFactorOtpService.deleteTwoFactorOtp(oldTwoFactorOTP);
                    }

                    TwoFactorOTP newTwoFactorOTP = twoFactorOtpService.createTwoFactorOtp(user, otp, token);

                    emailService.sendVerificationOTPEmail(user.getEmail(), otp);

                    authResponse.setSession(newTwoFactorOTP.getId());
                    authResponse.setJwt(token);
                    authResponse.setStatus(true);

                    return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);
                }

                AuthResponse authResponse = new AuthResponse();
                authResponse.setJwt(token);
                authResponse.setStatus(true);
                authResponse.setMessage("User login succesfully!");

                return new ResponseEntity<>(authResponse, HttpStatus.ACCEPTED);
            }

            return new ResponseEntity<>("Invalid credentials", HttpStatus.UNAUTHORIZED);

        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>("Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<?> registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists.");
        }
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                user.getEmail(),
                user.getPassword()
        );

        String token = jwtService.generateToken(authentication);
        AuthResponse authResponse = new AuthResponse();
        authResponse.setJwt(token);
        authResponse.setStatus(true);
        authResponse.setMessage("User signup succesfully!");

        User newUser = new User();

        newUser.setFullName(user.getFullName());
        newUser.setEmail(user.getEmail());
        newUser.setPassword(encoder.encode(user.getPassword()));

        userRepository.save(newUser);

        watchlistService.createWatchlist(newUser);

        return new ResponseEntity<>(authResponse, HttpStatus.CREATED);
    }

    public ResponseEntity<?> verifySigninOtp(String otp, String token) throws Exception {
        User user = findUserProfileByJwt(token);
        TwoFactorOTP twoFactorOTP = twoFactorOtpService.findByUserId(user.getId());
        AuthResponse authResponse = new AuthResponse();

        if(twoFactorOtpService.verifyTwoFactorOtp(twoFactorOTP, otp)){
           authResponse.setMessage("Two factor authentication verified!");
           authResponse.setTwoFactorAuthEnabled(true);
           authResponse.setJwt(twoFactorOTP.getJwt());

           return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }

        authResponse.setMessage("Invalid OTP!");
        return new ResponseEntity<>(authResponse, HttpStatus.UNAUTHORIZED);
    }

    public User findUserProfileByJwt(String token) throws Exception {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new Exception("Token is not valid!");
        }
        String email = jwtService.getUserNameFromJwtToken(token.substring(7));

        return userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));

    }

    public User findUserByEmail(String email){
        return userRepository.findByEmail(email).
                orElseThrow(() -> new UsernameNotFoundException("User Not Found with email: " + email));
    }

    public User findUserById(Long userId){
        return userRepository.findById(userId).
                orElseThrow(() -> new UsernameNotFoundException("User Not Found with id: " + userId));
    }

    public User enableTwoFactorAuthentication(VerificationType verificationType, String sendTo, User user){
        TwoFactorAuth twoFactorAuth = new TwoFactorAuth();
        twoFactorAuth.setEnable(true);
        twoFactorAuth.setSendTo(verificationType);

        user.setTwoFactorAuth(twoFactorAuth);

        return userRepository.save(user);
    }

    public User updatePassword(User user, String newPassword){
        user.setPassword(encoder.encode(newPassword));
        return userRepository.save(user);
    }
}
