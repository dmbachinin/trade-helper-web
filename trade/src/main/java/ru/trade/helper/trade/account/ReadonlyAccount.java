package ru.trade.helper.trade.account;

import ru.tinkoff.piapi.contract.v1.Operation;

import java.util.List;

public abstract class ReadonlyAccount extends Account {
    public ReadonlyAccount(String token) {
        super(token, true);
    }

    public abstract List<Operation> getAllOperation(long durationMillis);
}
