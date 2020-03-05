package ru.timeconqueror.timecore.api.registry;

import net.minecraft.util.ResourceLocation;
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
     * Method to create sound and register it automatically.
     *
     * @param name sound name.
     *             It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     */
    public SoundEvent createAndRegSound(String name) {
        name = name.toLowerCase();

        ResourceLocation location = new ResourceLocation(getModID(), name);
        SoundEvent soundEvent = new SoundEvent(location);

        regEntry(soundEvent, name);

        return soundEvent;
    }
}
