package ru.trade.helper.web.model;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
public class TradeTokenManagerKey {
    private Long tokenId;
    // Количество стратегий, которые используют токен
    private Long count;

    public TradeTokenManagerKey(Long tokenId) {
        this.tokenId = tokenId;
        this.count = 1L;
    }

    public void addOne() {
        count++;
    }

    public void minusOne() {
        count--;
    }

    public boolean isShouldDelete() {
        return  count == 0;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TradeTokenManagerKey that = (TradeTokenManagerKey) object;
        return Objects.equals(tokenId, that.tokenId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenId);
    }

    @Override
    public String toString() {
        return "TradeTokenManagerKey{" +
                "tokenId=" + tokenId +
                ", count=" + count +
                '}';
    }
}
