package com.example.SafeTradeX.Controller;

import com.example.SafeTradeX.Model.Asset;
import com.example.SafeTradeX.Model.User;
import com.example.SafeTradeX.Service.AssetService;
import com.example.SafeTradeX.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    @Autowired
    private AssetService assetService;

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<?> getAssetForUser(@RequestHeader("Authorization") String token){
        try {
            User user = userService.findUserProfileByJwt(token);
            List<Asset> assets = assetService.getUserAssets(user.getId());

            return new ResponseEntity<>(assets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/{assetId}")
    public ResponseEntity<?> getAssetById(@PathVariable Long assetId){
        try {
            Asset asset = assetService.getAssetById(assetId);

            return new ResponseEntity<>(asset, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/coin/{coinId}/user")
    public ResponseEntity<?> getAssetByUserIdAndCoinId(@PathVariable String coinId, @RequestHeader("Authorization") String token){
        try {
            User user = userService.findUserProfileByJwt(token);
            Asset asset = assetService.findAssetByUserIdAndCoinId(user.getId(), coinId);

            return new ResponseEntity(asset, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }
}
