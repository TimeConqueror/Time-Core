package ru.timeconqueror.timecore.api.registry;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Used for simplifying adding such stuff, that can be registered by Forge way (these objects implement {@link IForgeRegistryEntry}).<br>
 * Provides forge registry with Wrapper classes to provide extra features for every entry.<br>
 * <p>
 * Any your registry class that extends it should be annotated with {@link TimeAutoRegistry}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 */
public abstract class WrappedForgeTimeRegistry<T extends IForgeRegistryEntry<T>> extends ForgeTimeRegistry<T> {
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
