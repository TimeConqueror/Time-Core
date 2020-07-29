package ru.timeconqueror.timecore.mod.mixins.client.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.command.CommandSource;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SCommandListPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.client.command.ClientCommandManager;

@Mixin(ClientPlayNetHandler.class)
public class ClientPlayNetHandlerMixin {
    @Shadow
    private CommandDispatcher<CommandSource> commandDispatcher;

    @Inject(method = "<init>",
            at = @At(value = "RETURN"))
    public void onInit(Minecraft mcIn, Screen p_i46300_2_, NetworkManager networkManagerIn, GameProfile profileIn, CallbackInfo ci) {
        CommandDispatcher<CommandSource> clientDispatcher = ClientCommandManager.getClientDispatcher();
        fillWithClientCommands(clientDispatcher);
    }

    @Inject(method = "handleCommandList",
            at = @At(value = "TAIL")
    )
    public void handleCommandList(SCommandListPacket packetIn, CallbackInfo ci) {
        CommandDispatcher<CommandSource> clientDispatcher = ClientCommandManager.getClientDispatcher();
        fillWithClientCommands(clientDispatcher);
    }

    private void fillWithClientCommands(CommandDispatcher<CommandSource> clientDispatcher) {
        RootCommandNode<CommandSource> root = commandDispatcher.getRoot();

        for (CommandNode<CommandSource> child : clientDispatcher.getRoot().getChildren()) {
            root.addChild(child);
        }
    }
}
