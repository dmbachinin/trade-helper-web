package ru.trade.helper.trade.service.info;

import ru.tinkoff.piapi.contract.v1.Operation;
import ru.trade.helper.trade.account.ReadonlyAccount;

import java.util.List;

public interface InfoService {
    List<Operation> getOperationsInfo(ReadonlyAccount ReadonlyAccount, long durationSec);
}
