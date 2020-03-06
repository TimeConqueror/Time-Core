package ru.timeconqueror.timecore.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;

/**
 * Used for simplifying adding such stuff, that can be registered by Forge way (these objects implement {@link IForgeRegistryEntry}).
 * You need to extend it and do your stuff in {@link #register()} method<br>
 * <p>
 * Any your registry class that extends it should be annotated with {@link TimeAutoRegistrable}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b>
 */
public abstract class ForgeTimeRegistry<T extends IForgeRegistryEntry<T>> extends TimeRegistry {
    /**
     * Should be used only before calling {@link #onFireRegistryEvent(RegistryEvent.Register)}.
     * After calling that method it won't do anything and will become null.
     */
    private ArrayList<T> regList = new ArrayList<>();

    protected void onFireRegistryEvent(RegistryEvent.Register<T> event) {
        register();

        IForgeRegistry<T> registry = event.getRegistry();
        for (T entry : regList) {
            registry.register(entry);
        }

        regList.clear();
        regList = null;
    }

    protected void regEntry(T entry, String name) {
        name = name.toLowerCase();

        entry.setRegistryName(new ResourceLocation(getModID(), name));
        regList.add(entry);
    }
}
