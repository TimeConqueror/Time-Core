package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TimeModelRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TileModels {
    @AutoRegistrable
    private static final TimeModelRegister REGISTER = new TimeModelRegister(TimeCore.MODID);
    public static TimeModelLocation HEAT_CUBE = REGISTER.register("models/tileentity/heat_cube.json");
}
