package ru.timeconqueror.timecore.animation.builders;

import ru.timeconqueror.timecore.animation.*;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.builders.IAnimationManagerBuilder;
import ru.timeconqueror.timecore.api.util.SingleUseBuilder;

import java.util.LinkedHashMap;

public class BaseAnimationManagerBuilder extends SingleUseBuilder implements IAnimationManagerBuilder {
    private final LinkedHashMap<String, Layer> animationLayers = new LinkedHashMap<>();

    @Override
    public void addLayer(String name, BlendType blendType, float weight) {
        verifyNotUsed();
        Layer prev = animationLayers.put(name, new Layer(name, blendType, weight));
        if (prev != null) {
            throw new IllegalArgumentException("Layer with location " + name + " already exist in provided animation manager.");
        }
    }

    @Override
    public void addMainLayer() {
        verifyNotUsed();
        addLayer(AnimationConstants.MAIN_LAYER_NAME, BlendType.OVERWRITE, 1);
    }

    @Override
    public void addLayer(Layer layer) {
        verifyNotUsed();
        Layer prev = animationLayers.put(layer.getName(), layer.copy());
        if (prev != null) {
            throw new IllegalArgumentException("Layer with location " + layer.getName() + " already exist in provided animation manager.");
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
