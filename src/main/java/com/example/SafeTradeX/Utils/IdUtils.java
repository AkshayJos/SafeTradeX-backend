package com.example.SafeTradeX.Utils;

public class IdUtils {
    public String generateWalletId() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder id = new StringBuilder("#");
        for (int i = 0; i < 8; i++) {
            int randIndex = (int) (Math.random() * chars.length());
            id.append(chars.charAt(randIndex));
        }
        return id.toString();
    }
}
