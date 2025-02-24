package ru.trade.helper.web.entity;

import jakarta.persistence.*;
import lombok.*;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Objects;

@Entity
@Table(name = "order_history")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Order {

    @Id
    @Column(name = "id", length = 50)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "account_id", length = 50)
    private String accountId;

    @Column(name = "account_name", length = 150)
    private String accountName;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", length = 50)
    private OrderDirection direction;

    @Column(name = "instrument_ticker", length = 50)
    private String instrumentTicker;

    @Column(name = "instrument_figi", length = 12)
    private String figi;

    @Column(name = "lots_executed")
    private Long lotsExecuted;

    @Column(name = "commission_units")
    private Long commissionUnits;

    @Column(name = "commission_nano")
    private Integer commissionNano;

    @Column(name = "order_price_units")
    private Long orderPriceUnits;

    @Column(name = "order_price_nano")
    private Integer orderPriceNano;

    @Column(name = "total_order_amount_units")
    private Long totalOrderAmountUnits;

    @Column(name = "total_order_amount_nano")
    private Integer totalOrderAmountNano;

    @Column(name = "order_type", length = 50)
    private String orderType;

    @Column(name = "create_date", nullable = false)
    private Timestamp createDate;

    public enum OrderDirection {
        BUY,
        SELL,
        UNKNOWN
    }

    @Override
    public String toString() {
        return String.format("Операция: id=%s; Tикер=%s; Кол-во=%d; Тип=%s;",
                id, instrumentTicker, lotsExecuted, direction);
    }

    public String toLongString() {
        return String.format("""
                Счет: %s
                Tип операции: %s
                Тикер актива: %s
                Количество лотов: %d
                Сумма сделки: %f руб.
                """, accountName, direction, instrumentTicker, lotsExecuted, getTotalPrice());
    }

    public Double getTotalPrice() {
        long rubles = Math.abs(totalOrderAmountUnits);
        double pennies = Math.abs(totalOrderAmountNano) / 1e9;
        return rubles + pennies;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
        return sdf.format(createDate);
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Order order = (Order) object;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}

