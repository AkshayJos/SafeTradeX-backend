package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Model.Watchlist;
import com.example.SafeTradeX.Repository.WatchlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WatchlistServiceImpl implements WatchlistService {

    @Autowired
    private WatchlistRepository watchlistRepository;

    @Override
    public Watchlist findUserWatchlist(Long userId) throws Exception {
        Watchlist watchlist = watchlistRepository.findByUserId(userId);

        if(watchlist == null) throw new Exception("Watchlist not found!");

        return watchlist;
    }

    @Override
    public Watchlist createWatchlist(User user) {
        Watchlist watchlist = new Watchlist();
        watchlist.setUser(user);

        return watchlistRepository.save(watchlist);
    }

    @Override
    public Watchlist findById(Long id) throws Exception {
        return watchlistRepository.findById(id)
                .orElseThrow(() -> new Exception("Watchlist not found!"));
    }

    @Override
    public Coin addItemToWatchlist(Coin coin, User user) throws Exception {
        Watchlist watchlist = findUserWatchlist(user.getId());

        if(watchlist.getCoins().contains(coin)){
            watchlist.getCoins().remove(coin);
        }
        else{
            watchlist.getCoins().add(coin);
        }

        watchlistRepository.save(watchlist);

        return coin;
    }
}
