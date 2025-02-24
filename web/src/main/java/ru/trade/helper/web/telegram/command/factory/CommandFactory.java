package ru.trade.helper.web.telegram.command.factory;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.trade.helper.web.telegram.command.handler.Command;
import ru.trade.helper.web.telegram.command.handler.user.base.UnknownCommand;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Getter
public class CommandFactory {
    private final Map<String, Command> commandHandlers;

    public Command getCommandHandler(String command) {
        return commandHandlers.getOrDefault(command, new UnknownCommand());
    }
}
