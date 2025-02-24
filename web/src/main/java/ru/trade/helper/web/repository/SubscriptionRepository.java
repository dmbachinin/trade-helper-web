package ru.trade.helper.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.trade.helper.web.entity.Strategy;
import ru.trade.helper.web.entity.Subscription;

import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
