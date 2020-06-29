package ru.timeconqueror.timecore.animation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.animation.watcher.TransitionWatcher;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public abstract class BaseAnimationManager implements AnimationManager {
    @Nullable
    private final AnimationSetting walkingAnimationSetting;
    private HashMap<String, Layer> layerMap;
    private List<Layer> layers;

    public BaseAnimationManager(@Nullable AnimationSetting walkingAnimationSetting) {
        this.walkingAnimationSetting = walkingAnimationSetting;
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
    public void setAnimation(AnimationStarter animationStarter, String layerName) {
        AnimationStarter.AnimationData data = animationStarter.getData();
        if (containsLayer(layerName)) {
            Layer layer = getLayer(layerName);

            if (data.isIgnorable()) {
                AnimationWatcher watcher = layer.getAnimationWatcher();
                if (watcher != null) {
                    Animation animationIn = data.getAnimation();

                    if (animationIn.equals(watcher.getAnimation()) || (watcher instanceof TransitionWatcher && animationIn.equals(((TransitionWatcher) watcher).getDestination()))) {
                        return;//TODO add check for speed
                    }
                }
            }

            layer.setAnimation(data);
            onAnimationSet(data, layer);
        } else {
            TimeCore.LOGGER.error("Can't start animation: layer with name " + layerName + " doesn't exist in provided animation manager.");
        }
    }

    protected void onAnimationSet(AnimationStarter.AnimationData data, Layer layer) {

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

            onAnimationEnd(null, layer, layer.getAnimationWatcher(), System.currentTimeMillis());
        } else {
            TimeCore.LOGGER.error("Can't find layer with name " + layerName);
        }
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

                    if (watcher.requiresInit()) {
                        watcher.init(model);
                    }

                    if (watcher.isAnimationEnded(currentTime)) {
                        onAnimationEnd(model, layer, watcher, currentTime);

                        watcher = watcher.next();

                        if (watcher != null && watcher.requiresInit()) {
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

    protected void onAnimationEnd(@Nullable TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime) {

    }

    void setLayers(HashMap<String, Layer> layers) {
        layerMap = layers;

        this.layers = layers.values().stream()
                .sorted(Comparator.comparingInt(Layer::getPriority))
                .collect(Collectors.toList());
    }

    @Nullable
    public AnimationSetting getWalkingAnimationSetting() {
        return walkingAnimationSetting;
    }
}
