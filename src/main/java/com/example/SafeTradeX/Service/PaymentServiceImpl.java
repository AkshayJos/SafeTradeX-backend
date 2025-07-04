package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.PaymentGateway;
import com.example.SafeTradeX.Domain.PaymentOrderStatus;
import com.example.SafeTradeX.Model.PaymentOrder;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Repository.PaymentOrderRepository;
import com.example.SafeTradeX.Response.PaymentResponse;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService{

    @Autowired
    private PaymentOrderRepository paymentOrderRepository;

    @Value("${stripe.api.key}")
    private String stripeSecretKey;

    @Value("${razorpay.api.key}")
    private String razorpayApiKey;

    @Value("${razorpay.api.secret}")
    private String razorpayApiSecret;

    @Override
    public PaymentOrder createOrder(User user, Double amount, PaymentGateway paymentGateway) {
        PaymentOrder paymentOrder = new PaymentOrder();

        paymentOrder.setUser(user);
        paymentOrder.setAmount(amount);
        paymentOrder.setPaymentGateway(paymentGateway);
        paymentOrder.setStatus(PaymentOrderStatus.PENDING);

        return paymentOrderRepository.save(paymentOrder);
    }

    @Override
    public PaymentOrder getPaymentOrderById(Long id) throws Exception {
        return paymentOrderRepository.findById(id).
                orElseThrow(() -> new Exception("PaymentOrder not found!"));
    }

    @Override
    public Boolean proceedPaymentOrder(PaymentOrder paymentOrder, String paymentReference) throws RazorpayException, StripeException {
       if(paymentOrder.getStatus() == null){
           paymentOrder.setStatus(PaymentOrderStatus.PENDING);
       }
        if(paymentOrder.getStatus().equals(PaymentOrderStatus.PENDING)){
            if(paymentOrder.getPaymentGateway().equals(PaymentGateway.RAZORPAY)){
                RazorpayClient razorpayClient = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
                Payment payment = razorpayClient.payments.fetch(paymentReference);

                Integer amount = payment.get("amount");
                String status = payment.get("status");

                if(status.equals("captured")){
                        paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                        paymentOrderRepository.save(paymentOrder);   //added
                        return true;
                }

                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                return false;
            }
            else{
                Session session = Session.retrieve(paymentReference);
                String paymentIntentId = session.getPaymentIntent();
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

                String status = paymentIntent.getStatus();

                if (status.equals("succeeded")) {
                    paymentOrder.setStatus(PaymentOrderStatus.SUCCESS);
                    paymentOrderRepository.save(paymentOrder);
                    return true;
                }

                paymentOrder.setStatus(PaymentOrderStatus.FAILED);
                paymentOrderRepository.save(paymentOrder);
                return false;
            }
        }
        return false;
    }

    @Override
    public PaymentResponse createRazorPayPaymentLink(User user, Double amount, Long orderId) throws Exception {
            amount = amount * 100;

            RazorpayClient razorpayClient = new RazorpayClient(razorpayApiKey, razorpayApiSecret);
            JSONObject paymentLinkRequest = new JSONObject();

            paymentLinkRequest.put("amount", amount);
            paymentLinkRequest.put("currency", "INR");

            JSONObject customer = new JSONObject();
            customer.put("name", user.getFullName());
            customer.put("email", user.getEmail());

            JSONObject notify = new JSONObject();
            notify.put("email", true);

            paymentLinkRequest.put("customer", customer);
            paymentLinkRequest.put("notify", notify);
            paymentLinkRequest.put("reminder_enable", true);
            paymentLinkRequest.put("callback_url", "http://localhost:8080/wallet?order_id=" + orderId);
            paymentLinkRequest.put("callback_method", "get");

            PaymentLink paymentLink = razorpayClient.paymentLink.create(paymentLinkRequest);

            String paymentLinkId = paymentLink.get("id");
            String paymentLinkUrl = paymentLink.get("short_url");

            PaymentResponse paymentResponse = new PaymentResponse();
            paymentResponse.setPaymentUrl(paymentLinkUrl);

            return paymentResponse;
    }

    @Override
    public PaymentResponse createStripePayPaymentLink(User user, Double amount, Long orderId) throws StripeException {
        Stripe.apiKey = stripeSecretKey;

        amount = amount*100;

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl("http://localhost:5173/wallet?order_id=" + orderId + "&session_id={CHECKOUT_SESSION_ID}")
                .setCancelUrl("http://localhost:5173/payment/cancel")
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity(1L)
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmount(amount.longValue())
                                        .setProductData(SessionCreateParams
                                                .LineItem
                                                .PriceData
                                                .ProductData
                                                .builder()
                                                .setName("Top up wallet")
                                                .build()
                                        ).build()
                        ).build()
                ).build();

        Session session = Session.create(params);

        System.out.println("session _____ " + session);

        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setPaymentUrl(session.getUrl());

        return paymentResponse;
    }
}
