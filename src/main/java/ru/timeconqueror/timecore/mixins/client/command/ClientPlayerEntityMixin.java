package ru.timeconqueror.timecore.mixins.client.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LocalPlayer.class)
public class ClientPlayerEntityMixin {
    //todo port?
//    @Inject(method = "chat", at = @At(value = "HEAD"), cancellable = true)
//    public void onSendChatMessage(String message, CallbackInfo ci) {
//        if (message.startsWith("/")) {
//            if (ClientCommandManager.handleCommand(Minecraft.getInstance().player.createCommandSourceStack(), message)) {
//                ci.cancel();
//            }
//        }
//    }
}
