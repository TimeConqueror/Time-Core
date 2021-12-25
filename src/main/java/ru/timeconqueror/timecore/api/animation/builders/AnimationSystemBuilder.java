package ru.timeconqueror.timecore.api.animation.builders;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.builders.InternalAnimationSystemBuilder;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

import java.util.function.Consumer;

public class AnimationSystemBuilder {
    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(T entity, Level world) {
        //@formatter:off
        return forEntity(entity, world, managerBuilder -> {});
        //@formatter:on
    }

    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(T entity, Level world, Consumer<IAnimationManagerBuilder> animationManagerTuner) {
        //@formatter:off
        return forEntity(entity, world, animationManagerTuner, predefinedAnimations -> {});
        //@formatter:on
    }

    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(
            T entity,
            Level world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IPredefinedAnimations.IEntityPredefinedAnimations> predefinedAnimationsTuner) {
        return InternalAnimationSystemBuilder.forEntity(entity, world, animationManagerTuner, predefinedAnimationsTuner);
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(T tileEntity, Level world) {
        //@formatter:off
        return forTileEntity(tileEntity, world, managerBuilder -> {});
        //@formatter:on
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(T tileEntity, Level world, Consumer<IAnimationManagerBuilder> animationManagerTuner) {
        //@formatter:off
        return forTileEntity(tileEntity, world, animationManagerTuner, predefinedAnimations -> {});
        //@formatter:on
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(
            T tileEntity,
            Level world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IPredefinedAnimations> predefinedAnimationsTuner
    ) {
        return InternalAnimationSystemBuilder.forTileEntity(tileEntity, world, animationManagerTuner, predefinedAnimationsTuner);
    }
}
