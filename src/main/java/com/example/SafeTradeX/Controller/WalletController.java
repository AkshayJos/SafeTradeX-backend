package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Domain.WalletTransactionType;
import com.example.SafeTradeX.Model.*;
import com.example.SafeTradeX.Response.PaymentResponse;
import com.example.SafeTradeX.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    @Autowired
    private WalletService walletService;

    @Autowired
    private UserService userService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    @GetMapping
    public ResponseEntity<?> getUserWallet(@RequestHeader("Authorization") String token){
        try {
            User user = userService.findUserProfileByJwt(token);
            Wallet wallet = walletService.getUserWallet(user);

            return new ResponseEntity<>(wallet, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/{walletId}/transfer")
    public ResponseEntity<?> walletToWalletTransfer(@RequestHeader("Authorization") String token, @PathVariable Long walletId, @RequestBody WalletTransaction walletTransaction){
        try {
            User senderUser = userService.findUserProfileByJwt(token);
            Wallet recieverWallet = walletService.findWalletById(walletId);
            Wallet wallet = walletService.walletToWalletTransaction(senderUser, recieverWallet, walletTransaction.getAmount());

            walletTransactionService.addWalletTransaction(
                  wallet, WalletTransactionType.SENT_MONEY, walletTransaction.getTransferId(), walletTransaction.getAmount(),WalletTransactionType.SENT_MONEY + " -> " + walletTransaction.getPurpose()
            );

            walletTransactionService.addWalletTransaction(
                    recieverWallet, WalletTransactionType.RECIEVED_MONEY, walletTransaction.getTransferId(), walletTransaction.getAmount(), WalletTransactionType.RECIEVED_MONEY + " ->  " + walletTransaction.getPurpose()
            );

            return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return  new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/order/{orderId}/pay")
    public ResponseEntity<?> payOrderPayment(@RequestHeader("Authorization") String token, @PathVariable Long orderId){
        try {
            User user = userService.findUserProfileByJwt(token);
            Order order = orderService.getOrderById(orderId);

            Wallet wallet = walletService.payOrderPayment(order, user);

            return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return  new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/deposite")
    public ResponseEntity<?> addBalanceToWallet(@RequestHeader("Authorization") String token, @RequestParam(name = "order_id") Long orderId, @RequestParam(name = "payment_id") String paymentId){
        try {
            User user = userService.findUserProfileByJwt(token);

            Wallet wallet = walletService.getUserWallet(user);

            PaymentOrder order = paymentService.getPaymentOrderById(orderId);

            Boolean status = paymentService.proceedPaymentOrder(order, paymentId);

            PaymentResponse response = new PaymentResponse();

            if(wallet.getBalance() == null) wallet.setBalance(BigDecimal.valueOf(0));

            if (status){
                wallet = walletService.addBalance(wallet, order.getAmount());
                walletTransactionService.addWalletTransaction(
                        wallet, WalletTransactionType.ADD_MONEY, "", (double) order.getAmount(), String.valueOf(WalletTransactionType.ADD_MONEY)
                );
            }

            return new ResponseEntity<>(wallet, HttpStatus.ACCEPTED);
        } catch (Exception e) {
            return  new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
