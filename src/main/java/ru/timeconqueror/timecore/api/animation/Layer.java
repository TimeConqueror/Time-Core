package ru.timeconqueror.timecore.api.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import ru.timeconqueror.timecore.animation.watcher.AnimationTicker;

//TODO setWeight/setBlendType be synced
public interface Layer {
    float getWeight();

    BlendType getBlendType();

    MolangEnvironment getEnvironment();

    /**
     * Returns layer name.
     */
    String getName();

    AnimationTicker getCurrentTicker();

    void setCurrentTicker(AnimationTicker ticker);

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
