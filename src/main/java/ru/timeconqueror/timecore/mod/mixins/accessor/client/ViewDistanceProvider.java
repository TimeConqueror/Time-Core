package ru.timeconqueror.timecore.mod.mixins.accessor.client;

import net.minecraft.client.network.play.ClientPlayNetHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientPlayNetHandler.class)
public interface ViewDistanceProvider {
    @Accessor
    int getViewDistance();
}
