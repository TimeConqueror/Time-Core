package ru.timeconqueror.timecore.api.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.component.AnimationBone;
import ru.timeconqueror.timecore.animation.component.IKeyFrame;
import ru.timeconqueror.timecore.animation.watcher.TimelineSnapshot;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

public abstract class TransitionFactoryWithDestination extends TransitionFactory {
    public TransitionFactoryWithDestination(MolangEnvironment env, Animation source) {
        super(env, source);
    }

    public abstract Iterable<AnimationBone> getDestinationBones();

    /**
     * Returns destination keyframe of provided type for transition animation.
     * <p>
     * Will be called only when {@link #source} is a destination animation.
     *
     * @param part                 part for which destination keyframe should be calculated.
     * @param destinationStartTime snapshot which contains the start animation time to which keyframe should lead
     * @param boneName             location of bone/part for which destination keyframe should be calculated.
     * @param channel              type of keyframe which should be calculated.
     * @param transitionTime       time of transition between source and destination animations.
     */
    @NotNull
    public abstract IKeyFrame getDestKeyFrame(TimeModelPart part, TimelineSnapshot destinationStartTime, String boneName, Channel channel, int transitionTime);

    @Override
    public TransitionFactoryWithDestination withRequiredDestination() {
        return this;
    }
}
