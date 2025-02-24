package ru.trade.helper.web.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.trade.helper.web.entity.Operation;
import ru.trade.helper.web.entity.Strategy;
import ru.trade.helper.web.entity.Token;
import ru.trade.helper.web.model.MarketEventData;
import ru.trade.helper.web.model.TradeTokenManagerKey;
import ru.trade.helper.web.repository.StrategyRepository;
import ru.trade.helper.web.service.market.MarketService;
import ru.trade.helper.web.service.notification.NotificationService;
import ru.trade.helper.web.service.operation.OperationService;
import ru.trade.helper.web.service.operation.mapping.OperationMapping;
import ru.trade.helper.web.service.subscription.mapper.SubscriptionMapper;
import ru.trade.helper.trade.account.ReadonlyAccount;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionEventProcessor {

    private final OperationMapping operationMapping;
    private final MarketService marketService;
    private final StrategyRepository strategyRepository;
    private final OperationService operationService;
    private final NotificationService notificationService;
    private final SubscriptionMapper subscriptionMapper;

    @Value("${tracking-transaction.scheduler.fix-rate}")
    private Long fixedRate;

    @Async
    @Retryable(value = {RuntimeException.class}, maxAttempts = 10, backoff = @Backoff(delay = 900))
    public void checkTransactions(Map.Entry<TradeTokenManagerKey, ReadonlyAccount> observerEntry) {
        ReadonlyAccount account = observerEntry.getValue();
        List<ru.tinkoff.piapi.contract.v1.Operation> tOperations;
        try {
            tOperations = account.getAllOperation(fixedRate * 10);
        } catch (Exception e) {
            LOG.error("Ошибка при получении списка операций: {}", e.getMessage());
            throw new RuntimeException(e);
        }
        if (!tOperations.isEmpty()) {
            LOG.info("На счете {} обнаружены следующие операции: {}", account.getName(), tOperations);
            Strategy strategy = strategyRepository.findByTokenId(observerEntry.getKey().getTokenId()).orElseThrow();
            List<Operation> operations = operationMapping.toBack(tOperations, account, strategy.getAuthor());
            for (Operation operation : operations) {
                LOG.info("Замечена операция {}", operation);
                if (!operation.getOperationType().equals(Operation.OperationType.OTHER)) {
                    try {
                        operationService.save(operation);
                        var subscribers = strategy.getSubscriptions();
                        marketService.addToQueue(MarketEventData.builder()
                                .buyers(subscriptionMapper.toDto(subscribers))
                                .operation(operationMapping.toDto(operation))
                                .build());

                       notificationService.notify(strategy, getfindOperationString(operation));

                    } catch (IllegalStateException ignore) {
                    } catch (Exception e) {
                        operationService.delete(operation);
                        LOG.error("Ошибка при выполнении запроса: {}", e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    private String getfindOperationString(Operation operation) {
        return String.format("Замечена операция\uD83D\uDCDD\n\n%s", operation.toLongString());
    }

    @Recover
    public void recover(RuntimeException e, Map.Entry<TradeTokenManagerKey, ReadonlyAccount> observerEntry) {
        LOG.error("Ну удалось проверить наличие транзакций для {} {}", observerEntry.getKey(), observerEntry.getValue());
    }
}
