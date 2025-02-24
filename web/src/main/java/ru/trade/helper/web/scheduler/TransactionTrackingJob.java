package ru.trade.helper.web.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import ru.trade.helper.web.service.account.TradeTokenManagerService;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionTrackingJob {
    private final TradeTokenManagerService accountManagerService;
    private final TransactionEventProcessor processor;


    @Scheduled(fixedDelayString = "${tracking-transaction.scheduler.fix-rate}")
    public void checkObservers() {
        for (var observerEntry : accountManagerService.getObserverEntries()) {
            processor.checkTransactions(observerEntry);
        }
    }
}
