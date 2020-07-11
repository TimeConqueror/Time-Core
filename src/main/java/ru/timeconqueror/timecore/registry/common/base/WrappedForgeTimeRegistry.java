package ru.timeconqueror.timecore.registry.common.base;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistryEntry;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;

/**
 * Used for simplifying adding such stuff, that can be registered by Forge way (these objects implement {@link IForgeRegistryEntry}).
 * You need to extend it and do your stuff in {@link #register()} method<br>
 * Provides forge registry with Wrapper classes to provide extra features for every entry.<br>
 * <p>
 * Any your registry class that extends it should be annotated with {@link TimeAutoRegistrable}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 */
public abstract class WrappedForgeTimeRegistry<T extends IForgeRegistryEntry<T>> extends ForgeTimeRegistry<T> {
    public class EntryWrapper {
        private final T entry;

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

        public ResourceLocation getId() {
            return getEntry().getRegistryName();
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
