package ru.timeconqueror.timecore.api.animation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.builders.AnimationManagerBuilderImpl;
import ru.timeconqueror.timecore.animation.network.BlockEntityNetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.EntityNetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.animation.predefined.EmptyPredefinedAnimationManager;
import ru.timeconqueror.timecore.animation.predefined.EntityPredefinedAnimationManager;
import ru.timeconqueror.timecore.animation.predefined.EntityPredefinedAnimations;
import ru.timeconqueror.timecore.api.animation.builders.AnimationManagerBuilder;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.Objects;
import java.util.function.Consumer;

public class AnimationSystems {
    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(
            T entity,
            @Nullable EntityPredefinedAnimations entityPredefinedAnimations,
            Consumer<AnimationManagerBuilder> animationManagerTuner
    ) {
        PredefinedAnimationManager<T> predefinedManager = EmptyPredefinedAnimationManager.empty();
        if (entityPredefinedAnimations != null) {
            predefinedManager = new EntityPredefinedAnimationManager<>(entityPredefinedAnimations);
        }

        return custom(entity,
                new EntityNetworkDispatcher<>(),
                entity.level().isClientSide,
                predefinedManager,
                animationManagerTuner);
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystem<T> forBlockEntity(
            T blockEntity,
            Consumer<AnimationManagerBuilder> animationManagerTuner
    ) {
        return custom(blockEntity,
                new BlockEntityNetworkDispatcher<>(),
                Objects.requireNonNull(blockEntity.getLevel()).isClientSide,
                EmptyPredefinedAnimationManager.empty(),
                animationManagerTuner);
    }

    public static <T extends AnimatedObject<T>> AnimationSystem<T> custom(
            T object,
            NetworkDispatcher<T> networkDispatcher,
            boolean clientSide,
            PredefinedAnimationManager<T> predefinedAnimationManager,
            Consumer<? super AnimationManagerBuilderImpl> animationManagerTuner
    ) {
        AnimationManagerBuilderImpl animationManagerBuilder = new AnimationManagerBuilderImpl();
        animationManagerTuner.accept(animationManagerBuilder);

        SharedMolangObject sharedObjects = new SharedMolangObject();
        object.populateMolangObjects(new MolangObjectFiller(sharedObjects));

        NetworkDispatcherInstance<T> networkDispatcherInstance = new NetworkDispatcherInstance<>(networkDispatcher, object);

        BaseAnimationManager animationManager = animationManagerBuilder.build(clientSide, sharedObjects, networkDispatcherInstance);

        var api = new AnimationSystemAPI<T>();
        var system = new AnimationSystem<T>(object, animationManager, networkDispatcherInstance, api, predefinedAnimationManager);
        api.setSystem(system);

        return system;
    }
}
