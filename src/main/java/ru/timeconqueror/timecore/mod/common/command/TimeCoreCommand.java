package ru.timeconqueror.timecore.mod.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class TimeCoreCommand {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> tcCommand = dispatcher.register(Commands.literal("timecore").then(StructureRevealerSubCommand.register()));
        dispatcher.register(Commands.literal("tc").redirect(tcCommand));
    }

    public static void registerClient(CommandDispatcher<CommandSource> dispatcher) {
        LiteralCommandNode<CommandSource> tcCommand = dispatcher.register(Commands.literal("timecore").then(StructureRevealerSubCommand.ClientSubCommand.register()));
        dispatcher.register(Commands.literal("tc").redirect(tcCommand));
    }
}
