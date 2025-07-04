package com.example.SafeTradeX.Response;

import lombok.Data;

@Data
public class PaymentResponse {

    private String paymentUrl;

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }
}
