package ru.timeconqueror.timecore.animation.watcher;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ru.timeconqueror.timecore.animation.AnimationData;
import ru.timeconqueror.timecore.animation.LayerImpl;
import ru.timeconqueror.timecore.animation.action.AnimationEventListener;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationTickerInfo;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

import java.util.List;

@RequiredArgsConstructor
public abstract class AbstractAnimationTicker implements AnimationTickerInfo {

    @Getter(value = AccessLevel.PROTECTED)
    private final Timeline timeline;

    @Override
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

    public abstract void handleEndOnLayer(LayerImpl layer, List<AnimationEventListener> eventListeners);

    public abstract boolean canIgnore(AnimationData data);

    @Override
    public boolean isEmpty() {
        return this.getAnimationData().getAnimation() == Animation.NULL;
    }

    @Override
    public boolean isReversed() {
        return timeline.isReversed();
    }

    @Override
    public int getAnimationTime(long systemTime) {
        return timeline.getAnimationTime(systemTime);
    }

    @Override
    public int getAnimationLength() {
        return timeline.getLength();
    }

    @Override
    public int getElapsedTime(long systemTime) {
        return timeline.getElapsedTime(systemTime);
    }

    @Override
    public int getElapsedLength() {
        return timeline.getElapsedLength();
    }
}
