package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.client.render.model.TimeModelLoader;
import ru.timeconqueror.timecore.client.render.model.TimeModel;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class TileModels {
    @OnlyIn(Dist.CLIENT)
    public static TimeModel heatCubeModel;

    @SubscribeEvent
    public static void registerRenders(FMLClientSetupEvent event) {
        heatCubeModel = TimeModelLoader.loadJsonModel(new ResourceLocation(TimeCore.MODID, "models/tileentity/heat_cube.json"), RenderType::entityCutout);
    }
}
