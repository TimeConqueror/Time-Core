package ru.timeconqueror.timecore.mixins.accessor.client;

import net.minecraftforge.client.loading.ClientModLoader;
import net.minecraftforge.fml.LoadingFailedException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClientModLoader.class, remap = false)
public interface ClientModLoaderAccessor {
    @Accessor
    static LoadingFailedException getError() {
        throw new IllegalStateException();
    }
}
