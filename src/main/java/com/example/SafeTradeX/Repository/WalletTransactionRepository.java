package com.example.SafeTradeX.Repository;

import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Model.WalletTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, Long> {

    List<WalletTransaction> getWalletTransactionsByWallet(Wallet wallet);

}
