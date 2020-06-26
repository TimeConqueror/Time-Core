package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.MobEntity;
import ru.timeconqueror.timecore.api.animation.StateMachine;
import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.util.SingleUseBuilder;

import java.util.HashMap;

public class AnimationManagerBuilder extends SingleUseBuilder {
    private final HashMap<String, Layer> animationLayers = new HashMap<>();
    private AnimationStarter walkingAnimationStarter;

    public AnimationManagerBuilder(boolean setupDefaultLayer) {
        if (setupDefaultLayer) {
            addLayer(LayerReference.WALKING.createLayerFromDefault());
        }
    }

    public AnimationManagerBuilder setWalkingAnimationStarter(AnimationStarter walkingAnimationStarter) {
        this.walkingAnimationStarter = walkingAnimationStarter;
        return this;
    }

    public AnimationManagerBuilder addLayer(String name, int priority, BlendType blendType, float weight) {
        verifyNotUsed();
        Layer prev = animationLayers.put(name, new Layer(name, priority, blendType, weight));
        if (prev != null)
            throw new IllegalArgumentException("Layer with name " + name + " already exist in provided animation manager.");
        return this;
    }

    public AnimationManagerBuilder addMainLayer() {
        verifyNotUsed();
        addLayer(AnimationConstants.MAIN_LAYER_NAME, 0, BlendType.OVERRIDE, 1);

        return this;
    }

    private AnimationManagerBuilder addLayer(Layer layer) {
        verifyNotUsed();
        try {
            Layer prev = animationLayers.put(layer.getName(), layer.clone());
            if (prev != null)
                throw new IllegalArgumentException("Layer with name " + layer.getName() + " already exist in provided animation manager.");

            return this;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    BaseAnimationManager build(boolean serverSide) {
        BaseAnimationManager manager = serverSide ? new ServerAnimationManager<>(walkingAnimationStarter) : new ClientAnimationManager(walkingAnimationStarter);

        if (animationLayers.isEmpty()) {
            addMainLayer();
        }

        manager.setLayers(animationLayers);

        setUsed();

        return manager;
    }

    @SuppressWarnings("unchecked")
    <T extends MobEntity> void init(BaseAnimationManager manager, StateMachine<T> stateMachine) {
        if (manager instanceof ServerAnimationManager) {
            ((ServerAnimationManager<T>) manager).setStateMachine((StateMachineImpl<T>) stateMachine);
        }
    }
}
