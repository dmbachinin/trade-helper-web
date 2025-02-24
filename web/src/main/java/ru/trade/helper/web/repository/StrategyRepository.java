package ru.trade.helper.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trade.helper.web.entity.Strategy;
import ru.trade.helper.web.entity.Token;

import java.util.Optional;

public interface StrategyRepository extends JpaRepository<Strategy, Long> {
    Optional<Strategy> findByTokenId(Long token_id);
}
