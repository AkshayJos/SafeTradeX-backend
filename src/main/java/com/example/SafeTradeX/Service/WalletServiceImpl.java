package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Domain.OrderType;
import com.example.SafeTradeX.Model.Order;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Repository.WalletRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class WalletServiceImpl implements WalletService{

    @Autowired
    private WalletRepository walletRepository;

    @Override
    public Wallet getUserWallet(User user) {
        Wallet wallet = walletRepository.findByUserId(user.getId());
        if(wallet == null){
            wallet = new Wallet();
            wallet.setUser(user);

          wallet = walletRepository.save(wallet);
        }
        return wallet;
    }

    @Override
    public Wallet addBalance(Wallet wallet, Double money) {
        BigDecimal balance = wallet.getBalance();
        BigDecimal newBalance = balance.add(BigDecimal.valueOf(money));

        wallet.setBalance(newBalance);

        return walletRepository.save(wallet);
    }

    @Override
    public Wallet findWalletById(Long id) throws Exception {
        return walletRepository.findById(id).
                orElseThrow(()-> new Exception("Wallet not found!"));
    }

    @Override
    public Wallet walletToWalletTransaction(User sender, Wallet recieverWallet, Double amount) throws Exception {
       Wallet senderWallet = getUserWallet(sender);
       if(senderWallet.getBalance().compareTo(BigDecimal.valueOf(amount)) < 0){
           throw new Exception("Unsufficient Balance!");
       }

       BigDecimal senderBalance = senderWallet.getBalance().subtract(BigDecimal.valueOf(amount));
       senderWallet.setBalance(senderBalance);
       walletRepository.save(senderWallet);

       BigDecimal recieverBalance = recieverWallet.getBalance().add(BigDecimal.valueOf(amount));
       recieverWallet.setBalance(recieverBalance);
       walletRepository.save(recieverWallet);

        return senderWallet;
    }

    @Override
    public Wallet payOrderPayment(Order order, User user) throws Exception {
        Wallet wallet = getUserWallet(user);

        if(order.getOrderType().equals(OrderType.BUY)){
            BigDecimal newBalance = wallet.getBalance().subtract(order.getPrice());
            wallet.setBalance(newBalance);
        }
        else{
            BigDecimal newBalance = wallet.getBalance().add(order.getPrice());
            wallet.setBalance(newBalance);
        }

       return walletRepository.save(wallet);
    }
}
