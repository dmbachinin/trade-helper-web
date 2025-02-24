package ru.trade.helper.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SubscriberDto {
    private Long tokenId;
    private Long userId;
    private Long chatId;
    private Boolean isTelegramNotification;
    private Integer multiplier;
    private Type type;
    private Status status;

    public enum Type {
        MULTIPLIER,
        PERCENT
    }

    public enum Status {
        PAUSE,
        ACTIVE
    }

    @Override
    public String toString() {
        return "SubscriberDto{" +
                "tokenId=" + tokenId +
                ", userId=" + userId +
                ", chatId=" + chatId +
                ", isTelegramNotification=" + isTelegramNotification +
                ", multiplier=" + multiplier +
                ", status=" + status +
                ", type=" + type +
                '}';
    }
}
