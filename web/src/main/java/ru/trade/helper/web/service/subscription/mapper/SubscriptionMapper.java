package ru.trade.helper.web.service.subscription.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.trade.helper.web.entity.Subscription;
import ru.trade.helper.web.model.SubscriberDto;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "tokenId", expression = "java(sub.getToken().getId())")
    @Mapping(target = "userId", expression = "java(sub.getUser().getId())")
    @Mapping(target = "chatId", expression = "java(sub.getUser().getChatId())")
    @Mapping(target = "isTelegramNotification", expression = "java(sub.getUser().getIsTelegramNotification())")
    SubscriberDto toDto(Subscription sub);

    Set<SubscriberDto> toDto(Set<Subscription> subs);
}
