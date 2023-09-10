package ru.timeconqueror.timecore.api.animation;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.builders.AnimationManagerBuilderImpl;
import ru.timeconqueror.timecore.animation.network.BlockEntityNetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.EntityNetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.builders.AnimationManagerBuilder;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.Objects;
import java.util.function.Consumer;

public class AnimationSystems {
    public static <T extends Entity & AnimatedObject<T>> AnimationSystem<T> forEntity(
            T entity,
            Consumer<AnimationManagerBuilder> animationManagerTuner
    ) {
        //FIXME re-implement predefined animations
        return custom(entity, new EntityNetworkDispatcher<>(), entity.level().isClientSide, animationManagerTuner);
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystem<T> forBlockEntity(
            T blockEntity,
            Consumer<AnimationManagerBuilder> animationManagerTuner
    ) {
        //FIXME re-implement predefined animations
        return custom(blockEntity, new BlockEntityNetworkDispatcher<>(), Objects.requireNonNull(blockEntity.getLevel()).isClientSide, animationManagerTuner);
    }

    public static <T extends AnimatedObject<T>> AnimationSystem<T> custom(
            T object,
            NetworkDispatcher<T> networkDispatcher,
            boolean clientSide,
            Consumer<? super AnimationManagerBuilderImpl> animationManagerTuner
    ) {
        AnimationManagerBuilderImpl animationManagerBuilder = new AnimationManagerBuilderImpl();
        animationManagerTuner.accept(animationManagerBuilder);

        SharedMolangObject sharedObjects = new SharedMolangObject();
        object.populateMolangObjects(new MolangObjectFiller(sharedObjects));

        NetworkDispatcherInstance<T> networkDispatcherInstance = new NetworkDispatcherInstance<>(networkDispatcher, object);

        BaseAnimationManager animationManager = animationManagerBuilder.build(clientSide, sharedObjects, networkDispatcherInstance);

        var api = new AnimationSystemAPI<T>();
        var system = new AnimationSystem<>(animationManager, networkDispatcherInstance, api);
        api.setSystem(system);

        return system;
    }
}
