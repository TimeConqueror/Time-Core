package ru.timeconqueror.timecore.client.command;//package ru.timeconqueror.timecore.client.command;
//
//import net.minecraft.client.Minecraft;
//import net.minecraft.client.entity.player.ClientPlayerEntity;
//import net.minecraft.command.ICommandSource;
//import net.minecraft.util.text.ITextComponent;
//import net.minecraft.util.text.StringTextComponent;
//import net.minecraft.util.text.TextFormatting;
//import org.jetbrains.annotations.NotNull;
//
//public class ClientCommandSource implements ICommandSource {
//    private final ClientPlayerEntity player;
//
//    public ClientCommandSource() {
//        this.player = Minecraft.getInstance().player;
//    }
//
//    @Override
//    public void sendMessage(@NotNull ITextComponent component) {
//        player.sendMessage(component);
//    }
//
//    @Override
//    public boolean shouldReceiveFeedback() {
//        return true;
//    }
//
//    @Override
//    public boolean shouldReceiveErrors() {
//        return true;
//    }
//
//    @Override
//    public boolean allowLogging() {
//        return true;
//    }
//
//    public void sendErrorMessage(ITextComponent message) {
//        if (shouldReceiveErrors() && shouldReceiveFeedback()) {
//            sendMessage((new StringTextComponent("")).appendSibling(message).applyTextStyle(TextFormatting.RED));
//        }
//    }
//}
