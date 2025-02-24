package ru.trade.helper.trade.account;

import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderState;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;

import java.util.List;

public abstract class FullAccessAccount extends Account {
    public FullAccessAccount(String token) {
        super(token, false);
    }

    public abstract PostOrderResponse postOrder(String figi,
                                                long count,
                                                long units,
                                                int nano,
                                                OrderDirection orderDirection,
                                                OrderType orderType,
                                                String orderId);

    public abstract List<OrderState> getOrders();
}
