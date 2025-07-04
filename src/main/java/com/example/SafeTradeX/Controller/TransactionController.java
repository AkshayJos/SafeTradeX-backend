package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Model.WalletTransaction;
import com.example.SafeTradeX.Service.UserService;
import com.example.SafeTradeX.Service.WalletService;
import com.example.SafeTradeX.Service.WalletTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TransactionController {

    @Autowired
    private UserService userService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    @GetMapping("/api/transactions")
    public ResponseEntity<?> getUserWalletTransactions(@RequestHeader("Authorization") String token){
        try {
            User user = userService.findUserProfileByJwt(token);
            Wallet wallet = walletService.getUserWallet(user);

            List<WalletTransaction> walletTransactions = walletTransactionService.getWalletTransactionsByWallet(wallet);

            return new ResponseEntity<>(walletTransactions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
