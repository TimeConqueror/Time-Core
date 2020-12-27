package ru.timeconqueror.timecore.api.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import ru.timeconqueror.timecore.client.command.ClientCommandManager;

public class ClientCommandDispatcher {
    public static CommandDispatcher<CommandSource> get() {
        return ClientCommandManager.getClientDispatcher();
    }
}
