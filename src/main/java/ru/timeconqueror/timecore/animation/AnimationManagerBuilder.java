package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import ru.timeconqueror.timecore.api.animation.StateMachine;
import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.client.render.animation.AnimationConstants;
import ru.timeconqueror.timecore.util.SingleUseBuilder;

import java.util.HashMap;

public class AnimationManagerBuilder extends SingleUseBuilder {
    private final HashMap<String, Layer> animationLayers = new HashMap<>();

    public AnimationManagerBuilder addLayer(String name, int priority, BlendType blendType, float weight) {
        verifyNotUsed();
        Layer prev = animationLayers.put(name, new Layer(priority, blendType, weight));
        if (prev != null) throw new IllegalArgumentException("Layer with name " + name + " is already registered.");
        return this;
    }

    public AnimationManagerBuilder addMainLayer() {
        verifyNotUsed();
        addLayer(AnimationConstants.MAIN_LAYER_NAME, 0, BlendType.OVERRIDE, 1);

        return this;
    }

    BaseAnimationManager build(boolean serverSide) {
        BaseAnimationManager manager = serverSide ? new ServerAnimationManager<>() : new ClientAnimationManager();

        if (animationLayers.isEmpty()) {
            addMainLayer();
        }

        manager.setLayers(animationLayers);

        setUsed();

        return manager;
    }

    @SuppressWarnings("unchecked")
    <T extends Entity> void init(BaseAnimationManager manager, StateMachine<T> stateMachine) {
        if (manager instanceof ServerAnimationManager) {
            ((ServerAnimationManager<T>) manager).setStateMachine((StateMachineImpl<T>) stateMachine);
        }
    }
}
