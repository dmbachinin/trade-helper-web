package ru.trade.helper.web.telegram.command.handler.user.base;

import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.trade.helper.web.telegram.command.handler.Command;
import ru.trade.helper.web.telegram.info.BotMetaInfo;

import java.text.SimpleDateFormat;
import java.util.StringJoiner;

@Component("/info")
public class InfoCommand extends Command {

    @Override
    public SendMessage buildResponse(@NotNull Update update, BotMetaInfo metaInfo, String[] args) {
        Long chatId = update.getMessage().getChatId();
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        StringJoiner stringJoiner = new StringJoiner("\n");

        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy");

        stringJoiner.add("Информация о боте");
        stringJoiner.add(String.format("Дата запуска: %s", sdf.format(metaInfo.getLaunchDate())));
        stringJoiner.add(String.format("Количество сделок: %s", metaInfo.getTransactionsTrackedCount()));
        message.setText(stringJoiner.toString());
        return message;
    }
}
