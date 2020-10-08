package ru.timeconqueror.timecore.animation.builders;

import ru.timeconqueror.timecore.animation.*;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.util.SingleUseBuilder;

import java.util.LinkedHashMap;

public class BaseAnimationManagerBuilder extends SingleUseBuilder implements IAnimationManagerBuilder {
    private final LinkedHashMap<String, Layer> animationLayers = new LinkedHashMap<>();

    @Override
    public void addLayer(String name, BlendType blendType, float weight) {
        verifyNotUsed();
        Layer prev = animationLayers.put(name, new Layer(name, blendType, weight));
        if (prev != null)
            throw new IllegalArgumentException("Layer with name " + name + " already exist in provided animation manager.");
    }

    @Override
    public void addMainLayer() {
        verifyNotUsed();
        addLayer(AnimationConstants.MAIN_LAYER_NAME, BlendType.OVERRIDE, 1);
    }

    private void addLayer(Layer layer) {
        verifyNotUsed();
        try {
            Layer prev = animationLayers.put(layer.getName(), layer.clone());
            if (prev != null)
                throw new IllegalArgumentException("Layer with name " + layer.getName() + " already exist in provided animation manager.");
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    protected LinkedHashMap<String, Layer> getAnimationLayers() {
        return animationLayers;
    }

    BaseAnimationManager build(boolean serverSide, EnumAnimatedObjectType type) {
        BaseAnimationManager manager;
        if (serverSide) {
            manager = new ServerAnimationManager<>(type.getNetworkDispatcher());
        } else {
            manager = new ClientAnimationManager();
        }

        if (animationLayers.isEmpty()) {
            addMainLayer();
        }

        manager.buildLayers(animationLayers);

        setUsed();

        return manager;
    }

    @SuppressWarnings("unchecked")
    <T extends AnimatedObject<T>> void init(BaseAnimationManager manager, ActionManagerImpl<T> actionManager) {
        if (manager instanceof ServerAnimationManager) {
            ((ServerAnimationManager<T>) manager).setActionManager(actionManager);
        }
    }
}
