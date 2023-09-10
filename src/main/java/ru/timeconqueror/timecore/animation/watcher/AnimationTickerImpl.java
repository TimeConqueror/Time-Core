package ru.timeconqueror.timecore.animation.watcher;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.object.MolangLibrary;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.AnimationData;
import ru.timeconqueror.timecore.animation.LayerImpl;
import ru.timeconqueror.timecore.animation.action.AnimationEventListener;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.util.AnimationTickerSerializer;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.molang.Molang;
import ru.timeconqueror.timecore.molang.MolangObjects;

import java.util.List;

public class AnimationTickerImpl extends AbstractAnimationTicker {
    @Getter
    private final AnimationData animationData;
    @Getter
    private final AnimationCompanionData companionData;
    @Getter
    private final MolangLibrary tickerQuery;

    public AnimationTickerImpl(AnimationData animationData, AnimationCompanionData companionData) {
        super(new Timeline(animationData.getAnimationLength(), animationData.getSpeed(), animationData.isReversed(), System.currentTimeMillis(), animationData.getStartAnimationTime()));
        this.animationData = animationData;
        this.companionData = companionData;
        this.tickerQuery = MolangObjects.queriesForTicker(this);
    }

    @Override
    public void handleEndOnLayer(LayerImpl layer, List<AnimationEventListener> eventListeners) {
        AnimationData data = getAnimationData();
        AnimationData nextData = data.getNextAnimation();
        if (nextData != null) {
            layer.start(nextData);
            return;
        }

        if (data.getLoopMode() == LoopMode.HOLD_ON_LAST_FRAME) {
            return;
        } else if (data.getLoopMode() == LoopMode.LOOP) {
            getTimeline().reset();
            eventListeners.forEach(listener -> listener.onAnimationRestarted(this));
            return;
        }

        layer.removeAnimation(data.isNoTransitionToNone() ? 0 : AnimationConstants.BASIC_TRANSITION_TIME);
    }

    @Override
    public void apply(ITimeModel model, BlendType blendType, float outerWeight, MolangEnvironment env, long systemTime) {
        Animation animation = animationData.getAnimation();
        //TODO custom weight

        int animationTime = getTimeline().getAnimationTime(systemTime);

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
    public String toString() {
        long systemTime = System.currentTimeMillis();
        return String.format("Animation: Progress Time: %d/%d, Elapsed: %d/%dms, Data: %s", getAnimationTime(systemTime), getAnimationLength(), getElapsedTime(systemTime), getElapsedLength(), getAnimationData());
    }

    public static class Serializer implements AnimationTickerSerializer<AnimationTickerImpl> {
        @Override
        public void serialize(AnimationTickerImpl ticker, FriendlyByteBuf buffer) {
            AnimationData.encode(ticker.getAnimationData(), buffer);
            buffer.writeVarInt(ticker.getTimeline().getElapsedTime(System.currentTimeMillis()));
        }

        @Override
        public AnimationTickerImpl deserialize(FriendlyByteBuf buffer) {
            AnimationData data = AnimationData.decode(buffer);
            int elapsedTime = buffer.readVarInt();
            AnimationTickerImpl ticker = new AnimationTickerImpl(data, AnimationCompanionData.EMPTY);
            ticker.getTimeline().setFromElapsed(elapsedTime);
            return ticker;
        }
    }
}
