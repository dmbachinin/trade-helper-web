package ru.trade.helper.trade.account;

import ru.tinkoff.piapi.contract.v1.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

public class FullAccessAccountImpl extends FullAccessAccount {

    public FullAccessAccountImpl(String token) {
        super(token);
    }

    @Override
    public PostOrderResponse postOrder(String figi,
                                       long count,
                                       long units,
                                       int nano,
                                       OrderDirection orderDirection,
                                       OrderType orderType,
                                       String orderId) {
        Quotation price = Quotation.newBuilder()
                .setUnits(units)
                .setNano(nano)
                .build();

        try {
            return ordersService.postOrder(
                    figi,
                    count,
                    price,
                    orderDirection,
                    getId(),
                    orderType,
                    orderId).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<OrderState> getOrders() {
        try {
            return ordersService.getOrders(account.getId()).get();
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
