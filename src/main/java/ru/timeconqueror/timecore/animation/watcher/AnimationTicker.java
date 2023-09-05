package ru.timeconqueror.timecore.animation.watcher;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.LayerImpl;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.TickerInfo;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

@RequiredArgsConstructor
public abstract class AnimationTicker implements TickerInfo {

    @Getter(value = AccessLevel.PROTECTED)
    private final Timeline timeline;

    private boolean inited;

    public boolean isAnimationEnded(long systemTime) {
        return timeline.isEnded(systemTime);
    }

    public void freeze(FreezableTime.FreezeCause cause) {
        getTimeline().freeze(cause);
    }

    public void unfreeze(FreezableTime.FreezeCause cause) {
        getTimeline().unfreeze(cause);
    }

    public abstract void apply(ITimeModel model, BlendType blendType, float outerWeight, MolangEnvironment environment, long systemTime);

    public abstract void handleEndOnLayer(LayerImpl layer);

    public final void initIfNeedsTo() {
        if (!inited) {
            inited = true;
            init();
        }
    }

    protected void init() {
        //TODo is it really needed? maybe for init molangenv?
    }

    public abstract boolean canIgnore(AnimationStarter.AnimationData data);

    @Override
    public boolean isEmpty() {
        return this.getAnimationData().getAnimation() == Animation.NULL;
    }
}
