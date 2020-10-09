package ru.timeconqueror.timecore.animation_example.block_example.registry;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation_example.block_example.block.tile.TileHeatCube;
import ru.timeconqueror.timecore.animation_example.block_example.client.render.TERHeatCube;
import ru.timeconqueror.timecore.client.render.model.TimeModel;
import ru.timeconqueror.timecore.client.render.model.TimeModelLoader;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.deferred.DeferredTileEntityRegister;

@TimeAutoRegistrable
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ATileRegistry {
    @TimeAutoRegistrable
    private static final DeferredTileEntityRegister REGISTER = new DeferredTileEntityRegister(TimeCore.MODID);

    public static RegistryObject<TileEntityType<TileHeatCube>> HEAT_CUBE_TILE = REGISTER.regTileEntityType("heat_cube", TileHeatCube::new, () -> new Block[]{ABlockRegistry.HEAT_CUBE.get()})
            .regCustomRenderer(() -> TERHeatCube::new)
            .endTyped();

    public static TimeModel heatCubeModel;

    @SubscribeEvent
    public static void registerRenders(FMLClientSetupEvent event) {
        heatCubeModel = TimeModelLoader.loadJsonModel(new ResourceLocation(TimeCore.MODID, "models/tileentity/heat_cube.json"), RenderType::getEntityCutout);
    }
}
