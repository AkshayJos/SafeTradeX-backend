package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Withdrawal;

import java.util.List;

public interface WithdrawalService {

    Withdrawal requestWithdrawal(Double amount, User user) throws Exception;

    Withdrawal getById(Long WithdrawalId) throws Exception;

    Withdrawal processWithdrawal(Long WithdrawalId, boolean accept) throws Exception;

    List<Withdrawal> getUserWithdrawalHistory(User user);

    List<Withdrawal> getAllWithdrawalRequest();

}
