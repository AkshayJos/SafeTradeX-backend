package com.example.SafeTradeX.Repository;

import com.example.SafeTradeX.Model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    public VerificationCode findUserById(Long userId);
}
