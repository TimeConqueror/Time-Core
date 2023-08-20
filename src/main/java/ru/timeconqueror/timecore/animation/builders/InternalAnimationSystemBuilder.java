package ru.timeconqueror.timecore.animation.builders;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.EnumAnimatedObjectType;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
import ru.timeconqueror.timecore.animation.action.EntityActionManager;
import ru.timeconqueror.timecore.animation.action.TileEntityActionManager;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations.Builder;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations.EntityPredefinedAnimations;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.MolangObjectFiller;
import ru.timeconqueror.timecore.api.animation.builders.IAnimationManagerBuilder;
import ru.timeconqueror.timecore.api.animation.builders.IPredefinedAnimations;
import ru.timeconqueror.timecore.api.animation.builders.IPredefinedAnimations.IEntityPredefinedAnimations;
import ru.timeconqueror.timecore.molang.MolangSharedObjects;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class InternalAnimationSystemBuilder {
    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(
            T entity,
            Level world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IEntityPredefinedAnimations> predefinedAnimationsTuner
    ) {
        return create(entity, EnumAnimatedObjectType.ENTITY, world, animationManagerTuner, (animationManager) -> {
            Builder<EntityPredefinedAnimations> predefinedAnimsBuilder = Builder.of(new EntityPredefinedAnimations());
            predefinedAnimationsTuner.accept(predefinedAnimsBuilder.getInner());
            EntityPredefinedAnimations validatedPredefines = predefinedAnimsBuilder.validate(animationManager);

            return new EntityActionManager<>(animationManager, entity, validatedPredefines);
        });
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystem<T> forTileEntity(
            T tileEntity,
            Level world,
            Consumer<IAnimationManagerBuilder> animationManagerTuner,
            Consumer<IPredefinedAnimations> predefinedAnimationsTuner
    ) {
        return create(tileEntity, EnumAnimatedObjectType.TILE_ENTITY, world, animationManagerTuner, (animationManager) -> {
            Builder<PredefinedAnimations> predefinedAnimsBuilder = Builder.of(new PredefinedAnimations());
            predefinedAnimationsTuner.accept(predefinedAnimsBuilder.getInner());
            PredefinedAnimations validatedPredefines = predefinedAnimsBuilder.validate(animationManager);

            return new TileEntityActionManager<>(animationManager, tileEntity, validatedPredefines);
        });
    }

    private static <T extends AnimatedObject<T>> AnimationSystem<T> create(
            AnimatedObject<T> object,
            EnumAnimatedObjectType type,
            Level world,
            Consumer<? super BaseAnimationManagerBuilder> animationManagerTuner,
            Function<BaseAnimationManager, ? extends ActionManagerImpl<T>> actionManagerBuilderFactory
    ) {
        BaseAnimationManagerBuilder animationManagerBuilder = new BaseAnimationManagerBuilder();
        animationManagerTuner.accept(animationManagerBuilder);

        MolangSharedObjects sharedObjects = new MolangSharedObjects();
        object.populateMolangObjects(new MolangObjectFiller(sharedObjects));

        BaseAnimationManager animationManager = animationManagerBuilder.build(!world.isClientSide(), type, sharedObjects);

        ActionManagerImpl<T> actionManager = actionManagerBuilderFactory.apply(animationManager);

        animationManagerBuilder.init(animationManager, actionManager);

        return new AnimationSystem<>(actionManager, animationManager);
    }
}
