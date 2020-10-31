package ru.timeconqueror.timecore.registry.newreg;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;
import ru.timeconqueror.timecore.api.common.sound.TimeSound;

public class SoundRegister extends ForgeRegister<SoundEvent> {
    public SoundRegister(String modid) {
        super(ForgeRegistries.SOUND_EVENTS, modid);
    }

    public RegistryObject<TimeSound> register(String name) {
        ResourceLocation location = new ResourceLocation(getModid(), name);

        return registerEntry(name, () -> new TimeSound(location));
    }
}
