package ru.trade.helper.web.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class OperationDto {

    private String id;
    private Type operationType;
    private String figi;
    private Long quantity;
    private Long priceUnits;
    private Integer priceNano;
    private String currency;

    public enum Type {
        BUY,
        SELL,
        OTHER
    }

    @Override
    public String toString() {
        return "OperationDto{" +
                "id='" + id + '\'' +
                ", operationType=" + operationType +
                ", figi='" + figi + '\'' +
                ", quantity=" + quantity +
                ", priceUnits=" + priceUnits +
                ", priceNano=" + priceNano +
                ", currency='" + currency + '\'' +
                '}';
    }
}
