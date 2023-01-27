package ru.timeconqueror.timecore.api.registry;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import ru.timeconqueror.timecore.api.registry.util.Promised;

import java.util.function.Supplier;

/**
 * Simple register, which can be used for every stuff,
 * which is handled by {@link ForgeRegistries} or built-in vanilla registries, but doesn't have any extra settings provided by TimeCore.
 */
public class SimpleVanillaRegister<T> extends VanillaRegister<T> {
    public SimpleVanillaRegister(ResourceKey<? extends Registry<T>> registryKey, String modId) {
        super(registryKey, modId);
    }

    public SimpleVanillaRegister(Registry<T> registry, String modId) {
        super(registry, modId);
    }

    public SimpleVanillaRegister(IForgeRegistry<T> registry, String modId) {
        super(registry, modId);
    }

    /**
     * Adds entry to the delayed registry array, all entries from which will be registered later.
     *
     * @param name     The entry's name, will automatically have the modid as a namespace.
     * @param entrySup supplier of entry to be registered.
     */
    public <I extends T> Promised<I> register(String name, Supplier<I> entrySup) {
        return registerEntry(name, entrySup);
    }
}
