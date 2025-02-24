package ru.trade.helper.web.telegram.command.handler.user.base;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trade.helper.web.telegram.command.handler.Command;
import ru.trade.helper.web.telegram.info.BotMetaInfo;

@Component("/start")
public class StartCommand extends Command {

    @Override
    public SendMessage buildResponse(@NotNull Update update, BotMetaInfo metaInfo, String[] args) {
        Long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(getMessage(update));
        message.setParseMode("HTML");
        return message;
    }

    private String getMessage(Update update) {
        return String.format("""
                👋Привет, %s
                                        
                Меня зовут Trade Helper Bot🤖, я помогу тебе торговать на бирже и зарабатывать📈
                                        
                Для получения уведомлений о сделках тебе необходимо ввести в профиле свой уникальный код:
                ✅ <code>%d</code>
                """, update.getMessage().getFrom().getFirstName(), update.getMessage().getFrom().getId());
    }

}
