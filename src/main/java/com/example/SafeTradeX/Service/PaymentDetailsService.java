package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.PaymentDetails;
import com.example.SafeTradeX.Model.User;

public interface PaymentDetailsService {

    public PaymentDetails addPaymentDetails(String accountNumber, String accountHolderName, String ifscCode, String bankName, User user);

    public PaymentDetails getUserPaymentDetails(User user);
}
