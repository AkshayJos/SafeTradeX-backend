package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Domain.VerificationType;
import com.example.SafeTradeX.Model.ForgotPasswordToken;
import com.example.SafeTradeX.Model.TwoFactorOTP;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.VerificationCode;
import com.example.SafeTradeX.Repository.ForgotPasswordRepository;
import com.example.SafeTradeX.Repository.VerificationCodeRepository;
import com.example.SafeTradeX.Request.ForgotPasswordTokenRequest;
import com.example.SafeTradeX.Request.ResetPasswordRequest;
import com.example.SafeTradeX.Response.ApiResponse;
import com.example.SafeTradeX.Response.AuthResponse;
import com.example.SafeTradeX.Service.*;
import com.example.SafeTradeX.Utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @Autowired
    private ForgotPasswordService forgotPasswordService;

    @Autowired
    private ForgotPasswordRepository forgotPasswordRepository;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Autowired
    private TwoFactorOtpService twoFactorOtpService;

    @GetMapping("/api/users/profile")
    public ResponseEntity<?> getUserProfile(@RequestHeader("Authorization") String token) {
        try {
            System.out.println(token);
            User user = userService.findUserProfileByJwt(token);

            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @PatchMapping("/enable-two-factor/verify-otp/{otp}")
    public ResponseEntity<?> enableTwoFactorAuthentication(@RequestHeader("Authorization") String jwt, @PathVariable String otp) {
        try {
            User user = userService.findUserProfileByJwt(jwt);
            VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUserId(user.getId());

            String sendTo = verificationCode.getVerificationType()
                    .equals(VerificationType.EMAIL)
                    ? verificationCode.getEmail() : verificationCode.getMobile();

            boolean isVerified = verificationCode.getOtp().equals(otp);

            if(isVerified){
                User updatedUser = userService.enableTwoFactorAuthentication(
                        verificationCode.getVerificationType(), sendTo, user
                );

                verificationCodeService.deleteVerificationCodeById(verificationCode);
                return new ResponseEntity<>(updatedUser, HttpStatus.OK);
            }

            return new ResponseEntity<>("Invalid OTP!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/verification/{verificationType}/send-otp")
    public ResponseEntity<?> sendVerificationOtp(@RequestHeader("Authorization") String jwt, @PathVariable VerificationType verificationType) {
        try {
            User user = userService.findUserProfileByJwt(jwt);

            VerificationCode verificationCode = verificationCodeService.getVerificationCodeByUserId(user.getId());

            if(verificationCode == null){
               verificationCode = verificationCodeService.sendVerificationCode(user, verificationType);
            }
            else{
                verificationCode.setOtp(OtpUtils.generateOTP());
                verificationCodeRepository.save(verificationCode);
            }

            if(verificationType.equals(VerificationType.EMAIL)){
                emailService.sendVerificationOTPEmail(verificationCode.getEmail(), verificationCode.getOtp());
            }

            return new ResponseEntity<>("Verification OTP sent successfully!", HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @PostMapping("auth/reset-password/send-otp")
    public ResponseEntity<?> sendForgotPasswordOtp (@RequestBody ForgotPasswordTokenRequest request) {
        try {
            User user = userService.findUserByEmail(request.getSendTo());
            String otp = OtpUtils.generateOTP();
            UUID uuid = UUID.randomUUID();
            String id = uuid.toString();

            ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findByUserId(user.getId());

            if(forgotPasswordToken == null){
                forgotPasswordToken = forgotPasswordService.createToken(user, id, otp, request.getVerificationType(), request.getSendTo());
            } else {
                forgotPasswordToken.setOtp(otp);
                forgotPasswordRepository.save(forgotPasswordToken);
            }

            if(request.getVerificationType().equals(VerificationType.EMAIL)){
                emailService.sendVerificationOTPEmail(user.getEmail(), forgotPasswordToken.getOtp());
            }

            AuthResponse authResponse = new AuthResponse();
            authResponse.setSession(forgotPasswordToken.getId());
            authResponse.setMessage("Password reset OTP sent successfully!");

            return new ResponseEntity<>(authResponse, HttpStatus.OK);
        }
        catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("auth/users/reset-password/verify-otp")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordRequest request) {
        try {
            User user = userService.findUserByEmail(request.getEmail());

            ForgotPasswordToken forgotPasswordToken = forgotPasswordService.findByUserId(user.getId());

            boolean isVerifed = forgotPasswordToken.getOtp().equals(request.getOtp());

            if(isVerifed){
                userService.updatePassword(forgotPasswordToken.getUser(), request.getPassword());
                ApiResponse apiResponse = new ApiResponse();
                apiResponse.setMessage("Password updated successfully!");

                return new ResponseEntity<>(apiResponse, HttpStatus.ACCEPTED);
            }

            return new ResponseEntity<>("Invalid OTP!", HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }
}
