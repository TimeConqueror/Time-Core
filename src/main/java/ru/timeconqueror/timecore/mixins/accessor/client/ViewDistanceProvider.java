package ru.timeconqueror.timecore.mixins.accessor.client;

import net.minecraft.client.multiplayer.ClientPacketListener;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPacketListener.class)
public interface ViewDistanceProvider {
    @Accessor
    int getServerChunkRadius();
}
