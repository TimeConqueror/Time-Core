package ru.timeconqueror.timecore.api.animation;

import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.client.render.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

public interface AnimationManager {

    boolean containsLayer(String name);

    /**
     * //TODO
     *
     * @param name
     * @return
     * @throws RuntimeException if layer is not found
     */
    @NotNull
    AnimationLayer getLayer(String name);

    /**
     * If animation has layer with name 'main', then it will return specifically that layer.
     * (It will has that layer, if you don't add layers to your manager)
     * Otherwise it will return the first layer in the manager.
     *
     * @return animation layer with name 'main' or if it doesn't find so, it will return the first layer.
     */
    @NotNull
    AnimationLayer getMainLayer();

    void applyAnimations(TimeEntityModel<?> model);

    void setAnimation(AnimationStarter.AnimationData animationData, String layerName);

    void removeAnimation(String layerName);

    void removeAnimation(String layerName, int transitionTime);
}
