package ru.trade.helper.web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Entity
@Table(name = "operation_history")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Operation {

    @Id
    @Column(name = "id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_name", length = 150)
    private String accountName;

    @Column(name = "account_id", length = 50)
    private String accountId;

    @Enumerated(EnumType.STRING)
    @Column(name = "operation_type")
    private OperationType operationType;

    @Column(name = "instrument_ticker", length = 50)
    private String instrumentTicker;

    @Column(name = "figi", length = 12)
    private String figi;

    @Column(name = "instrument_uid", length = 50)
    private String instrumentUid;

    @Column(name = "instrument_type", length = 50)
    private String instrumentType;

    @Column(name = "quantity")
    private Long quantity;

    @Column(name = "price_units")
    private Long priceUnits;

    @Column(name = "price_nano")
    private Integer priceNano;

    @Column(name = "currency", length = 12)
    private String currency;

    @Column(name = "date", nullable = false)
    private Timestamp date;

    @Override
    public String toString() {
        return String.format("Операция: id=%s; Tикер=%s; Кол-во=%d; Тип=%s;",
                id, instrumentTicker, quantity, operationType);
    }

    public String toLongString() {
        return String.format("""
                Счет: %s
                Tип операции: %s
                Тикер актива: %s
                Количество: %d
                Сумма сделки: %f руб.
                """, accountName, operationType, instrumentTicker, quantity, getTotalPrice());
    }


    public Double getTotalPrice() {
        long rubles = Math.abs(priceUnits);
        double pennies = Math.abs(priceNano) / 1e9;
        return (rubles + pennies) * quantity;
    }

    public Double getInstrumentPrice() {
        long rubles = Math.abs(priceUnits);
        double pennies = Math.abs(priceNano) / 1e9;
        return rubles + pennies;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(date);
    }

    public enum OperationType {
        BUY,
        SELL,
        OTHER
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Operation operation = (Operation) object;
        return Objects.equals(id, operation.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
