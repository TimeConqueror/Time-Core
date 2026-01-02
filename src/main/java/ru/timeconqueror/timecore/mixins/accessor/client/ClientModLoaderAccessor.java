package ru.timeconqueror.timecore.mixins.accessor.client;


import net.neoforged.fml.ModLoadingException;
import net.neoforged.neoforge.client.loading.ClientModLoader;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = ClientModLoader.class, remap = false)
public interface ClientModLoaderAccessor {
    @Accessor
    static ModLoadingException getError() {
        throw new IllegalStateException();
    }
}
