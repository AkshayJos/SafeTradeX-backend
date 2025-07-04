package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.VerificationType;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.VerificationCode;

public interface VerificationCodeService {
    VerificationCode sendVerificationCode(User user, VerificationType verificationType);

    VerificationCode getVerificationCodeById(Long id) throws Exception;

    VerificationCode getVerificationCodeByUserId(Long userId);

    void deleteVerificationCodeById(VerificationCode verificationCode);
}
