package ru.timeconqueror.timecore.animation.common;

import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationManager;
import ru.timeconqueror.timecore.client.render.animation.AnimationConstants;

import java.util.HashMap;

public class AnimationManageBuilder {
    private final HashMap<String, Layer> animationLayers = new HashMap<>();
    private boolean used = false;

    public AnimationManageBuilder addLayer(String name, int priority, BlendType blendType, float weight) {
        verifyNotUsed();
        Layer prev = animationLayers.put(name, new Layer(priority, blendType, weight));
        if (prev != null) throw new IllegalArgumentException("Layer with name " + name + " is already registered.");
        return this;
    }

    public AnimationManageBuilder addMainLayer() {
        addLayer(AnimationConstants.MAIN_LAYER_NAME, 0, BlendType.OVERRIDE, 1);

        return this;
    }

    public IAnimationManager build() {
        verifyNotUsed();

        if (animationLayers.isEmpty()) {
            addMainLayer();
        }

        AnimationManager manager = new AnimationManager();
        manager.setLayers(animationLayers);

        used = true;

        return manager;
    }

    private void verifyNotUsed() {
        if (used) throw new IllegalStateException("Builder can't be used after it created animation manager.");
    }
}
