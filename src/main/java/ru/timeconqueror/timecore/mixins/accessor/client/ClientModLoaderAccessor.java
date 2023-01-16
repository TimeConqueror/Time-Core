package ru.timeconqueror.timecore.mixins.accessor.client;

import net.minecraftforge.fml.LoadingFailedException;
import net.minecraftforge.fml.client.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClientModLoader.class, remap = false)
public interface ClientModLoaderAccessor {
    @Accessor
    static LoadingFailedException getError() {
        throw new IllegalStateException();
    }
}
