package com.example.SafeTradeX.Repository;

import com.example.SafeTradeX.Model.Coin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CoinRepository extends JpaRepository<Coin, String> {
}
