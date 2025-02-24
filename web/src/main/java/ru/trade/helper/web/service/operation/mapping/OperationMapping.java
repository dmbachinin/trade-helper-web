package ru.trade.helper.web.service.operation.mapping;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.tinkoff.piapi.contract.v1.Instrument;
import ru.trade.helper.web.entity.User;
import ru.trade.helper.web.entity.Operation;
import ru.trade.helper.web.model.OperationDto;
import ru.trade.helper.trade.account.Account;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface OperationMapping {

    @Mapping(target = "priceUnits", expression = "java(tOperation.getPrice().getUnits())")
    @Mapping(target = "priceNano", expression = "java(tOperation.getPrice().getNano())")
    Operation toBack(ru.tinkoff.piapi.contract.v1.Operation tOperation);

    default Operation toBack(ru.tinkoff.piapi.contract.v1.Operation tOperation, Account tAccount, User user) {
        Operation operation = toBack(tOperation);
        operation.setAccountId(tAccount.getId());
        operation.setAccountName(tAccount.getName());
        Instrument tInstrument = tAccount.getInstrumentByFigi(operation.getFigi());
        if (tInstrument != null) {
            operation.setInstrumentTicker(tInstrument.getTicker());
        } else {
            operation.setInstrumentTicker("UNKNOWN");
        }
        operation.setUser(user);
        return operation;
    }

    default List<Operation> toBack(List<ru.tinkoff.piapi.contract.v1.Operation> tOperations, Account tAccount, User user) {
        if (tOperations == null) {
            return Collections.emptyList();
        }
        return tOperations.stream().map(o -> toBack(o, tAccount, user)).toList();
    }

    default Timestamp fromProtoToSql(com.google.protobuf.Timestamp protoTimestamp) {
        if (protoTimestamp == null) {
            return null;
        }

        long seconds = protoTimestamp.getSeconds();
        int nanos = protoTimestamp.getNanos();

        long milliseconds = seconds * 1000 + nanos / 1_000_000;

        return new Timestamp(milliseconds);
    }

    default Operation.OperationType toBack(ru.tinkoff.piapi.contract.v1.OperationType tOperationType) {
        return switch (tOperationType) {
            case OPERATION_TYPE_BUY, OPERATION_TYPE_BUY_CARD -> Operation.OperationType.BUY;
            case OPERATION_TYPE_SELL, OPERATION_TYPE_SELL_CARD -> Operation.OperationType.SELL;
            default -> Operation.OperationType.OTHER;
        };
    }

    OperationDto toDto(Operation operation);
}
