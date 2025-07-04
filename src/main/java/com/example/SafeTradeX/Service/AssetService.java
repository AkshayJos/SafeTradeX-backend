package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.Asset;
import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Model.User;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.List;

public interface AssetService {

    Asset createAsset(User user, Coin coin, Double quantity);

    Asset getAssetById(Long id) throws Exception;

    Asset getAssetByUserIdAndAssetId(Long userId, Long assetId);

    List<Asset> getUserAssets(Long userId);

    Asset updateAsset(Long assetId, Double quantity) throws Exception;

    Asset findAssetByUserIdAndCoinId(Long userId, String coinId);

    void deleteAsset(Long assetId);
}
