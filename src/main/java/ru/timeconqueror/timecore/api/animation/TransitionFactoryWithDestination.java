package ru.timeconqueror.timecore.api.animation;

import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.component.BoneOption;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

public abstract class TransitionFactoryWithDestination extends TransitionFactory {
    public TransitionFactoryWithDestination(Animation source) {
        super(source);
    }

    public abstract Iterable<BoneOption> getDestAnimationBones();

    /**
     * Returns destination keyframe of provided type for transition animation.
     * <p>
     * Will be called only when {@link #source} is a destination animation.
     *
     * @param part           part for which destination keyframe should be calculated.
     * @param boneName       location of bone/part for which destination keyframe should be calculated.
     * @param channel        type of keyframe which should be calculated.
     * @param transitionTime time of transition between source and destination animations.
     */
    @NotNull
    public abstract KeyFrame getDestKeyFrame(TimeModelPart part, String boneName, Channel channel, int transitionTime);

    @Override
    public TransitionFactoryWithDestination withRequiredDestination() {
        return this;
    }
}
