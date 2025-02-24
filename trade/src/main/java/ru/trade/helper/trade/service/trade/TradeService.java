package ru.trade.helper.trade.service.trade;

import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.trade.helper.trade.account.FullAccessAccount;

import java.util.List;

public interface TradeService {
    PostOrderResponse buy(FullAccessAccount account, String figi, long count, long units, int nano, OrderType orderType);

    PostOrderResponse buyFast(FullAccessAccount account, String figi, long count);

    List<PostOrderResponse> buySmart(FullAccessAccount account, String figi, long count, long units, int nano);

    PostOrderResponse sell(FullAccessAccount account, String figi, long count, long units, int nano, OrderType orderType);

    PostOrderResponse sellFast(FullAccessAccount account, String figi, long count);

    List<PostOrderResponse> sellSmart(FullAccessAccount account, String figi, long count, long units, int nano);

    PostOrderResponse fastOrder(FullAccessAccount account, String figi, long count, String orderDirection);
}
