package ru.timeconqueror.timecore.registry.deferred;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.api.common.sound.TimeSound;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.deferred.base.DeferredFMLImplForgeRegister;

/**
 * Deferred Register for sounds.
 * <p>
 * To work it needs to be in a static field in registry class and be annotated with {@link TimeAutoRegistrable}.
 * Extra params in this annotation are ignored.
 */
public class DeferredSoundRegister extends DeferredFMLImplForgeRegister<SoundEvent> {
    public DeferredSoundRegister(String modid) {
        super(ForgeRegistries.SOUND_EVENTS, modid);
    }

    public TimeSound regSound(String name) {
        ResourceLocation location = new ResourceLocation(getModid(), name);

        TimeSound sound = new TimeSound(location);

        deferredRegister.register(name, () -> sound);

        return sound;
    }
}
