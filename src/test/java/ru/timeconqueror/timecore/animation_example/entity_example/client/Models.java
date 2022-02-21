package ru.timeconqueror.timecore.animation_example.entity_example.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TimeModelRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Models {
    @AutoRegistrable
    private static final TimeModelRegister REGISTER = new TimeModelRegister(TimeCore.MODID);

    public static final TimeModelLocation FLORO = REGISTER.register("models/entity/floro.json");
}
