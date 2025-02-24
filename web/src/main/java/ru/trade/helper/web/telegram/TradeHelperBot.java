package ru.trade.helper.web.telegram;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.trade.helper.web.telegram.command.processor.CommandProcessor;
import ru.trade.helper.web.telegram.info.BotMetaInfo;

@Slf4j
@Component
public class TradeHelperBot extends TelegramLongPollingBot {

    private static final String BOT_USERNAME = "@TradeHelperBot";


    private final BotMetaInfo metaInfo;
    private final CommandProcessor commandProcessor;

    public TradeHelperBot(@Value("${bot.token}") String token,
                          BotMetaInfo metaInfo, CommandProcessor commandProcessor) throws TelegramApiException {
        super(token);

        this.metaInfo = metaInfo;
        this.commandProcessor = commandProcessor;

        TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
        botsApi.registerBot(this);
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText() ||
                update.hasCallbackQuery()) {
            try {
                SendMessage sendMessage = commandProcessor.handeCommand(update, metaInfo);
                execute(sendMessage);
            } catch (Exception e) {
                LOG.error("Ошибка при обработке запроса: " + e);
            }
        }
    }

    public void sendMessageToUser(Long chatId, String message) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            LOG.error("Ошибка при отправке сообщения {} в чат {}", message, chatId);
        }
    }
}
