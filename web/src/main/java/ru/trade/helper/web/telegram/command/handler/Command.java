package ru.trade.helper.web.telegram.command.handler;

import lombok.Getter;
import lombok.Setter;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trade.helper.web.telegram.info.BotMetaInfo;

@Getter
@Setter
public abstract class Command {
    abstract public SendMessage buildResponse(Update update, BotMetaInfo metaInfo, String[] args);
}
