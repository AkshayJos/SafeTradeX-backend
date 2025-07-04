package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.PaymentGateway;
import com.example.SafeTradeX.Model.PaymentOrder;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Response.PaymentResponse;
import com.razorpay.RazorpayException;
import com.stripe.exception.StripeException;

public interface PaymentService {

    PaymentOrder createOrder(User user, Double amount, PaymentGateway paymentGateway);

    PaymentOrder getPaymentOrderById(Long id) throws Exception;

    Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentId) throws RazorpayException, StripeException;

    PaymentResponse createRazorPayPaymentLink(User user, Double amount, Long orderId) throws Exception;

    PaymentResponse createStripePayPaymentLink(User user, Double amount, Long orderId) throws StripeException;

}
