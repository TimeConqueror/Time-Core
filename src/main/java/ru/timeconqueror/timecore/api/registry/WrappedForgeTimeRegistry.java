package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.api.TimeMod;

public abstract class WrappedForgeTimeRegistry<T extends IForgeRegistryEntry<T>> extends ForgeTimeRegistry<T> {
    public WrappedForgeTimeRegistry(TimeMod mod) {
        super(mod);
    }

    public class EntryWrapper {
        private T entry;

        /**
         * Calling this constructor you will also register entry.
         */
        public EntryWrapper(T entry, String name) {
            this.entry = entry;
            regEntry(entry, name);
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
