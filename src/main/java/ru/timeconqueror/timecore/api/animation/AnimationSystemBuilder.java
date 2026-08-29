package ru.timeconqueror.timecore.api.animation;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.*;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
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

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class AnimationSystemBuilder<T extends AnimatedObject<T>> {
    private final T object;
    private final boolean clientSide;
    private final NetworkDispatcher<T> networkDispatcher;
    private final PredefinedAnimationManager<T> predefinedAnimationManager;
    private final boolean syncAnimationsOnFirstTick;

    private final List<LayerDefinition> layers = new ArrayList<>(1);
    private Clock clock = new TickBasedClock();

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
                predefinedManager,
                false);
    }

    public static <T extends BlockEntity & AnimatedObject<T>> AnimationSystemBuilder<T> forBlockEntity(T blockEntity) {
        Level level = blockEntity.getLevel();

        return create(blockEntity,
                new BlockEntityNetworkDispatcher<>(),
                level == null /* for guis */ || level.isClientSide(),
                EmptyPredefinedAnimationManager.empty(),
                true);
    }

    public static <T extends AnimatedObject<T>> AnimationSystemBuilder<T> create(T object,
                                                                                 NetworkDispatcher<T> networkDispatcher,
                                                                                 boolean clientSide,
                                                                                 PredefinedAnimationManager<T> predefinedAnimationManager,
                                                                                 boolean syncAnimationsOnFirstTick) {
        return new AnimationSystemBuilder<>(object, clientSide, networkDispatcher, predefinedAnimationManager, syncAnimationsOnFirstTick);
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

        BaseAnimationManager animationManager = makeAnimationManager(clientSide, clock, sharedObjects, networkDispatcherInstance);
        ActionManagerImpl actionManager = ActionManagerImpl.create(animationManager.getLayers(), () -> new LayerActionManager(object, predefinedActionManagerImpl));

        return new AnimationSystemImpl<>(object,
                clientSide,
                clock,
                animationManager,
                networkDispatcherInstance,
                predefinedAnimationManager,
                predefinedActionManagerImpl,
                actionManager);
    }

    private BaseAnimationManager makeAnimationManager(boolean clientSide, Clock clock, SharedMolangObject sharedMolangObject, NetworkDispatcherInstance<T> networkDispatcherInstance) {
        BaseAnimationManager manager;
        if (!clientSide) {
            manager = new ServerAnimationManager<>(clock, sharedMolangObject, networkDispatcherInstance, syncAnimationsOnFirstTick);
        } else {
            manager = new ClientAnimationManager(clock, sharedMolangObject);
        }

        if (this.layers.isEmpty()) {
            withMainAnimationLayer();
        }

        manager.init(layers);

        return manager;
    }
}
