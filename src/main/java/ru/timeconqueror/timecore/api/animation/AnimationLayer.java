package ru.timeconqueror.timecore.api.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;

public interface AnimationLayer extends Cloneable {
    float getWeight();

    void setWeight(float weight);

    BlendType getBlendType();

    void setBlendType(BlendType type);

    /**
     * Returns layer name.
     */
    String getName();

    /**
     * Returns the information about animation currently played on layer.
     */
    IAnimationWatcherInfo getWatcherInfo();

//    /**
//     * Pauses the layer as well as the played animation.
//     * Being frozen layer will also ignore animation adding and removal
//     */
//    void freeze();
//
//    /**
//     * Unpauses the layer and the animation started on it.
//     */
//    void unfreeze();
//
//    /**
//     * Returns true if layer & animation on it is paused, otherwise returns false.
//     */
//    boolean isFrozen();

    @Nullable
    @Deprecated
        //TODO 1.18+ removal
        //use #getAnimationInfo
    AnimationWatcher getAnimationWatcher();

    @Deprecated
        // TODO 1.18+ removal, use #getAnimationInfo()
    Animation getCurrentAnimation();

    @Deprecated
//TODO 1.18+ removal, use #getAnimationInfo()
    boolean hasAnimation();
}
