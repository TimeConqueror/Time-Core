package ru.timeconqueror.timecore.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseAnimationManager implements AnimationManager {
    private HashMap<String, Layer> layerMap;
    private List<Layer> layers;
    @Nullable
    private final AnimationStarter walkingAnimationStarter;

    public BaseAnimationManager(@Nullable AnimationStarter walkingAnimationStarter) {
        this.walkingAnimationStarter = walkingAnimationStarter;
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
    public void setAnimation(AnimationStarter.AnimationData animationData, String layerName) {
        if (containsLayer(layerName)) {
            getLayer(layerName).setAnimation(animationData);
        } else {
            TimeCore.LOGGER.error("Can't start animation: layer with name " + layerName + " doesn't exist in provided animation manager.");
        }
    }

    @Override
    public void removeAnimation(String layerName) {
        removeAnimation(layerName, AnimationConstants.BASIC_TRANSITION_TIME);
    }

    @Override
    public void removeAnimation(String layerName, int transitionTime) {
        if (containsLayer(layerName)) {
            Layer layer = getLayer(layerName);
            layer.removeAnimation(transitionTime);

            onAnimationEnd(DummyElements.DUMMY_ENTITY_MODEL, layer, layer.getAnimationWatcher(), System.currentTimeMillis());
        } else {
            TimeCore.LOGGER.error("Can't find layer with name " + layerName);
        }
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

                    if (!watcher.isInited()) {
                        watcher.init(model);
                    }

                    if (watcher.isAnimationEnded(currentTime)) {
                        onAnimationEnd(model, layer, watcher, currentTime);

                        watcher = watcher.next();

                        if (watcher != null && !watcher.isInited()) {
                            watcher.init(model);
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

    @Nullable
    public AnimationStarter getWalkingAnimationStarter() {
        return walkingAnimationStarter;
    }
}
