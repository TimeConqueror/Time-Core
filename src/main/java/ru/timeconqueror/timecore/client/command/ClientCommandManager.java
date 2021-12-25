package ru.timeconqueror.timecore.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandRuntimeException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.*;
import ru.timeconqueror.timecore.TimeCore;

public class ClientCommandManager {
    private static final CommandDispatcher<CommandSourceStack> CLIENT_DISPATCHER = new CommandDispatcher<>();

    /**
     * Handles provided command on the client side.
     *
     * @return false, if provided command is unknown for client side, otherwise returns true.
     */
    public static boolean handleCommand(CommandSourceStack source, String command) {
        StringReader commandReader = new StringReader(command);
        if (commandReader.canRead() && commandReader.peek() == '/') {
            commandReader.skip();
        }

        try {
            return handleCommandWithThrowing(source, commandReader);

            //Catch clauses copied from Commands#handleCommand
        } catch (CommandRuntimeException e) {
            source.sendFailure(e.getComponent());
        } catch (CommandSyntaxException e) {
            source.sendFailure(ComponentUtils.fromMessage(e.getRawMessage()));
            if (e.getInput() != null && e.getCursor() >= 0) {
                int k = Math.min(e.getInput().length(), e.getCursor());
                MutableComponent itextcomponent1 = new TextComponent("").withStyle(ChatFormatting.GRAY).withStyle((style) -> style.withClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (k > 10) {
                    itextcomponent1.append("...");
                }

                itextcomponent1.append(e.getInput().substring(Math.max(0, k - 10), k));
                if (k < e.getInput().length()) {
                    Component itextcomponent2 = (new TextComponent(e.getInput().substring(k))).withStyle(ChatFormatting.RED, ChatFormatting.UNDERLINE);
                    itextcomponent1.append(itextcomponent2);
                }

                itextcomponent1.append((new TranslatableComponent("command.context.here")).withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
                source.sendFailure(itextcomponent1);
            }
        } catch (Exception e) {
            MutableComponent itextcomponent = new TextComponent(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
            if (TimeCore.LOGGER.isDebugEnabled()) {
                StackTraceElement[] astacktraceelement = e.getStackTrace();

                for (int j = 0; j < Math.min(astacktraceelement.length, 3); ++j) {
                    itextcomponent.append("\n\n").append(astacktraceelement[j].getMethodName()).append("\n ").append(astacktraceelement[j].getFileName()).append(":").append(String.valueOf(astacktraceelement[j].getLineNumber()));
                }
            }

            source.sendFailure((new TranslatableComponent("command.failed")).withStyle((style) -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent))));
        }

        return true;
    }

    private static boolean handleCommandWithThrowing(CommandSourceStack source, StringReader commandReader) throws CommandSyntaxException {
        ParseResults<CommandSourceStack> parseResults = CLIENT_DISPATCHER.parse(commandReader, source);
        if (canBeHandled(parseResults)) {
            CLIENT_DISPATCHER.execute(parseResults);

            return true;
        }

        return false;
    }

    /**
     * Checks for unknown command exception, so determines if command can be handled on client.
     */
    private static boolean canBeHandled(ParseResults<CommandSourceStack> parseResults) {
        if (parseResults.getReader().canRead()) {
            return !parseResults.getContext().getRange().isEmpty();
        }

        return true;
    }

    public static CommandDispatcher<CommandSourceStack> getClientDispatcher() {
        return CLIENT_DISPATCHER;
    }
}
