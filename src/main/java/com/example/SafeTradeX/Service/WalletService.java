package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.Order;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Wallet;

public interface WalletService {

    Wallet getUserWallet(User user);

    Wallet addBalance(Wallet wallet, Double money);

    Wallet findWalletById(Long id) throws Exception;

    Wallet walletToWalletTransaction(User sender, Wallet recieverWallet, Double amount) throws Exception;

    Wallet payOrderPayment(Order order, User user) throws Exception;
}
