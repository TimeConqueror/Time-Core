package ru.timeconqueror.timecore.client.render.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationManager;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class AnimationManager implements IAnimationManager {
    private HashMap<String, Layer> layerMap;
    private List<Layer> layers;

    AnimationManager() {
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
    public <T extends LivingEntity> void applyAnimations(TimeEntityModel<T> model) {
        for (Layer layer : layers) {
            AnimationWatcher watcher = layer.getAnimationWatcher();

            boolean paused = Minecraft.getInstance().isGamePaused();

            long time = System.currentTimeMillis();

            if (watcher != null) {
                if (paused) {
                    watcher.freeze();
                } else {
                    watcher.unfreeze();

                    if (watcher.requiresTransitionPreparation()) {
                        watcher.initTransition(model);
                    }

                    if (watcher.isAnimationEnded(time)) {
                        if (watcher.getAnimation() instanceof Transition) {
                            IAnimation anim = ((Transition) watcher.getAnimation()).getDestAnimation();
                            //noinspection ConstantConditions (transition should always have its data)
                            watcher = anim != null ? new AnimationWatcher(anim, watcher.getTransitionData().getSpeedFactor()) : null;
                        } else if (watcher.getAnimation().isLooped()) {
                            watcher.resetTimer();
                        } else {
                            watcher = null;
                        }
                    }
                }
            }

            layer.setAnimationWatcher(watcher); //here we update current watcher

            if (watcher != null) {
                IAnimation animation = watcher.getAnimation();
                animation.apply(model, layer, watcher.getExistingTime(time));
            }
        }
    }

    void setLayers(HashMap<String, Layer> layers) {
        layerMap = layers;

        this.layers = layers.values().stream()
                .sorted(Comparator.comparingInt(Layer::getPriority))
                .collect(Collectors.toList());
    }
}