package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.WalletTransactionType;
import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Model.WalletTransaction;

import java.util.List;

public interface WalletTransactionService {

    List<WalletTransaction> getWalletTransactionsByWallet(Wallet wallet) throws Exception;

    WalletTransaction addWalletTransaction(Wallet wallet,  WalletTransactionType transactionType, String transferId, Double amount, String purpose);
}
