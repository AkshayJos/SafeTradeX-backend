package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Watchlist;

public interface WatchlistService {

    Watchlist findUserWatchlist(Long userId) throws Exception;

    Watchlist createWatchlist(User user);

    Watchlist findById(Long id) throws Exception;

    Coin addItemToWatchlist(Coin coin, User user) throws Exception;


}
