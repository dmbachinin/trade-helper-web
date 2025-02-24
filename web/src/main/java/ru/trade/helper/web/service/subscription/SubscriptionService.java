package ru.trade.helper.web.service.subscription;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.trade.helper.web.repository.SubscriptionRepository;

@Service
@RequiredArgsConstructor
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
}
