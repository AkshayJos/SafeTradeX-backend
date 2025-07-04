package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Domain.PaymentGateway;
import com.example.SafeTradeX.Model.PaymentOrder;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Response.PaymentResponse;
import com.example.SafeTradeX.Service.PaymentService;
import com.example.SafeTradeX.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PaymentController {

    @Autowired
    private UserService userService;

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/payment/{paymentGateway}/amount/{amount}")
    public ResponseEntity<?> paymentHandler(@PathVariable PaymentGateway paymentGateway, @PathVariable Double amount, @RequestHeader("Authorization") String token){
        try {
            System.out.println(token);
            User user = userService.findUserProfileByJwt(token);

            PaymentResponse paymentResponse;

            PaymentOrder paymentOrder = paymentService.createOrder(user, amount, paymentGateway);

            if(paymentGateway.equals(PaymentGateway.RAZORPAY)){
                paymentResponse = paymentService.createRazorPayPaymentLink(user, amount, paymentOrder.getId());
            }else{
              paymentResponse = paymentService.createStripePayPaymentLink(user, amount, paymentOrder.getId());
            }

            return new ResponseEntity<>(paymentResponse, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
