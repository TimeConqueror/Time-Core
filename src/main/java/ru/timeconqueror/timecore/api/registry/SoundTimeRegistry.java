package ru.timeconqueror.timecore.api.registry;

import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import ru.timeconqueror.timecore.api.TimeMod;

/**
 * Used for easy sound registering.
 * Any class that extends this, should be extended and annotated with {@link TimeAutoRegistry}.
 */
public abstract class SoundTimeRegistry extends ForgeTimeRegistry<SoundEvent> {
    public SoundTimeRegistry(TimeMod mod) {
        super(mod);
    }

    @SubscribeEvent
    public final void onRegSoundEvent(RegistryEvent.Register<SoundEvent> event) {
        onFireRegistryEvent(event);
    }

    /**
     * Method to register sounds automatically.
     *
     * @param sound sound to be registered.
     * @param name  sound name.
     *              It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     * @return {@link SoundWrapper} to provide extra register options.
     */
    public SoundWrapper regSound(SoundEvent sound, String name) {
        return new SoundWrapper(sound, name);
    }

    public class SoundWrapper extends EntryWrapper {
        public SoundWrapper(SoundEvent entry, String name) {
            super(entry, name);
        }

        /**
         * Returns sound bound to wrapper.
         * Method duplicates {@link #getEntry()}, so it exists only for easier understanding.
         */
        public SoundEvent getSound() {
            return getEntry();
        }
    }
}
