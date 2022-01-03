//package ru.timeconqueror.timecore.animation_example.block_example.client.render;
//
//import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
//import net.minecraft.util.ResourceLocation;
//import ru.timeconqueror.timecore.TimeCore;
//import ru.timeconqueror.timecore.animation.renderer.AnimatedTileEntityRenderer;
//import ru.timeconqueror.timecore.animation_example.block_example.block.tile.TileHeatCube;
//import ru.timeconqueror.timecore.animation_example.block_example.registry.TileModels;
//import ru.timeconqueror.timecore.api.client.resource.location.TextureLocation;
//
//public class TERHeatCube extends AnimatedTileEntityRenderer<TileHeatCube> {
//    public TERHeatCube(TileEntityRendererDispatcher rendererDispatcherIn) {
//        super(rendererDispatcherIn, TileModels.heatCubeModel);
//    }
//
//    @Override
//    protected ResourceLocation getTexture(TileHeatCube tileEntityIn) {
//        return new TextureLocation(TimeCore.MODID, "tileentity/spark_smelter.png").fullLocation();
//    }
//}
