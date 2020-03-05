package ru.timeconqueror.timecore.api.registry.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import ru.timeconqueror.timecore.api.registry.ForgeTimeRegistry;
import ru.timeconqueror.timecore.api.registry.TimeAutoRegistry;

/**
 * Used for simplifying sound adding.<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistry}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 * Examples can be seen at test module.//TODO add examples :)
 */
public abstract class SoundTimeRegistry extends ForgeTimeRegistry<SoundEvent> {

    /**
     * Method to create sound and register it automatically.
     *
     * @param name sound name.
     *             It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     */
    public static TimeSound createSound(String name) {
        ResourceLocation location = new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name.toLowerCase());

        return new TimeSound(location);
    }

    @SubscribeEvent
    public final void onRegSoundEvent(RegistryEvent.Register<SoundEvent> event) {
        onFireRegistryEvent(event);
    }

    public void regSound(TimeSound sound) {
        regEntry(sound, sound.name().getPath());
    }
}
