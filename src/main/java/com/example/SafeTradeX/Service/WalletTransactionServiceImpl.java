package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.WalletTransactionType;
import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Model.WalletTransaction;
import com.example.SafeTradeX.Repository.WalletTransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WalletTransactionServiceImpl implements WalletTransactionService{

    @Autowired
    private WalletTransactionRepository walletTransactionRepository;

    @Override
    public List<WalletTransaction> getWalletTransactionsByWallet(Wallet wallet) throws Exception {
        List<WalletTransaction> walletTransaction = walletTransactionRepository.getWalletTransactionsByWallet(wallet);

        if(walletTransaction.isEmpty()){
            throw new Exception("walletTransactions not found!");
        }
        return walletTransaction;
    }

    @Override
    public WalletTransaction addWalletTransaction(Wallet wallet, WalletTransactionType transactionType, String transferId, Double amount, String purpose){
        WalletTransaction walletTransaction = new WalletTransaction();
        walletTransaction.setWallet(wallet);
        walletTransaction.setTransactionType(transactionType);
        walletTransaction.setAmount(amount);
        walletTransaction.setDate(LocalDate.now());
        walletTransaction.setPurpose(purpose);
        walletTransaction.setTransferId(transferId);

        return walletTransactionRepository.save(walletTransaction);
    }
}
