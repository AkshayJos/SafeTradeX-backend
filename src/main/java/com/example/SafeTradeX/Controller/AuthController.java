package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

        @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody User user){
        return userService.login(user);
    }

    @PostMapping("verification/two-factor/verify/{otp}")
    public ResponseEntity<?> verifySigninOtp(@RequestHeader("Authorization") String token ,@PathVariable String otp){
        try {
            return userService.verifySigninOtp(otp, token);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}













