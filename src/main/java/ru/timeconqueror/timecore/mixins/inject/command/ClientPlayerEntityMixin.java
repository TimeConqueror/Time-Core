package ru.timeconqueror.timecore.mixins.inject.command;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.client.command.ClientCommandManager;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    @Inject(method = "sendChatMessage", at = @At(value = "HEAD"), cancellable = true)
    public void onSendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith("/")) {
            if (ClientCommandManager.handleCommand(Minecraft.getInstance().player.getCommandSource(), message)) {
                ci.cancel();
            }
        }
    }
}
