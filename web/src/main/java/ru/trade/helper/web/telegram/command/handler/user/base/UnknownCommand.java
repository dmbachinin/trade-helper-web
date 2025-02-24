package ru.trade.helper.web.telegram.command.handler.user.base;

import jakarta.validation.constraints.NotNull;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trade.helper.web.telegram.command.handler.Command;
import ru.trade.helper.web.telegram.info.BotMetaInfo;

public class UnknownCommand extends Command {

    @Override
    public SendMessage buildResponse(@NotNull Update update, BotMetaInfo metaInfo, String[] args) {
        Long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setParseMode("HTML");
        message.setText(String.format("""
                Для получения уведомлений о сделках тебе необходимо ввести в профиле свой уникальный код:
                ✅ <code>%d</code>""", chatId));
        return message;
    }
}
