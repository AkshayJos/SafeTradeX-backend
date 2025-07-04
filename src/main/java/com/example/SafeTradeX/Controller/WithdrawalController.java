package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Domain.USER_ROLE;
import com.example.SafeTradeX.Domain.WalletTransactionType;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Model.Withdrawal;
import com.example.SafeTradeX.Service.UserService;
import com.example.SafeTradeX.Service.WalletService;
import com.example.SafeTradeX.Service.WalletTransactionService;
import com.example.SafeTradeX.Service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class WithdrawalController {

    @Autowired
    private WithdrawalService withdrawalService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    @PostMapping("/withdrawal/{amount}")
    public ResponseEntity<?> withdrawalRequest(@PathVariable Double amount, @RequestHeader("Authorization") String token) throws Exception {
        try {
            User user = userService.findUserProfileByJwt(token);
//            Wallet userWallet = walletService.getUserWallet(user);

            Withdrawal withdrawal = withdrawalService.requestWithdrawal(amount, user);
//        walletService.addBalance(userWallet, -withdrawal.getAmount());
//
//        walletTransactionService.addWalletTransaction(
//                userWallet, WalletTransactionType.WITHDRAWAL_MONEY, null, (double) withdrawal.getAmount(), String.valueOf(WalletTransactionType.WITHDRAWAL_MONEY)
//        );

            if(user.getRole().equals(USER_ROLE.ROLE_ADMIN)){
                Withdrawal withdrawal1 = withdrawalService.processWithdrawal(withdrawal.getId(), true);

                Wallet userWallet = walletService.getUserWallet(user);

                walletService.addBalance(userWallet, -withdrawal1.getAmount());
                walletTransactionService.addWalletTransaction(
                        userWallet, WalletTransactionType.WITHDRAWAL_MONEY, null, (double) withdrawal1.getAmount(), String.valueOf(WalletTransactionType.WITHDRAWAL_MONEY)
                );

                return new ResponseEntity<>(withdrawal1, HttpStatus.OK);
            }

            return new ResponseEntity<>(withdrawal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/admin/withdrawal/{id}/proceed/{accept}")
    public ResponseEntity<?> proceedWithdrawal(@PathVariable Long id, @PathVariable boolean accept, @RequestHeader("Authorization") String token){
        try {
            User adminUser = userService.findUserProfileByJwt(token);

            if(adminUser.getRole().equals(USER_ROLE.ROLE_ADMIN)){
                Withdrawal withdrawal = withdrawalService.processWithdrawal(id, accept);

                User user = withdrawalService.getById(id).getUser();

                Wallet userWallet = walletService.getUserWallet(user);
                if(accept){
                    walletService.addBalance(userWallet, -withdrawal.getAmount());
                    walletTransactionService.addWalletTransaction(
                            userWallet, WalletTransactionType.WITHDRAWAL_MONEY, null, (double) withdrawal.getAmount(), String.valueOf(WalletTransactionType.WITHDRAWAL_MONEY)
                    );
                }

                return new ResponseEntity<>(withdrawal, HttpStatus.OK);
            }

            return new ResponseEntity<>("You are not admin!", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/withdrawal")
    public ResponseEntity<?> getWithdrawalHistory(@RequestHeader("Authorization") String token){
        try {
            User user = userService.findUserProfileByJwt(token);

            List<Withdrawal> AllWithdrawalHistory = withdrawalService.getUserWithdrawalHistory(user);

            return new ResponseEntity<>(AllWithdrawalHistory, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/admin/withdrawal")
    public ResponseEntity<?> getAllWithdrawalRequest(@RequestHeader("Authorization") String token){
        try {
            User user = userService.findUserProfileByJwt(token);

            List<Withdrawal> AllWithdrawalRequest = withdrawalService.getAllWithdrawalRequest();

            return new ResponseEntity<>(AllWithdrawalRequest, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
