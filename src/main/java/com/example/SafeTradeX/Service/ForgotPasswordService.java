package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.VerificationType;
import com.example.SafeTradeX.Model.ForgotPasswordToken;
import com.example.SafeTradeX.Model.User;

public interface ForgotPasswordService {

    ForgotPasswordToken createToken(User user, String id, String otp, VerificationType verificationType, String sendTo);

    ForgotPasswordToken findById(String id) throws Exception;

    ForgotPasswordToken findByUserId(Long userId);

    void deleteToken(ForgotPasswordToken forgotPasswordToken);
}
