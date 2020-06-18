package ru.timeconqueror.timecore.api.client.render.animation;

import org.jetbrains.annotations.NotNull;
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
    IAnimationLayer getLayer(String name);

    /**
     * If animation has layer with name 'main', then it will return specifically that layer.
     * (It will has that layer, if you don't add layers to your manager)
     * Otherwise it will return the first layer in the manager.
     *
     * @return animation layer with name 'main' or if it doesn't find so, it will return the first layer.
     */
    @NotNull
    IAnimationLayer getMainLayer();

    void applyAnimations(TimeEntityModel<?> model);
}
