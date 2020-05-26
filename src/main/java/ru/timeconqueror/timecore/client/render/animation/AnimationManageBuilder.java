package ru.timeconqueror.timecore.client.render.animation;

import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationManager;

import java.util.HashMap;

public class AnimationManageBuilder {
    private HashMap<String, Layer> animationLayers = new HashMap<>();
    private boolean used = false;

    public AnimationManageBuilder addLayer(String name, int priority, BlendType blendType, int weight) {
        verifyNotUsed();
        animationLayers.put(name, new Layer(priority, blendType, weight));
        return this;
    }

    public IAnimationManager build() {
        verifyNotUsed();

        if (animationLayers.isEmpty()) {
            animationLayers.put(AnimationConstants.MAIN_LAYER_NAME, new Layer(0, BlendType.OVERRIDE, 1));
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
