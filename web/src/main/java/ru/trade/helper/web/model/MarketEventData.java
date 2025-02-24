package ru.trade.helper.web.model;

import lombok.Builder;

import java.util.Set;

@Builder
public record MarketEventData(Set<SubscriberDto> buyers, OperationDto operation) {
}
