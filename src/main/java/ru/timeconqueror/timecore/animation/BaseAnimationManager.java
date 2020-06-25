package ru.timeconqueror.timecore.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseAnimationManager implements AnimationManager {
    private HashMap<String, Layer> layerMap;
    private List<Layer> layers;

    @Nullable
    private final IAnimation walkingAnimation;

    public BaseAnimationManager(@Nullable IAnimation walkingAnimation) {
        this.walkingAnimation = walkingAnimation;
    }

    @Override
    public boolean containsLayer(String name) {
        return layerMap.get(name) != null;
    }

    @NotNull
    @Override
    public Layer getLayer(String name) {
        Layer layer = layerMap.get(name);
        if (layer == null) throw new RuntimeException("There is no layer with name " + name);
        return layer;
    }

    @Override
    public @NotNull Layer getMainLayer() {
        return layerMap.containsKey(AnimationConstants.MAIN_LAYER_NAME) ? layerMap.get(AnimationConstants.MAIN_LAYER_NAME) : layers.get(0) /*won't be empty, because we check it in AnimationStarter*/;
    }

    @Override
    public void applyAnimations(TimeEntityModel<?> model) {
        for (Layer layer : layers) {
            AnimationWatcher watcher = layer.getAnimationWatcher();

            boolean paused = isGamePaused();
            long currentTime = System.currentTimeMillis();

            if (watcher != null) {
                if (paused) {
                    watcher.freeze();
                } else {
                    watcher.unfreeze();

                    watcher.onFrame(model);

                    if (watcher.isAnimationEnded(currentTime)) {
                        onAnimationEnd(model, layer, watcher, currentTime);

                        if (watcher.getAnimation().isLooped()) {
                            watcher.resetTimer();
                        } else {
                            watcher = watcher.next();
                        }
                    }
                }
            }

            layer.setAnimationWatcher(watcher); //here we update current watcher

            if (watcher != null) {
                applyAnimation(model, layer, watcher, currentTime);
            }
        }
    }

    protected abstract void applyAnimation(TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime);

    protected abstract boolean isGamePaused();

    protected void onAnimationEnd(TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime) {

    }

    void setLayers(HashMap<String, Layer> layers) {
        layerMap = layers;

        this.layers = layers.values().stream()
                .sorted(Comparator.comparingInt(Layer::getPriority))
                .collect(Collectors.toList());
    }

    public @Nullable IAnimation getWalkingAnimation() {
        return walkingAnimation;
    }
}
