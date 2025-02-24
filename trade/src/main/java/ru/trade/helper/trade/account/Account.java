package ru.trade.helper.trade.account;

import lombok.Getter;
import lombok.Setter;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.LastPrice;
import ru.tinkoff.piapi.contract.v1.Quotation;
import ru.tinkoff.piapi.core.*;
import ru.tinkoff.piapi.core.models.Money;
import ru.tinkoff.piapi.core.models.Portfolio;
import ru.tinkoff.piapi.core.models.Position;
import ru.tinkoff.piapi.core.models.WithdrawLimits;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Getter
public abstract class Account implements AutoCloseable {
    @Setter
    protected String name;
    protected final InvestApi client;
    protected final ru.tinkoff.piapi.contract.v1.Account account;
    protected final OperationsService operationService;
    protected final OrdersService ordersService;
    protected final InstrumentsService instrumentsService;
    protected final MarketDataService marketDataService;
    protected final Double COMMISSION = 0.01;
    protected final Executor delayedExecutor = CompletableFuture.delayedExecutor(10, TimeUnit.SECONDS);

    public Account(String token, boolean isReadOnly) {
        try {
            if (isReadOnly) {
                client = InvestApi.createReadonly(token);
            } else {
                client = InvestApi.create(token);
            }
            account = client.getUserService().getAccounts().get().get(0);
            this.name = account.getName();
            operationService = client.getOperationsService();
            instrumentsService = client.getInstrumentsService();
            ordersService = client.getOrdersService();
            marketDataService = client.getMarketDataService();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public boolean isReadonlyMode() {
        return client.isReadonlyMode();
    }

    public boolean isSandboxMode() {
        return client.isSandboxMode();
    }

    public String getId() {
        return account.getId();
    }

    public ru.tinkoff.piapi.contract.v1.Account getAccounts() {
        return account;
    }

    @Override
    public String toString() {
        return name;
    }

    public OrdersService getOrdersStreamService() {
        return ordersService;
    }


    public Instrument getInstrumentByFigi(String figi) {
        Instrument instrument = null;
        try {
            instrument = instrumentsService.getInstrumentByFigi(figi).get();
        } catch (ExecutionException | InterruptedException ignore) {
        }
        return instrument;

    }

    public Long getInstrumentCountInPortfolio(String figi) {
        long instrumentCount = 0L;
        try {
            Portfolio portfolio = operationService.getPortfolio(getId()).get();
            List<Position> positions = portfolio.getPositions();
            for (Position position : positions) {
                if (position.getFigi().equalsIgnoreCase(figi)) {
                    instrumentCount = position.getQuantity().longValue();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return instrumentCount;
    }

    public Long getAvailableMoney(String currency) {
        try {
            WithdrawLimits withdrawLimits = operationService.getWithdrawLimits(getId()).get();
            for (Money money : withdrawLimits.getMoney()) {
                if (money.getCurrency().equalsIgnoreCase(currency)) {
                    return money.getValue().longValue();
                }
            }
        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return 0L;
    }

    public Double getLastPrice(String figi) {
        try {
            List<LastPrice> lastPrices = marketDataService.getLastPrices(Collections.singleton(figi)).get();
            if (!lastPrices.isEmpty()) {
                LastPrice lastPrice = lastPrices.get(0);
                return quotationToDouble(lastPrice.getPrice());
            }

        } catch (ExecutionException | InterruptedException e) {
            throw new RuntimeException(e);
        }
        return Double.MAX_VALUE;
    }

    public Double quotationToDouble(Quotation quotation) {
        long rubles = Math.abs(quotation.getUnits());
        double pennies = Math.abs(quotation.getNano()) / 1e9;
        return (rubles + pennies);
    }

    public Double quotationToDouble(long units, int nano) {
        long rubles = Math.abs(units);
        double pennies = Math.abs(nano) / 1e9;
        return (rubles + pennies);
    }

    @Override
    public void close() {
        CompletableFuture.runAsync(() -> {
            client.destroy(3);
        }, delayedExecutor);
    }
}
