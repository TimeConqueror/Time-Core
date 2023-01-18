package ru.timeconqueror.timecore.api.animation;

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
}
