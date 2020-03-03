package ru.timeconqueror.timecore.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.api.TimeMod;

import java.util.ArrayList;

/**
 * Registry that should be extended and annotated with {@link ru.timeconqueror.timecore.api.registry.TimeAutoRegistry},
 * if you want to register any object that extends {@link IForgeRegistryEntry}.
 */
public abstract class ForgeTimeRegistry<T extends IForgeRegistryEntry<T>> {
    /**
     * Should be used only before calling {@link #onFireRegistryEvent(RegistryEvent.Register)}.
     * After calling that method it won't do anything and will become null.
     */
    private ArrayList<EntryWrapper> regList = new ArrayList<>();
    private TimeMod mod;

    public ForgeTimeRegistry(TimeMod mod) {
        this.mod = mod;
    }

    public void onFireRegistryEvent(RegistryEvent.Register<T> event) {
        register();

        IForgeRegistry<T> registry = event.getRegistry();
        for (EntryWrapper t : regList) {
            registry.register(t.entry);
        }

        regList.clear();
        regList = null;
    }

    /**
     * Method, where you should do all register stuff.
     */
    public abstract void register();

    public TimeMod getMod() {
        return mod;
    }

    public String getModID() {
        return getMod().getModID();
    }

    public class EntryWrapper {
        private T entry;

        /**
         * Calling this constructor you will also add it to {@link #regList}.
         */
        public EntryWrapper(T entry, String name) {
            name = name.toLowerCase();

            this.entry = entry;
            entry.setRegistryName(new ResourceLocation(getModID(), name));

            regList.add(this);
        }

        /**
         * Returns entry bound to wrapper.
         */
        public T getEntry() {
            return entry;
        }

        /**
         * Runs code in {@code clientStuff} only for physical client side.
         * Used to register models, blockstates, etc.
         */
        protected void runForClient(Runnable clientStuff) {
            if (FMLEnvironment.dist == Dist.CLIENT) {
                clientStuff.run();
            }
        }
    }
}
