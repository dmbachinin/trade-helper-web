package ru.trade.helper.web.service.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trade.helper.web.entity.Strategy;
import ru.trade.helper.web.repository.StrategyRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StrategyService {

    private final StrategyRepository strategyRepository;

    public Optional<Strategy> findByTokenId(Long tokenId) {
        return strategyRepository.findByTokenId(tokenId);
    }
}
