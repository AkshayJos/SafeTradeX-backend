package com.example.SafeTradeX.Repository;

import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    List<Withdrawal> findByUserId(Long userId);

}
