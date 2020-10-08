package ru.timeconqueror.timecore.api.animation.builders;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

import java.util.function.Consumer;

public class AnimationSystemBuilder {
    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(T entity, World world) {
        //@formatter:off
        return forEntity(entity, world, managerBuilder -> {});
        //@formatter:on
    }

    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(T entity, World world, Consumer<IAnimationManagerBuilder> animationManagerTuner) {
        //@formatter:off
        return forEntity(entity, world, animationManagerTuner, predefinedAnimations -> {});
        //@formatter:on
    }

    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(
            T entity,
            World world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IPredefinedAnimations.IEntityPredefinedAnimations> predefinedAnimationsTuner) {
        return AnimationSystemBuilder.forEntity(entity, world, animationManagerTuner, predefinedAnimationsTuner);
    }

    public static <T extends TileEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(T tileEntity, World world) {
        //@formatter:off
        return forTileEntity(tileEntity, world, managerBuilder -> {});
        //@formatter:on
    }

    public static <T extends TileEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(T tileEntity, World world, Consumer<IAnimationManagerBuilder> animationManagerTuner) {
        //@formatter:off
        return forTileEntity(tileEntity, world, animationManagerTuner, predefinedAnimations -> {});
        //@formatter:on
    }

    public static <T extends TileEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(
            T tileEntity,
            World world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IPredefinedAnimations> predefinedAnimationsTuner
    ) {
        return AnimationSystemBuilder.forTileEntity(tileEntity, world, animationManagerTuner, predefinedAnimationsTuner);
    }
}
