package ru.timeconqueror.timecore.api.common.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.api.TimeMod;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry that should be extended and annotated with {@link ru.timeconqueror.timecore.api.common.registry.TimeAutoRegistry},
 * if you want to register any object that extends {@link IForgeRegistryEntry}.
 */
public abstract class ForgeTimeRegistry<T extends IForgeRegistryEntry<T>> {
    private ArrayList<EntryWrapper> regList = new ArrayList<>();//TODO clear after reg
    private TimeMod mod;

    public ForgeTimeRegistry(TimeMod mod) {
        this.mod = mod;
    }

    public void onFireRegistryEvent(RegistryEvent.Register<T> event) {
        register();

        for (EntryWrapper t : regList) {
            event.getRegistry().register(t.entry);
        }
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

    public List<EntryWrapper> getRegList() {
        return regList;
    }

    public class EntryWrapper {
        private T entry;

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
