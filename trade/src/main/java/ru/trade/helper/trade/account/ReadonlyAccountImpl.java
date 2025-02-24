package ru.trade.helper.trade.account;

import ru.tinkoff.piapi.contract.v1.Operation;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ReadonlyAccountImpl extends ReadonlyAccount {

    public ReadonlyAccountImpl(String token) {
        super(token);
    }

    @Override
    public List<Operation> getAllOperation(long durationMillis) {
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        ZonedDateTime currDate = now.minus(durationMillis, ChronoUnit.MILLIS);

        Instant nowDate = now.toInstant();
        Instant offsetDate = currDate.toInstant();
        try {
            return operationService.getAllOperations(account.getId(), offsetDate, nowDate).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
