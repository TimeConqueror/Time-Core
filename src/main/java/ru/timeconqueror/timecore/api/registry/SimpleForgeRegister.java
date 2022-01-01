package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

/**
 * Simple register, which can be used for every stuff,
 * which is handled by {@link ForgeRegistries}, but doesn't have any extra settings provided by TimeCore.
 */
public class SimpleForgeRegister<T extends IForgeRegistryEntry<T>> extends ForgeRegister<T> {
    public SimpleForgeRegister(IForgeRegistry<T> reg, String modid) {
        super(reg, modid);
    }

    public <I extends T> RegistryObject<I> register(String name, Supplier<I> entry) {
        return registerEntry(name, entry);
    }
}
