package ru.trade.helper.web.service.order.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.tinkoff.piapi.contract.v1.OrderDirection;
import ru.tinkoff.piapi.contract.v1.PostOrderResponse;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.entity.Order;
import ru.trade.helper.trade.account.Account;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    @Mapping(target = "commissionUnits", expression = "java(tOrder.getExecutedCommission().getUnits())")
    @Mapping(target = "commissionNano", expression = "java(tOrder.getExecutedCommission().getNano())")
    @Mapping(target = "orderPriceUnits", expression = "java(tOrder.getExecutedOrderPrice().getUnits())")
    @Mapping(target = "orderPriceNano", expression = "java(tOrder.getExecutedOrderPrice().getNano())")
    @Mapping(target = "totalOrderAmountUnits", expression = "java(tOrder.getTotalOrderAmount().getUnits())")
    @Mapping(target = "totalOrderAmountNano", expression = "java(tOrder.getTotalOrderAmount().getNano())")
    @Mapping(target = "createDate", expression = "java( new java.sql.Timestamp(System.currentTimeMillis()))")
    @Mapping(target = "id", source = "orderId")
    Order toBack(PostOrderResponse tOrder);

    default Order.OrderDirection toBack(OrderDirection tOrderDirection) {
        return switch (tOrderDirection) {
            case ORDER_DIRECTION_BUY -> Order.OrderDirection.BUY;
            case ORDER_DIRECTION_SELL -> Order.OrderDirection.SELL;
            default -> Order.OrderDirection.UNKNOWN;
        };
    }

    default Order toBack(PostOrderResponse tOrder, Account tAccount, User user) {
        Order order = toBack(tOrder);
        order.setAccountId(tAccount.getId());
        order.setAccountName(tAccount.getName());
        Instrument tInstrument = tAccount.getInstrumentByFigi(order.getFigi());
        if (tInstrument != null) {
            order.setInstrumentTicker(tInstrument.getTicker());
        } else {
            order.setInstrumentTicker("UNKNOWN");
        }
        order.setUser(user);
        return order;
    }

    default List<Order> toBack(List<PostOrderResponse> tOperations, Account tAccount, User user) {
        if (tOperations == null) {
            return Collections.emptyList();
        }
        return tOperations.stream().map(o -> toBack(o, tAccount, user)).toList();
    }
}
