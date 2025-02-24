package ru.trade.helper.web.service.market;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.trade.helper.web.entity.Token;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.entity.Operation;
import ru.trade.helper.web.entity.Order;
import ru.trade.helper.web.model.SubscriberDto;
import ru.trade.helper.web.model.OperationDto;
import ru.trade.helper.web.service.account.TradeTokenManagerService;
import ru.trade.helper.web.service.notification.NotificationService;
import ru.trade.helper.web.service.order.OrderService;
import ru.trade.helper.web.service.order.mapper.OrderMapper;
import ru.trade.helper.web.service.user.UserService;
import ru.trade.helper.trade.account.FullAccessAccount;
import ru.trade.helper.trade.service.trade.TradeService;
import ru.trade.helper.trade.util.exception.NotEnoughMoneyException;

@Component
@Slf4j
@RequiredArgsConstructor
public class MarketEventProcessor {

    private final TradeTokenManagerService accountManagerService;
    private final TradeService tradeService;
    private final OrderService orderService;
    private final UserService userService;
    private final OrderMapper orderMapping;
    private final NotificationService notificationService;

    @Async
    @Retryable(value = {RuntimeException.class}, maxAttempts = 10, backoff = @Backoff(delay = 1000))
    @Transactional
    public void eventProcessing(SubscriberDto subDto, OperationDto operation) {
        if (subDto.getStatus().equals(SubscriberDto.Status.ACTIVE)) {
            FullAccessAccount account = accountManagerService.getFullAccessAccount(subDto.getTokenId());
            PostOrderResponse tOrder = null;
            try {
                switch (operation.getOperationType()) {
                    case BUY ->
                            tOrder = tradeService.buyFast(account, operation.getFigi(), operation.getQuantity() * subDto.getMultiplier());
                    case SELL ->
                            tOrder = tradeService.sellFast(account, operation.getFigi(), operation.getQuantity() * subDto.getMultiplier());
                }
            } catch (NotEnoughMoneyException e) {
                notificationService.notify(subDto, getNotEnoughMoneyExceptionMessage(e));
            } catch (Exception e) {
                LOG.error("Ошибка при совершении сделки: {}", e.getMessage());
                throw new RuntimeException(e);
            }
            if (tOrder != null) {
                User user = userService.findById(subDto.getUserId()).orElseThrow();
                Order order = orderMapping.toBack(tOrder, account, user);
                LOG.info("Совершена сделка: {}", order);
                orderService.save(order);
                notificationService.notify(order.getUser(), getFindOrderMessage(order));
            }
        }
    }

    private String getFindOrderMessage(Order order) {
        return String.format("Совершена сделка\uD83D\uDCB0\n\n%s", order.toLongString());
    }

    private String getNotEnoughMoneyExceptionMessage(Exception e) {
        return String.format("Неудачная попытка совершить сделку\n\n%s", e.getMessage());
    }

    @Recover
    public void recover(RuntimeException e, Token token, Operation operation) {
        notificationService.notify(token.getUser(), String.format("""
                Не удалось совершить сделку
                                
                Токен:%s
                Операция:%s
                Текст ошибки:%s
                """, token, operation, e.getMessage()));
        LOG.error("Ну удалось совершить сделку {}", operation);
    }
}
