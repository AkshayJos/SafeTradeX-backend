package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Domain.OrderType;
import com.example.SafeTradeX.Domain.WalletTransactionType;
import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Model.Order;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Wallet;
import com.example.SafeTradeX.Request.CreateOrderRequest;
import com.example.SafeTradeX.Service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

    @Autowired
    private WalletTransactionService walletTransactionService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/pay")
    public ResponseEntity<?> payOrderPayment(@RequestHeader("Authorization") String token, @RequestBody CreateOrderRequest request){
        try {
            User user = userService.findUserProfileByJwt(token);
            Coin coin = coinService.findById(request.getCoinId());
            Wallet wallet = walletService.getUserWallet(user);

            Order order = orderService.processOrder(coin, request.getQuantity(), request.getOrderType(), user);

            Double amount = request.getQuantity() * coin.getCurrentPrice();

            if(request.getOrderType().equals(OrderType.BUY)){
                walletTransactionService.addWalletTransaction(
                        wallet, WalletTransactionType.BUY_ASSET, request.getCoinId(), amount, WalletTransactionType.BUY_ASSET + " -> " + coin.getName()
                );
            }
            else{
                walletTransactionService.addWalletTransaction(
                        wallet, WalletTransactionType.SELL_ASSET, request.getCoinId(), amount, WalletTransactionType.SELL_ASSET + " -> " + coin.getName()
                );
            }



            return new ResponseEntity<>(order, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@RequestHeader("Authorization") String token, @PathVariable Long orderId){
        try {
            User user = userService.findUserProfileByJwt(token);
            Order order = orderService.getOrderById(orderId);

            if(order.getUser().getId() == user.getId()){
                return new ResponseEntity<>(order, HttpStatus.OK);
            }
            else{
               return new ResponseEntity<>("You dont't have access!", HttpStatus.FORBIDDEN);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllOrdersForUser(@RequestHeader("Authorization") String token){
        try {
            Long userId = userService.findUserProfileByJwt(token).getId();

            List<Order> userOrders = orderService.getAllOrdersOfUser(userId);

            return new ResponseEntity<>(userOrders, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
