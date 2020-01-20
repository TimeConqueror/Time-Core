package ru.timeconqueror.timecore.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.api.ITimeMod;

import java.util.ArrayList;

public abstract class TimeForgeRegistry<T extends IForgeRegistryEntry<T>> {
    protected ArrayList<EntryWrapper> regList = new ArrayList<>();
    private ITimeMod mod;

    public TimeForgeRegistry(ITimeMod mod) {
        this.mod = mod;
    }

    public void onFireRegistryEvent(RegistryEvent.Register<T> event) {
        register();

        for (EntryWrapper t : regList) {
            event.getRegistry().register(t.entry);
        }
    }

    public abstract void register();

    public ITimeMod getMod() {
        return mod;
    }

    public class EntryWrapper {
        private T entry;

        public EntryWrapper(T entry, String name) {
            this.entry = entry;
            entry.setRegistryName(new ResourceLocation(getMod().getModID(), name));

            regList.add(this);
        }

        public T get() {
            return entry;
        }
    }
}
