package com.example.SafeTradeX.Model;

import com.example.SafeTradeX.Domain.VerificationType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class TwoFactorAuth {
    private boolean isEnable = false;

    @Enumerated(EnumType.STRING)
    private VerificationType sendTo;

    public VerificationType getSendTo() {
        return sendTo;
    }

    public void setSendTo(VerificationType sendTo) {
        this.sendTo = sendTo;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }
}
