package ru.timeconqueror.timecore.client.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.context.CommandContextBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandSource;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import ru.timeconqueror.timecore.TimeCore;

import java.util.Arrays;

public class ClientCommandManager {
    private static final CommandDispatcher<CommandSource> CLIENT_DISPATCHER = new CommandDispatcher<>();

    /**
     * Handles provided command on the client side.
     *
     * @return false, if provided command is unknown for client side, otherwise returns true.
     */
    public static boolean handleCommand(CommandSource source, String command) {
        StringReader commandReader = new StringReader(command);
        if (commandReader.canRead() && commandReader.peek() == '/') {
            commandReader.skip();
        }

        try {
            return handleCommandWithThrowing(source, commandReader);

            //Catch clauses copied from Commands#handleCommand
        } catch (CommandException e) {
            source.sendErrorMessage(e.getComponent());
        } catch (CommandSyntaxException e) {
            source.sendErrorMessage(TextComponentUtils.toTextComponent(e.getRawMessage()));
            if (e.getInput() != null && e.getCursor() >= 0) {
                int k = Math.min(e.getInput().length(), e.getCursor());
                ITextComponent itextcomponent1 = new StringTextComponent("").applyTextStyle(TextFormatting.GRAY).applyTextStyle((style) -> style.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, command)));
                if (k > 10) {
                    itextcomponent1.appendText("...");
                }

                itextcomponent1.appendText(e.getInput().substring(Math.max(0, k - 10), k));
                if (k < e.getInput().length()) {
                    ITextComponent itextcomponent2 = (new StringTextComponent(e.getInput().substring(k))).applyTextStyles(TextFormatting.RED, TextFormatting.UNDERLINE);
                    itextcomponent1.appendSibling(itextcomponent2);
                }

                itextcomponent1.appendSibling((new TranslationTextComponent("command.context.here")).applyTextStyles(TextFormatting.RED, TextFormatting.ITALIC));
                source.sendErrorMessage(itextcomponent1);
            }
        } catch (Exception e) {
            ITextComponent itextcomponent = new StringTextComponent(e.getMessage() == null ? e.getClass().getName() : e.getMessage());
            if (TimeCore.LOGGER.isDebugEnabled()) {
                StackTraceElement[] astacktraceelement = e.getStackTrace();

                for (int j = 0; j < Math.min(astacktraceelement.length, 3); ++j) {
                    itextcomponent.appendText("\n\n").appendText(astacktraceelement[j].getMethodName()).appendText("\n ").appendText(astacktraceelement[j].getFileName()).appendText(":").appendText(String.valueOf(astacktraceelement[j].getLineNumber()));
                }
            }

            source.sendErrorMessage((new TranslationTextComponent("command.failed")).applyTextStyle((style) -> style.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, itextcomponent))));
        }

        return true;
    }

    private static boolean handleCommandWithThrowing(CommandSource source, StringReader commandReader) throws CommandSyntaxException {
        ParseResults<CommandSource> parseResults = CLIENT_DISPATCHER.parse(commandReader, source);
        if (canBeHandled(parseResults)) {
            CLIENT_DISPATCHER.execute(parseResults);

            return true;
        }

        return false;
    }

    /**
     * Checks for unknown command exception, so determines if command can be handled on client.
     */
    private static boolean canBeHandled(ParseResults<CommandSource> parseResults) {
        String[] commandPath = parseResults.getReader().getString().substring(1).split(" ");
        CommandNode<CommandSource> node = CLIENT_DISPATCHER.findNode(Arrays.asList(commandPath));
//        System.out.println(node);
//        System.out.println(parseResults.getContext());
        CommandContextBuilder<CommandSource> context = parseResults.getContext();
        context.getCommand();


        if (parseResults.getReader().canRead()) {
            return !parseResults.getContext().getRange().isEmpty();
        }

        return true;
    }

    private static boolean searchByLiterals(ParseResults<CommandSource> parseResults) {
//        String[] commandPath = parseResults.getReader().getString().substring(1).split(" ");
//        CommandNode<S> node = root;
//        for (final String name : commandPath) {
//            node = node.getChild(name);
//            if (node == null) {
//                return null;
//            }
//        }
        return true;
    }

    public static CommandDispatcher<CommandSource> getClientDispatcher() {
        return CLIENT_DISPATCHER;
    }
}