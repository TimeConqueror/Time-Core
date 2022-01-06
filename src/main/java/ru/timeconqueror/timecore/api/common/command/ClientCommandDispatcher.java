package ru.timeconqueror.timecore.api.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import ru.timeconqueror.timecore.internal.client.command.ClientCommandManager;

public class ClientCommandDispatcher {
    public static CommandDispatcher<CommandSourceStack> get() {
        return ClientCommandManager.getClientDispatcher();
    }
}
