package ru.trade.helper.trade.service.trade;

import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.OrderType;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.trade.helper.trade.account.FullAccessAccount;
import ru.trade.helper.trade.util.exception.NotEnoughMoneyException;

import java.util.List;

@Component
public class TradeServiceImpl implements TradeService {

    private PostOrderResponse postOrder(FullAccessAccount account,
                                        String figi,
                                        long count,
                                        long units,
                                        int nano,
                                        OrderDirection orderDirection,
                                        OrderType orderType) {
        return account.postOrder(figi, count, units, nano, orderDirection, orderType, null);
    }

    @Override
    public PostOrderResponse buy(FullAccessAccount account, String figi, long count, long units, int nano, OrderType orderType) {
        count = adjustQuantityForBUY(account, figi, count, units, nano);
        return postOrder(account, figi, count, units, nano, OrderDirection.ORDER_DIRECTION_BUY, orderType);
    }

    @Override
    public PostOrderResponse buyFast(FullAccessAccount account, String figi, long count) {
        count = adjustQuantityForBUY(account, figi, count, 0, 0);
        if (count == 0) {
            throw new NotEnoughMoneyException("Недостаточно средств на счете " + account.getName());
        }
        return postOrder(account, figi, count, 0, 0, OrderDirection.ORDER_DIRECTION_BUY, OrderType.ORDER_TYPE_MARKET);
    }

    @Override
    public List<PostOrderResponse> buySmart(FullAccessAccount account, String figi, long count, long units, int nano) {
        return null;
    }

    @Override
    public PostOrderResponse sell(FullAccessAccount account, String figi, long count, long units, int nano, OrderType orderType) {
        count = adjustQuantityForSELL(account, figi, count);
        if (count == 0) {
            return null;
        }
        return postOrder(account, figi, count, units, nano, OrderDirection.ORDER_DIRECTION_SELL, orderType);
    }

    @Override
    public PostOrderResponse sellFast(FullAccessAccount account, String figi, long count) {
        count = adjustQuantityForSELL(account, figi, count);
        if (count == 0) {
            return null;
        }
        return postOrder(account, figi, count, 0, 0, OrderDirection.ORDER_DIRECTION_SELL, OrderType.ORDER_TYPE_MARKET);

    }

    @Override
    public List<PostOrderResponse> sellSmart(FullAccessAccount account, String figi, long count, long units, int nano) {
        return null;
    }

    @Override
    public PostOrderResponse fastOrder(FullAccessAccount account, String figi, long count, String orderDirection) {
        if (orderDirection.equalsIgnoreCase("BUY")) {
            return buyFast(account, figi, count);
        } else if (orderDirection.equalsIgnoreCase("SELL")) {
            return sellFast(account, figi, count);
        }
        return null;
    }

    public Long adjustQuantityForSELL(FullAccessAccount account, String figi, long count) {
        Instrument instrument = account.getInstrumentByFigi(figi);
        int lot = instrument.getLot();
        long lotCount = count / lot;
        long availableLotCount = account.getInstrumentCountInPortfolio(figi) / lot;
        return Math.min(availableLotCount, lotCount);
    }

    public Long adjustQuantityForBUY(FullAccessAccount account, String figi, long count, long units, int nano) {
        Instrument instrument = account.getInstrumentByFigi(figi);
        int lot = instrument.getLot();
        double availableMoney = account.getAvailableMoney(instrument.getCurrency()) * (1 - account.getCOMMISSION());
        Double price = account.quotationToDouble(units, nano);
        if (price == 0) {
            price = account.getLastPrice(figi);
        }
        long lotCount = count / lot;
        long availableLotCount = (long) (availableMoney / (price * lot));
        return Math.min(availableLotCount, lotCount);
    }
}
