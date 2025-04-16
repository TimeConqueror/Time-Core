package ru.timeconqueror.timecore.api.animation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.*;
import ru.timeconqueror.timecore.animation.action.LayerActionManager;
import ru.timeconqueror.timecore.animation.action.PredefinedActionManagerImpl;
import ru.timeconqueror.timecore.animation.clock.TickBasedClock;
import ru.timeconqueror.timecore.animation.network.BlockEntityNetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.EntityNetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcher;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.animation.predefined.EmptyPredefinedAnimationManager;
import ru.timeconqueror.timecore.animation.predefined.EntityPredefinedAnimationManager;
import ru.timeconqueror.timecore.animation.predefined.EntityPredefinedAnimations;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AnimationSystemBuilder<T extends AnimatedObject<T>> {
    private final T object;
    private final Boolean clientSide;
    private final NetworkDispatcher<T> networkDispatcher;
    private final PredefinedAnimationManager<T> predefinedAnimationManager;

    private Clock clock = new TickBasedClock();
    private List<LayerDefinition> layers = new ArrayList<>(1);

    public static <T extends Entity & AnimatedObject<T>> AnimationSystemBuilder<T> forEntity(
            T entity,
            @Nullable EntityPredefinedAnimations entityPredefinedAnimations
    ) {
        PredefinedAnimationManager<T> predefinedManager = EmptyPredefinedAnimationManager.empty();
        if (entityPredefinedAnimations != null) {
            predefinedManager = new EntityPredefinedAnimationManager<>(entityPredefinedAnimations);
        }

        Level level = entity.level();
        return create(entity,
                new EntityNetworkDispatcher<>(),
                level == null || level.isClientSide(),
                predefinedManager);
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystemBuilder<T> forBlockEntity(T blockEntity) {
        Level level = blockEntity.getLevel();

        return create(blockEntity,
                new BlockEntityNetworkDispatcher<>(),
                level == null /* for guis */ || level.isClientSide(),
                EmptyPredefinedAnimationManager.empty());
    }

    public static <T extends AnimatedObject<T>> AnimationSystemBuilder<T> create(T object,
                                                                                 NetworkDispatcher<T> networkDispatcher,
                                                                                 boolean clientSide,
                                                                                 PredefinedAnimationManager<T> predefinedAnimationManager) {
        return new AnimationSystemBuilder<>(object, clientSide, networkDispatcher, predefinedAnimationManager);
    }

    public AnimationSystemBuilder<T> withAnimationLayer(LayerDefinition layer) {
        layers.add(layer);
        return this;
    }

    public AnimationSystemBuilder<T> withMainAnimationLayer() {
        return withAnimationLayer(new LayerDefinition(AnimationConstants.MAIN_LAYER_NAME, BlendType.OVERWRITE, 1));
    }

    public AnimationSystemBuilder<T> withClock(Clock clock) {
        this.clock = clock;
        return this;
    }

    public AnimationSystem<T> build() {
        SharedMolangObject sharedObjects = new SharedMolangObject();
        object.populateMolangObjects(new MolangObjectFiller(sharedObjects));

        PredefinedActionManagerImpl<T> predefinedActionManagerImpl = new PredefinedActionManagerImpl<>(object, clientSide);
        NetworkDispatcherInstance<T> networkDispatcherInstance = new NetworkDispatcherInstance<>(networkDispatcher, object);

        Supplier<LayerActionManager> actionManagerFactory = () -> new LayerActionManager(object, predefinedActionManagerImpl);

        BaseAnimationManager animationManager = makeAnimationManager(clientSide, clock, sharedObjects, actionManagerFactory, networkDispatcherInstance, predefinedActionManagerImpl);

        return new AnimationSystemImpl<>(object, clientSide, clock, animationManager, networkDispatcherInstance, predefinedAnimationManager, predefinedActionManagerImpl);
    }

    private <T extends AnimatedObject<T>> BaseAnimationManager makeAnimationManager(boolean clientSide, Clock clock, SharedMolangObject sharedMolangObject, Supplier<LayerActionManager> actionManagerFactory, NetworkDispatcherInstance<T> networkDispatcherInstance, PredefinedActionManagerImpl<T> predefinedActionManagerImpl) {
        BaseAnimationManager manager;
        if (!clientSide) {
            manager = new ServerAnimationManager<>(clock, actionManagerFactory, sharedMolangObject, networkDispatcherInstance);
        } else {
            manager = new ClientAnimationManager(clock, actionManagerFactory, sharedMolangObject);
        }

        if (this.layers.isEmpty()) {
            withMainAnimationLayer();
        }

        manager.init(layers);

        return manager;
    }
}
