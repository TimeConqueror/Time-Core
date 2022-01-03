//package ru.timeconqueror.timecore.animation_example.block_example.block.tile;
//
//import net.minecraft.tileentity.ITickableTileEntity;
//import net.minecraft.tileentity.TileEntity;
//import org.jetbrains.annotations.NotNull;
//import ru.timeconqueror.timecore.animation.AnimationStarter;
//import ru.timeconqueror.timecore.animation.AnimationSystem;
//import ru.timeconqueror.timecore.animation_example.block_example.registry.ATileRegistry;
//import ru.timeconqueror.timecore.animation_example.block_example.registry.TileAnimations;
//import ru.timeconqueror.timecore.api.animation.AnimatedObject;
//import ru.timeconqueror.timecore.api.animation.AnimationConstants;
//import ru.timeconqueror.timecore.api.animation.builders.AnimationSystemBuilder;
//
//public class TileHeatCube extends TileEntity implements AnimatedObject<TileHeatCube>, ITickableTileEntity {
//    private AnimationSystem<TileHeatCube> animationSystem;
//
//    public TileHeatCube() {
//        super(ATileRegistry.HEAT_CUBE);
//    }
//
//    @Override
//    public void onLoad() {
//        animationSystem = AnimationSystemBuilder.forTileEntity(this, level);
//    }
//
//    @Override
//    public @NotNull AnimationSystem<TileHeatCube> getSystem() {
//        return animationSystem;
//    }
//
//    @Override
//    public void tick() {
//        if (getLevel().isClientSide) {
//            new AnimationStarter(TileAnimations.heatCubeIdle).setIgnorable(true).startAt(getAnimationManager(), AnimationConstants.MAIN_LAYER_NAME);
//        }
//    }
//}
