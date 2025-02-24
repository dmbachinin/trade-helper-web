package ru.trade.helper.web.telegram.info;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;


@Setter
@Getter
@Component
@NoArgsConstructor
public class BotMetaInfo {
    private final Timestamp launchDate = new Timestamp(System.currentTimeMillis());
    private Long transactionsTrackedCount = 0L;
    private boolean isDebugMode = false;

    public void changeDebugMode() {
        this.isDebugMode = !isDebugMode;
    }
}
