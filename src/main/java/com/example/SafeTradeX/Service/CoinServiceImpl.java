package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Repository.CoinRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class CoinServiceImpl implements CoinService {

    @Autowired
    private CoinRepository coinRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String[][] proxies = {
            //webshare.io

//            {"198.23.239.134", "6540", "kdwhsqrs", "8q6ash79z7ru"},         //akshayjoshi.x@gmail.com
//            {"207.244.217.165", "6712", "kdwhsqrs", "8q6ash79z7ru"},
//            {"107.172.163.27", "6543", "kdwhsqrs", "8q6ash79z7ru"},
//            {"23.94.138.75", "6349", "kdwhsqrs", "8q6ash79z7ru"},
//            {"216.10.27.159", "6837", "kdwhsqrs", "8q6ash79z7ru"},
//            {"136.0.207.84", "6661", "kdwhsqrs", "8q6ash79z7ru"},
//            {"64.64.118.149", "6732", "kdwhsqrs", "8q6ash79z7ru"},
//            {"142.147.128.93", "6593", "kdwhsqrs", "8q6ash79z7ru"},
//            {"104.239.105.125", "6655", "kdwhsqrs", "8q6ash79z7ru"},
//            {"173.0.9.70", "5653", "kdwhsqrs", "8q6ash79z7ru"},
//
            {"198.23.239.134", "6540", "mvobgwtv", "8jtpdnlti05p"},         //akshayjoshi.practice@gmail.com
            {"207.244.217.165", "6712", "mvobgwtv", "8jtpdnlti05p"},
            {"198.23.239.134", "6540", "qavbrtmi", "4kico666xvwl"},          //blogvista.contact@gmail.com
            {"207.244.217.165", "6712", "qavbrtmi", "4kico666xvwl"},
            {"107.172.163.27", "6543", "qavbrtmi", "4kico666xvwl"},
            {"23.94.138.75", "6349", "mvobgwtv", "8jtpdnlti05p"},
            {"107.172.163.27", "6543", "mvobgwtv", "8jtpdnlti05p"},
            {"23.94.138.75", "6349", "qavbrtmi", "4kico666xvwl"},
            {"216.10.27.159", "6837", "mvobgwtv", "8jtpdnlti05p"},
            {"136.0.207.84", "6661", "mvobgwtv", "8jtpdnlti05p"},
            {"216.10.27.159", "6837", "qavbrtmi", "4kico666xvwl"},
            {"136.0.207.84", "6661", "qavbrtmi", "4kico666xvwl"},
            {"64.64.118.149", "6732", "mvobgwtv", "8jtpdnlti05p"},
            {"142.147.128.93", "6593", "mvobgwtv", "8jtpdnlti05p"},
            {"64.64.118.149", "6732", "qavbrtmi", "4kico666xvwl"},
            {"142.147.128.93", "6593", "qavbrtmi", "4kico666xvwl"},
            {"104.239.105.125", "6655", "qavbrtmi", "4kico666xvwl"},
            {"173.0.9.70", "5653", "qavbrtmi", "4kico666xvwl"},
            {"104.239.105.125", "6655", "mvobgwtv", "8jtpdnlti05p"},
            {"173.0.9.70", "5653", "mvobgwtv", "8jtpdnlti05p"},

            // https://free-proxy-list.net/ (free usable proxies with bandwith limit)

//            {"51.81.245.3", "17981", "", ""},  // 0
//            {"161.35.98.111", "8080", "", ""},  // 1
//            {"185.234.65.66", "1080", "", ""},  // 2
////            {"167.86.127.219", "8888", "", ""}, //3       //
////            {"57.129.81.201", "8080", "", ""}, //4        //
//            {"103.254.164.6", "3128", "", ""}, //5
////            {"194.170.146.125", "8080", "", ""}, //6       //
////            {"159.69.57.20", "8880", "", ""},//7           //
//            {"57.129.81.201", "8080", "", ""},//8
//            {"38.250.126.201", "999", "", ""},//9
//            {"161.35.98.111", "8080", "", ""}, //10
//            {"81.22.132.94", "15182", "", ""}, //11
////            {"91.219.63.72", "4442", "", ""},  //12         //

    };

    private static final AtomicInteger proxyIndex = new AtomicInteger(0);

    private static int getNextProxyIndex() {
        return proxyIndex.getAndUpdate(i -> (i + 1) % proxies.length);
    }

    private Executor getExecutorWithRotatingProxy() throws Exception {
        int index = getNextProxyIndex();
        String[] proxyData = proxies[index];

        String proxyHost = proxyData[0];
        int proxyPort = Integer.parseInt(proxyData[1]);
        String username = proxyData[2];
        String password = proxyData[3];

        HttpHost proxy = new HttpHost(proxyHost, proxyPort);
        CredentialsProvider credsProvider = new BasicCredentialsProvider();
        credsProvider.setCredentials(new AuthScope(proxy), new UsernamePasswordCredentials(username, password));

        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setProxy(proxy)
                .setDefaultCredentialsProvider(credsProvider)
                .build();

        return Executor.newInstance(httpClient);
    }

    private String sendGetRequest(String url) throws Exception {
        try {
            return getExecutorWithRotatingProxy()
                    .execute(Request.Get(url)
                            .addHeader("accept", "application/json")
                            .connectTimeout(5000)
                            .socketTimeout(5000))
                    .returnContent().asString();
        } catch (Exception e) {
            System.out.println(proxyIndex);
            System.out.println(e.getMessage());
            throw new Exception("You have crossed the rate limit. Please wait a while!", e);
        }
    }

    @Override
    public List<Coin> getCoinList(int page) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=10&page=" + page;
        String json = sendGetRequest(url);
        return objectMapper.readValue(json, new TypeReference<List<Coin>>() {});
    }

    @Override
    public String getMarketChart(String coinId, int days) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId + "/market_chart?vs_currency=usd&days=" + days;
        return sendGetRequest(url);
    }

    @Override
    public String getCoinDetails(String coinId) throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/" + coinId;
        String jsonResponse = sendGetRequest(url);

        JsonNode jsonNode = objectMapper.readTree(jsonResponse);
        Coin coin = new Coin();
        coin.setId(jsonNode.get("id").asText());
        coin.setName(jsonNode.get("name").asText());
        coin.setSymbol(jsonNode.get("symbol").asText());
        coin.setImage(jsonNode.get("image").get("large").asText());

        JsonNode marketData = jsonNode.get("market_data");
        coin.setCurrentPrice(marketData.get("current_price").get("usd").asDouble());
        coin.setMarketCap(marketData.get("market_cap").get("usd").asLong());
        coin.setMarketCapRank(marketData.get("market_cap_rank").asInt());
        coin.setTotalVolume(marketData.get("total_volume").get("usd").asLong());
        coin.setHigh24h(marketData.get("high_24h").get("usd").asDouble());
        coin.setLow24h(marketData.get("low_24h").get("usd").asDouble());
        coin.setPriceChange24h(marketData.get("price_change_24h").asDouble());
        coin.setPriceChangePercentage24h(marketData.get("price_change_percentage_24h").asDouble());
        coin.setMarketCapChange24h(marketData.get("market_cap_change_24h").asLong());
        coin.setMarketCapChangePercentage24h(marketData.get("market_cap_change_percentage_24h").asDouble());
        coin.setTotalSupply(marketData.get("total_supply").asDouble());

        coinRepository.save(coin);
        return jsonResponse;
    }

    @Override
    public Coin findById(String coinId) throws Exception {
        return coinRepository.findById(coinId)
                .orElseThrow(() -> new Exception("Coin not found with coinId " + coinId));
    }

    @Override
    public String searchCoin(String keyword) throws Exception {
        String url = "https://api.coingecko.com/api/v3/search?query=" + keyword;
        return sendGetRequest(url);
    }

    @Override
    public String getTop50CoinsByMarketCapRank() throws Exception {
        String url = "https://api.coingecko.com/api/v3/coins/markets?vs_currency=usd&per_page=50&page=1";
        return sendGetRequest(url);
    }

    @Override
    public String getTrendingCoins() throws Exception {
        String url = "https://api.coingecko.com/api/v3/search/trending";
        return sendGetRequest(url);
    }
}