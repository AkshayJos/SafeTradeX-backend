package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Service.CoinService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/coins")
public class CoinController {

    @Autowired
    private CoinService coinService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<?> getCoins(@RequestParam("page") int page){
        try {
            List<Coin> coins = coinService.getCoinList(page);

            return new ResponseEntity<>(coins, HttpStatus.OK);
        }catch (Exception e){
            System.out.println(e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/{coinId}/chart")
    public ResponseEntity<?> getMarketChart(@PathVariable String coinId, @RequestParam("days") int days){
        try {
            String response = coinService.getMarketChart(coinId, days);
            JsonNode jsonNode = objectMapper.readTree(response);

            return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        }catch (Exception e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchCoin(@RequestParam("q") String keyword){
        try {
            String coin = coinService.searchCoin(keyword);
            JsonNode jsonNode = objectMapper.readTree(coin);

            return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/top50")
    public ResponseEntity<?> getTop50CoinByMarketCapRank(){
        try {
            String coin = coinService.getTop50CoinsByMarketCapRank();
            JsonNode jsonNode = objectMapper.readTree(coin);

            return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        } catch (Exception e) {
           return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

        @GetMapping("/trending")
    public ResponseEntity<?> getTrendingCoins(){
        try {
           String coin = coinService.getTrendingCoins();
           JsonNode jsonNode = objectMapper.readTree(coin);

           return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS);
        }
    }

    @GetMapping("/details/{coinId}")
    public ResponseEntity<?> getCoinDetails(@PathVariable String coinId){
        try {
            String coin = coinService.getCoinDetails(coinId);
            JsonNode jsonNode = objectMapper.readTree(coin);

            return new ResponseEntity<>(jsonNode, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.TOO_MANY_REQUESTS    );
        }
    }
}
