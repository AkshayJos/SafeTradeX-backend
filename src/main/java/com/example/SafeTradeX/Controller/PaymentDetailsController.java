package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Model.PaymentDetails;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Service.PaymentDetailsService;
import com.example.SafeTradeX.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentDetailsController {

    @Autowired
    private PaymentDetailsService paymentDetailsService;

    @Autowired
    private UserService userService;

    @PostMapping("/payment-details")
    public ResponseEntity<?> addPaymentDetails(@RequestBody PaymentDetails paymentDetails, @RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserProfileByJwt(token);

        PaymentDetails paymentDetails1 = paymentDetailsService.addPaymentDetails(
                paymentDetails.getAccountNumber(),
                paymentDetails.getAccountHolderName(),
                paymentDetails.getIfscCode(),
                paymentDetails.getBankName(),
                user
        );

        return new ResponseEntity<>(paymentDetails, HttpStatus.CREATED);
    }

    @GetMapping("/payment-details")
    public ResponseEntity<?> getUserPaymentDetails(@RequestHeader("Authorization") String token) throws Exception {
        User user = userService.findUserProfileByJwt(token);

        PaymentDetails paymentDetails = paymentDetailsService.getUserPaymentDetails(user);

        return new ResponseEntity<>(paymentDetails, HttpStatus.ACCEPTED);
    }

}
