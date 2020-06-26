package ru.timeconqueror.timecore.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.api.util.Requirements;
import ru.timeconqueror.timecore.client.render.animation.Transition;

public class TransitionWatcher extends AnimationWatcher {
    private final int transitionTime;
    private final float destAnimSpeedFactor;
    @Nullable
    private final IAnimation destination;

    private final IAnimation source;
    private final int sourceExistingTime;

    //from null source
    public TransitionWatcher(int transitionTime, @Nullable IAnimation destination, float destAnimSpeedFactor) {
        this(null, 0, transitionTime, destination, destAnimSpeedFactor);
    }

    public TransitionWatcher(@Nullable IAnimation source, int sourceExistingTime, int transitionTime, @Nullable IAnimation destination, float destAnimSpeedFactor) {
        super(null, 1.0F);

        Requirements.greaterOrEqualsThan(transitionTime, 0);
        if (destination != null) Requirements.greaterThan(destAnimSpeedFactor, 0);

        this.transitionTime = transitionTime;
        this.destAnimSpeedFactor = destAnimSpeedFactor;
        this.destination = destination;
        this.source = source;
        this.sourceExistingTime = sourceExistingTime;
    }

    @Override
    public void init(TimeEntityModel<?> model) {
        animation = Transition.create(source, sourceExistingTime, destination, model.getBaseModel(), transitionTime);
    }

    @Override
    @Nullable
    public AnimationWatcher next() {
        return destination != null ? new AnimationWatcher(destination, destAnimSpeedFactor) : null;
    }

    public @Nullable IAnimation getDestination() {
        return destination;
    }
}
