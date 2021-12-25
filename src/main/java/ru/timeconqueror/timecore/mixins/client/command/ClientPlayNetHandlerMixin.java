package ru.timeconqueror.timecore.mixins.client.command;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.tree.CommandNode;
import com.mojang.brigadier.tree.RootCommandNode;
import net.minecraft.client.ClientTelemetryManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import ru.timeconqueror.timecore.client.command.ClientCommandManager;

@Mixin(ClientPacketListener.class)
public class ClientPlayNetHandlerMixin {
    @Shadow
    private CommandDispatcher<CommandSourceStack> commands;

    @Inject(method = "<init>",
            at = @At(value = "RETURN"))
    public void onInit(Minecraft mcIn, Screen screen, Connection networkManagerIn, GameProfile profileIn, ClientTelemetryManager telemetrySender_, CallbackInfo ci) {
        CommandDispatcher<CommandSourceStack> clientDispatcher = ClientCommandManager.getClientDispatcher();
        fillWithClientCommands(clientDispatcher);
    }

    @Inject(method = "handleCommands",
            at = @At(value = "TAIL")
    )
    public void handleCommands(ClientboundCommandsPacket packetIn, CallbackInfo ci) {
        CommandDispatcher<CommandSourceStack> clientDispatcher = ClientCommandManager.getClientDispatcher();
        fillWithClientCommands(clientDispatcher);
    }

    private void fillWithClientCommands(CommandDispatcher<CommandSourceStack> clientDispatcher) {
        RootCommandNode<CommandSourceStack> root = commands.getRoot();

        for (CommandNode<CommandSourceStack> child : clientDispatcher.getRoot().getChildren()) {
            root.addChild(child);
        }
    }
}
