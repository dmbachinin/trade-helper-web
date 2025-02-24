package ru.trade.helper.web.telegram.command.processor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trade.helper.web.telegram.command.factory.CommandFactory;
import ru.trade.helper.web.telegram.command.handler.Command;
import ru.trade.helper.web.telegram.info.BotMetaInfo;

@Component
@RequiredArgsConstructor
@Slf4j
public class CommandProcessor {
    private final CommandFactory commandFactory;

    public @NonNull SendMessage handeCommand(Update update, BotMetaInfo metaInfo) {
        String request;
        if (update.hasCallbackQuery()) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            request = callbackQuery.getData();
        } else {
            request = update.getMessage().getText();
        }
        LOG.info("Пользователь в чате {} выполняет запрос {}", update.getMessage().getChatId(), request);
        String[] args = request.split(" ");
        String command = args[0];
        Command method = commandFactory.getCommandHandler(command);
        return method.buildResponse(update, metaInfo, args);
    }
}
