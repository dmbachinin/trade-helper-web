package ru.trade.helper.web.service.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.trade.helper.web.entity.Strategy;
import ru.trade.helper.web.entity.Subscription;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.model.SubscriberDto;
import ru.trade.helper.web.telegram.TradeHelperBot;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final TradeHelperBot bot;

    public void notify(User user, String message) {
        if (user.getIsTelegramNotification()) {
            bot.sendMessageToUser(user.getChatId(), message);
        }
    }

    public void notify(Strategy strategy, String message) {
        strategy.getSubscriptions().stream()
                .filter(sub -> sub.getToken().getIsEnable() && sub.getToken().getIsValid())
                .map(Subscription::getToken)
                .collect(Collectors.toSet())
                .forEach(token -> notify(token.getUser(), message));
    }

    public void notify(SubscriberDto subDto, String message) {
        if (subDto.getIsTelegramNotification()) {
            bot.sendMessageToUser(subDto.getChatId(), message);
        }
    }
}
