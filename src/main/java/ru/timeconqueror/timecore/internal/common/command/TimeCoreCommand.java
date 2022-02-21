package ru.timeconqueror.timecore.internal.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class TimeCoreCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("timecore").then(StructureRevealerSubCommand.register()));
    }

    public static void registerClient(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(Commands.literal("timecore_client").then(StructureRevealerSubCommand.ClientSubCommand.register()));
    }
}
