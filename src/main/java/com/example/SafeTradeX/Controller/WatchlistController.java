package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Watchlist;
import com.example.SafeTradeX.Service.CoinService;
import com.example.SafeTradeX.Service.UserService;
import com.example.SafeTradeX.Service.WatchlistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/watchlist")
public class WatchlistController {

    @Autowired
    private WatchlistService watchlistService;

    @Autowired
    private UserService userService;

    @Autowired
    private CoinService coinService;

    @GetMapping("/user")
    public ResponseEntity<?> getUserWatchlist(@RequestHeader("Authorization") String token){
        try {
            User user = userService.findUserProfileByJwt(token);

            Watchlist watchlist = watchlistService.findUserWatchlist(user.getId());

            return new ResponseEntity<>(watchlist, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{watchlistId}")
    public ResponseEntity<?> getWatchlistById(@PathVariable Long watchlistId){
        try {
            Watchlist watchlist = watchlistService.findById(watchlistId);

            return new ResponseEntity<>(watchlist, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PatchMapping("/add/coin/{coinId}")
    public ResponseEntity<?> addItemToWatchlist(@RequestHeader("Authorization") String token, @PathVariable String coinId){
        try {
            User user = userService.findUserProfileByJwt(token);
            Coin coin = coinService.findById(coinId);

            Coin addedCoin = watchlistService.addItemToWatchlist(coin, user);

            return new ResponseEntity<>(addedCoin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
