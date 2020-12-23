package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.util.registry.Registry;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.registry.Promised;

import java.util.function.Supplier;

/**
 * You can use it as a wrapper for all vanilla registries, which don't have forge wrapper.
 * All entries will be registered on the main thread on {@link FMLCommonSetupEvent}
 */
public class SimpleVanillaRegister<T> extends VanillaRegister<T> {
    public SimpleVanillaRegister(String modId, Registry<T> registry) {
        super(modId, registry);
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
