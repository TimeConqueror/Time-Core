package ru.timeconqueror.timecore.animation_example.block_example.client.render;

import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.renderer.AnimatedTileEntityRenderer;
import ru.timeconqueror.timecore.animation_example.block_example.block.tile.TileHeatCube;
import ru.timeconqueror.timecore.animation_example.block_example.registry.TileModels;
import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
import ru.timeconqueror.timecore.client.render.model.ModelConfiguration;
import ru.timeconqueror.timecore.client.render.model.TimeModel;

public class TERHeatCube extends AnimatedTileEntityRenderer<TileHeatCube> {
    public TERHeatCube(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn, new TimeModel(ModelConfiguration.builder(TileModels.HEAT_CUBE).build()));
    }

    @Override
    protected ResourceLocation getTexture(TileHeatCube tileEntityIn) {
        return new TextureLocation(TimeCore.MODID, "tileentity/spark_smelter.png").fullLocation();
    }
}
