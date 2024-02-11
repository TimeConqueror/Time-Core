package ru.timeconqueror.timecore.animation.watcher;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.object.MolangLibrary;
import lombok.Getter;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.AnimationController;
import ru.timeconqueror.timecore.animation.AnimationData;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.network.AnimationState;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.molang.Molang;
import ru.timeconqueror.timecore.molang.MolangObjects;

@Getter
public class AnimationTickerImpl extends AbstractAnimationTicker {
    private final AnimationData animationData;
    private final AnimationCompanionData companionData;
    private final MolangLibrary tickerQuery;

    public AnimationTickerImpl(AnimationData animationData, long clockTime, AnimationCompanionData companionData) {
        super(new Timeline(animationData.getAnimationLength(), animationData.getSpeed(), animationData.isReversed(), clockTime, animationData.getStartAnimationTime()));
        this.animationData = animationData;
        this.companionData = companionData;
        this.tickerQuery = MolangObjects.queriesForTicker(this);
    }

    public AnimationTickerImpl(AnimationState.ActiveState state, long clockTime) {
        this(state.getData(), clockTime - state.getElapsedTime(), AnimationCompanionData.EMPTY);
    }

    @Override
    public void update(AnimationController animationController, long clockTime) {
        long elapsedTimeTillFirstBoundary = getTimeline().getElapsedTimeTillFirstBoundary(clockTime);
        // if still playing the zero cycle and hasn't achieved the first boundary
        if (elapsedTimeTillFirstBoundary > 0) return;

        AnimationData data = getAnimationData();
        AnimationData nextData = data.getNextAnimation();
        if (nextData != null) {
            animationController.startAnimation(nextData, clockTime + elapsedTimeTillFirstBoundary);
            return;
        }

        if (data.getLoopMode() == LoopMode.DO_NOT_LOOP) {
            animationController.removeAnimation(clockTime + elapsedTimeTillFirstBoundary, data.isNoTransitionToNone() ? 0 : AnimationConstants.BASIC_TRANSITION_TIME);
        }
    }

    @Override
    public void apply(ITimeModel model, BlendType blendType, float outerWeight, MolangEnvironment env, long clockTime) {
        Animation animation = animationData.getAnimation();
        //TODO custom weight

        int animationTime = getTimeline().getAnimationTime(clockTime, isLooped());

        env.loadLibrary(Molang.Query.Domains.ANIMATION, tickerQuery);
        animation.apply(model, blendType, outerWeight, env, animationTime);
        env.unloadLibrary(Molang.Query.Domains.ANIMATION);
    }

    @Override
    public boolean canIgnore(AnimationData data) {
        return getAnimationData().equals(data);
    }

    @Override
    public boolean isTransition() {
        return false;
    }

    @Override
    public int getAnimationTimeAt(long clockTime) {
        return getTimeline().getAnimationTime(clockTime, isLooped());
    }

    public String print(long clockTime) {
        return String.format("Animation: Progress Time: %d/%d, Elapsed: %d/%dms, Data: %s", getAnimationTimeAt(clockTime), getAnimationLength(), getElapsedTimeAt(clockTime), getTimeline().getElapsedTimeTillFirstBoundary(clockTime), getAnimationData());
    }

    @Override
    public AnimationState getState(long clockTime) {
        return new AnimationState.ActiveState(getAnimationData(),
                getTimeline().getElapsedTime(clockTime));
    }
}
