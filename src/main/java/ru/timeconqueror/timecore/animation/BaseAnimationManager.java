package ru.timeconqueror.timecore.animation;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.animation.watcher.TransitionWatcher;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.builders.LayerDefinition;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.molang.MolangSharedObjects;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class BaseAnimationManager implements AnimationManager {
    @Getter
    private final MolangSharedObjects molangSharedObjects;
    private Map<String, Layer> layerMap;

    public BaseAnimationManager(MolangSharedObjects molangSharedObjects) {
        this.molangSharedObjects = molangSharedObjects;
    }

    @Override
    public boolean containsLayer(String name) {
        return layerMap.get(name) != null;
    }

    @NotNull
    @Override
    public Layer getLayer(String name) {
        Layer layer = layerMap.get(name);
        if (layer == null) throw new RuntimeException("There is no layer with location " + name);
        return layer;
    }

    @Override
    public Set<String> getLayerNames() {
        return layerMap.keySet();
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
        } else {
            TimeCore.LOGGER.error("Can't start animation: layer with location " + layerName + " doesn't exist in provided animation manager.");
        }
    }

    protected void onAnimationStart(Layer layer, AnimationStarter.AnimationData data, AnimationWatcher watcher) {

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
        } else {
            TimeCore.LOGGER.error("Can't find layer with location " + layerName);
        }
    }

    @Override
    public void applyAnimations(ITimeModel model) {
        long currentTime = System.currentTimeMillis();
        for (Layer layer : layerMap.values()) {
            layer.update(this, model, currentTime);

            var watcher = layer.getAnimationWatcher();
            if (watcher != null) {
                applyAnimation(model, layer, watcher, currentTime);
            }
        }
    }

    protected abstract void applyAnimation(ITimeModel model, Layer layer, AnimationWatcher watcher, long currentTime);

    protected abstract boolean isGamePaused();

    /**
     * Called when the animation was removed or ended.
     *
     * @param watcher
     */
    @OverridingMethodsMustInvokeSuper
    protected void onAnimationEnd(@Nullable ITimeModel model, Layer layer, AnimationWatcher watcher) {
        onAnimationStop(watcher);
    }

    public void buildLayers(LinkedHashMap<String, LayerDefinition> layers) {
        layerMap = layers.values().stream()
                .map(layerDefinition -> new Layer(this, layerDefinition))
                .collect(Collectors.toMap(Layer::getName, layer -> layer, (o, o2) -> o, LinkedHashMap::new));
    }

    /**
     * Called on every animation stop like removing animation or replacing it.
     *
     * @param watcher
     */
    protected void onAnimationStop(AnimationWatcher watcher) {

    }

    public void onLoopedAnimationRestart(AnimationWatcher watcher) {

    }
}
