package ru.trade.helper.web.service.account;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.trade.helper.trade.account.FullAccessAccount;
import ru.trade.helper.trade.account.FullAccessAccountImpl;
import ru.trade.helper.trade.account.ReadonlyAccount;
import ru.trade.helper.trade.account.ReadonlyAccountImpl;
import ru.trade.helper.web.entity.Strategy;
import ru.trade.helper.web.entity.Subscription;
import ru.trade.helper.web.entity.Token;
import ru.trade.helper.web.model.TradeTokenManagerKey;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class TradeTokenManagerService {

    private final Map<TradeTokenManagerKey, ReadonlyAccount> observersAccounts = new ConcurrentHashMap<>();
    private final Map<TradeTokenManagerKey, FullAccessAccount> buyersAccounts = new ConcurrentHashMap<>();

    @Transactional
    public void addToken(Token token) {
        if (token.getId() == null) {
            throw new IllegalArgumentException("Токен должен быть сохранен перед инициализацией");
        }
        TradeTokenManagerKey key = getKey(token);
        if (key != null) {
            key.addOne();
            LOG.info("Добавлен токен без инициализации {}", key);
            return;
        }
        try {
            if (token.getType().equals(Token.Type.FULL_ACCESS)) {
                ReadonlyAccount account = new ReadonlyAccountImpl(token.getToken());
                token.setAccountName(account.getName());
                observersAccounts.put(new TradeTokenManagerKey(token.getId()), account);
                LOG.info("Инициализация readonly токена {} прошла успешно", token);
            } else if (token.getType().equals(Token.Type.READONLY)) {
                FullAccessAccount account = new FullAccessAccountImpl(token.getToken());
                token.setAccountName(account.getName());
                buyersAccounts.put(new TradeTokenManagerKey(token.getId()), account);
                LOG.info("Инициализация fullAccess токена {} прошла успешно", token);
            } else {
                LOG.error("Попытка инициализировать токен, который не имеет тип {}", token);
            }
        } catch (Exception e) {
            LOG.info("Ошибка инициализации токена {} ", token);
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void turnOnStrategy(Strategy strategy) {
        Token rootToken = strategy.getToken();
        List<Token> subToken = strategy.getSubscriptions().stream()
                .map(Subscription::getToken)
                .filter(t -> t.getIsEnable() && t.getIsValid())
                .toList();
        if (rootToken.getIsEnable() && rootToken.getIsValid() && !subToken.isEmpty()) {
            addToken(rootToken);
            subToken.forEach(this::addToken);
        }
    }

    @Transactional
    public void turnOffStrategy(Strategy strategy) {
        Token rootToken = strategy.getToken();
        List<Token> subToken = strategy.getSubscriptions().stream()
                .map(Subscription::getToken)
                .toList();
        removeToken(rootToken);
        subToken.forEach(this::removeToken);
    }

    public void removeToken(Token token) {
        TradeTokenManagerKey key = getKey(token);
        if (key != null) {
            key.minusOne();
            LOG.info("Удален токен без удаления соединения {}", key);
            if (key.isShouldDelete()) {
                if (token.getType().equals(Token.Type.FULL_ACCESS)) {
                    FullAccessAccount account = buyersAccounts.remove(key);
                    account.close();
                    LOG.info("Удаление fullAccess токена {}", token);
                } else if (token.getType().equals(Token.Type.READONLY)) {
                    ReadonlyAccount account = observersAccounts.remove(key);
                    account.close();
                    LOG.info("Удаление readonly токена с {}", token);
                }
            }
        }

    }

    public ReadonlyAccount getReadonlyAccount(Long tokenId) {
        return observersAccounts.get(new TradeTokenManagerKey(tokenId));
    }


    public FullAccessAccount getFullAccessAccount(Long tokenId) {
        return buyersAccounts.get(new TradeTokenManagerKey(tokenId));
    }


    public Set<Map.Entry<TradeTokenManagerKey, ReadonlyAccount>> getObserverEntries() {
        return observersAccounts.entrySet();
    }


    public Set<Long> getBuyersId() {
        return buyersAccounts.keySet().stream()
                .map(TradeTokenManagerKey::getTokenId)
                .collect(Collectors.toSet());
    }


    public Set<Long> getObserversId() {
        return observersAccounts.keySet().stream()
                .map(TradeTokenManagerKey::getTokenId)
                .collect(Collectors.toSet());
    }

    @Transactional
    public void updateToken(Token token) {
        TradeTokenManagerKey key = getKey(token);
        if (key == null && token.getIsValid() && token.getIsEnable()) {
            for (int i = 0; i < token.getSubscriptions().size(); i++) {
                addToken(token);
            }
        } else if (key != null && (!token.getIsValid() || !token.getIsEnable())) {
            for (int i = 0; i < token.getSubscriptions().size(); i++) {
                removeToken(token);
            }
        }
    }

    @Transactional
    public void init(List<Strategy> strategies) {
        strategies.forEach(this::turnOnStrategy);
    }

    @Transactional
    public void clean(List<Strategy> strategies) {
        strategies.forEach(this::turnOffStrategy);
    }

    @Transactional
    public void restart(List<Strategy> strategies) {
        clean(strategies);
        init(strategies);
    }

    private TradeTokenManagerKey getKey(Token token) {
        if (token.getType().equals(Token.Type.FULL_ACCESS)) {
            return buyersAccounts.keySet()
                    .stream()
                    .filter(k -> k.getTokenId().equals(token.getId()))
                    .findFirst()
                    .orElse(null);
        } else if (token.getType().equals(Token.Type.READONLY)) {
            return observersAccounts.keySet()
                    .stream()
                    .filter(k -> k.getTokenId().equals(token.getId()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }
}
