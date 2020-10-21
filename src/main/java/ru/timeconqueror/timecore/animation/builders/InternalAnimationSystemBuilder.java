package ru.timeconqueror.timecore.animation.builders;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.EnumAnimatedObjectType;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
import ru.timeconqueror.timecore.animation.action.EntityActionManager;
import ru.timeconqueror.timecore.animation.action.TileEntityActionManager;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations.Builder;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations.EntityPredefinedAnimations;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.builders.IAnimationManagerBuilder;
import ru.timeconqueror.timecore.api.animation.builders.IPredefinedAnimations;
import ru.timeconqueror.timecore.api.animation.builders.IPredefinedAnimations.IEntityPredefinedAnimations;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class InternalAnimationSystemBuilder {
    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(
            T entity,
            World world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IEntityPredefinedAnimations> predefinedAnimationsTuner
    ) {
        return create(EnumAnimatedObjectType.ENTITY, world, animationManagerTuner, (animationManager) -> {
            Builder<EntityPredefinedAnimations> predefinedAnimsBuilder = Builder.of(new EntityPredefinedAnimations());
            predefinedAnimationsTuner.accept(predefinedAnimsBuilder.getInner());
            EntityPredefinedAnimations validatedPredefines = predefinedAnimsBuilder.validate(animationManager);

            return new EntityActionManager<>(animationManager, entity, validatedPredefines);
        });
    }

    public static <T extends TileEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(
            T tileEntity,
            World world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IPredefinedAnimations> predefinedAnimationsTuner
    ) {
        return create(EnumAnimatedObjectType.TILE_ENTITY, world, animationManagerTuner, (animationManager) -> {
            Builder<PredefinedAnimations> predefinedAnimsBuilder = Builder.of(new PredefinedAnimations());
            predefinedAnimationsTuner.accept(predefinedAnimsBuilder.getInner());
            PredefinedAnimations validatedPredefines = predefinedAnimsBuilder.validate(animationManager);

            return new TileEntityActionManager<>(animationManager, tileEntity, validatedPredefines);
        });
    }

    private static <T extends AnimatedObject<T>> AnimationSystem<T> create(
            EnumAnimatedObjectType type,
            World world,
            Consumer<? super BaseAnimationManagerBuilder> animationManagerTuner,
            Function<BaseAnimationManager, ? extends ActionManagerImpl<T>> actionManagerBuilderFactory
    ) {
        BaseAnimationManagerBuilder animationManagerBuilder = new BaseAnimationManagerBuilder();
        animationManagerTuner.accept(animationManagerBuilder);

        BaseAnimationManager animationManager = animationManagerBuilder.build(!world.isClientSide(), type);

        ActionManagerImpl<T> actionManager = actionManagerBuilderFactory.apply(animationManager);

        animationManagerBuilder.init(animationManager, actionManager);

        return new AnimationSystem<>(actionManager, animationManager);
    }
}
