package com.example.SafeTradeX.Service;

import com.example.SafeTradeX.Model.Asset;
import com.example.SafeTradeX.Model.Coin;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Repository.AssetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssetServiceImpl implements AssetService{

    @Autowired
    private AssetRepository assetRepository;

    @Override
    public Asset createAsset(User user, Coin coin, Double quantity) {
        Asset asset = new Asset();
        asset.setUser(user);
        asset.setQuantity(quantity);
        asset.setCoin(coin);
        asset.setBuyPrice(coin.getCurrentPrice());

        return assetRepository.save(asset);
    }

    @Override
    public Asset getAssetById(Long id) throws Exception {
        return assetRepository.findById(id)
                .orElseThrow(() -> new Exception("Asset not found!"));
    }

    @Override
    public Asset getAssetByUserIdAndAssetId(Long userId, Long assetId) {
        return null;
    }

    @Override
    public List<Asset> getUserAssets(Long userId) {
        return assetRepository.findByUserId(userId);
    }

    @Override
    public Asset updateAsset(Long assetId, Double quantity) throws Exception {
        Asset oldAsset = getAssetById(assetId);
        oldAsset.setQuantity(quantity + oldAsset.getQuantity());

        return assetRepository.save(oldAsset);
    }

    @Override
    public Asset findAssetByUserIdAndCoinId(Long userId, String coinId) {
        return assetRepository.findByUserIdAndCoinId(userId, coinId);
    }

    @Override
    public void deleteAsset(Long assetId) {
        assetRepository.deleteById(assetId);
    }
}
