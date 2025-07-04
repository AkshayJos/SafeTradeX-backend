package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.VerificationType;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.VerificationCode;
import com.example.SafeTradeX.Repository.VerificationCodeRepository;
import com.example.SafeTradeX.Utils.OtpUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VerificationCodeServiceImpl implements VerificationCodeService{

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    @Override
    public VerificationCode sendVerificationCode(User user, VerificationType verificationType) {
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setOtp(OtpUtils.generateOTP());
        verificationCode.setVerificationType(verificationType);
        verificationCode.setUser(user);

        if(verificationType.equals(VerificationType.EMAIL)) verificationCode.setEmail(user.getEmail());

        return verificationCodeRepository.save(verificationCode);
    }

    @Override
    public VerificationCode getVerificationCodeById(Long id) throws Exception {

        return verificationCodeRepository.findById(id)
                .orElseThrow(() -> new Exception("Verification code not found!"));
    }

    @Override
    public VerificationCode getVerificationCodeByUserId(Long userId) {
        return verificationCodeRepository.findUserById(userId);
    }

    @Override
    public void deleteVerificationCodeById(VerificationCode verificationCode) {
        verificationCodeRepository.delete(verificationCode);
    }
}
