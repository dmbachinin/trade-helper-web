package ru.trade.helper.web.service.market;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import ru.trade.helper.web.model.SubscriberDto;
import ru.trade.helper.web.model.MarketEventData;
import ru.trade.helper.web.model.OperationDto;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Service
@Slf4j
@RequiredArgsConstructor
public class MarketService {

    private final BlockingQueue<MarketEventData> queue = new LinkedBlockingQueue<>();
    private final MarketEventProcessor processor;


    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        new Thread(this::processQueue).start();
    }

    public void addToQueue(MarketEventData buyEvent) {
        try {
            queue.put(buyEvent);
            LOG.info("В очередь на покупку добавлена операция {}", buyEvent.operation().getId());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LOG.error("Ошибка при добавлении операции в очередь {}", buyEvent.operation().getId());
            throw new RuntimeException("Ошибка при добавлении операции в очередь", e);
        }
    }

    @Async
    public void processQueue() {
        LOG.info("Запущен процесс MarketService");

        while (!Thread.currentThread().isInterrupted()) {
            try {
                var buyEvent = queue.take();
                Set<SubscriberDto> buyers = buyEvent.buyers();
                OperationDto operation = buyEvent.operation();
                LOG.info("Операция взята в обработку {}", operation.getId());
                for (SubscriberDto buyer : buyers) {
                    processor.eventProcessing(buyer, operation);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Прерывание процесса MarketService", e);
            }
        }
    }

}
