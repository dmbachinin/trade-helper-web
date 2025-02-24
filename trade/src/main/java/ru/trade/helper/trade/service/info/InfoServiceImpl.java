package ru.trade.helper.trade.service.info;

import org.springframework.stereotype.Component;
import ru.tinkoff.piapi.contract.v1.Operation;
import ru.trade.helper.trade.account.ReadonlyAccount;

import java.util.List;

@Component
public class InfoServiceImpl implements InfoService {
    @Override
    public List<Operation> getOperationsInfo(ReadonlyAccount ReadonlyAccount, long durationSec) {
        return ReadonlyAccount.getAllOperation(durationSec);
    }
}
