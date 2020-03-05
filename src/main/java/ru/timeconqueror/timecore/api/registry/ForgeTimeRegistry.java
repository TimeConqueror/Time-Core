package ru.timeconqueror.timecore.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.api.TimeMod;

import java.util.ArrayList;

/**
 * Registry that should be extended and annotated with {@link ru.timeconqueror.timecore.api.registry.TimeAutoRegistry},
 * if you want to register any object that extends {@link IForgeRegistryEntry}.
 */
public abstract class ForgeTimeRegistry<T extends IForgeRegistryEntry<T>> implements TimeRegistry {
    /**
     * Should be used only before calling {@link #onFireRegistryEvent(RegistryEvent.Register)}.
     * After calling that method it won't do anything and will become null.
     */
    private ArrayList<T> regList = new ArrayList<>();
    private TimeMod mod;

    public ForgeTimeRegistry(TimeMod mod) {
        this.mod = mod;
    }

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

    public TimeMod getMod() {
        return mod;
    }

    public String getModID() {
        return getMod().getModID();
    }
}
