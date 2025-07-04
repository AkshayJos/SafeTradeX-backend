package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.WithDrawalStatus;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Model.Withdrawal;
import com.example.SafeTradeX.Repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WithdrawalServiceImp implements WithdrawalService{

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private WalletService walletService;

    @Override
    public Withdrawal requestWithdrawal(Double amount, User user) throws Exception {
        Wallet wallet = walletService.getUserWallet(user);

        if(wallet.getBalance().compareTo(BigDecimal.valueOf(amount)) >= 0){
            Withdrawal withdrawal = new Withdrawal();

            withdrawal.setAmount(amount);
            withdrawal.setUser(user);
            withdrawal.setStatus(WithDrawalStatus.PENDING);
            withdrawal.setDate(LocalDateTime.now());

            return withdrawalRepository.save(withdrawal);
        }

        throw new Exception("Insufficient money to withdraw!");
    }

    @Override
    public Withdrawal processWithdrawal(Long withdrawalId, boolean accept) throws Exception {
        Withdrawal withdrawal = getById(withdrawalId);

        withdrawal.setDate(LocalDateTime.now());

        if(accept){
            withdrawal.setStatus(WithDrawalStatus.SUCCESS);
        }else{
            withdrawal.setStatus(WithDrawalStatus.DECLINE);
        }
        return withdrawalRepository.save(withdrawal);
    }

    @Override
    public Withdrawal getById(Long withdrawalId) throws Exception {
        return withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new  Exception("Withdrawal not found!"));
    }

    @Override
    public List<Withdrawal> getUserWithdrawalHistory(User user) {
        return withdrawalRepository.findByUserId(user.getId());
    }

    @Override
    public List<Withdrawal> getAllWithdrawalRequest() {
        return withdrawalRepository.findAll();
    }
}
