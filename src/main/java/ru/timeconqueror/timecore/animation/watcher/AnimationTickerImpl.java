package ru.timeconqueror.timecore.animation.watcher;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.LayerImpl;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.util.AnimationTickerSerializer;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

public class AnimationTickerImpl extends AnimationTicker {

    @Getter
    private final AnimationStarter.AnimationData animationData;

    public AnimationTickerImpl(AnimationStarter.AnimationData animationData) {
        super(new Timeline(animationData.getAnimationLength(), animationData.getSpeed(), animationData.isReversed(), System.currentTimeMillis(), animationData.getStartAnimationTime()));
        this.animationData = animationData;
    }

    @Override
    public void handleEndOnLayer(LayerImpl layer) {
        AnimationStarter.AnimationData data = getAnimationData();
        AnimationStarter.AnimationData nextData = data.getNextAnimation();
        if (nextData != null) {
            layer.start(nextData);
            return;
        }

        if (data.getLoopMode() == LoopMode.HOLD_ON_LAST_FRAME) {
            return;
        } else if (data.getLoopMode() == LoopMode.LOOP) {
            getTimeline().reset();
            return;
        }

        layer.removeAnimation(data.isNoTransitionToNone() ? 0 : AnimationConstants.BASIC_TRANSITION_TIME);
    }

    @Override
    public void apply(ITimeModel model, BlendType blendType, float outerWeight, MolangEnvironment env, long systemTime) {
        Animation animation = animationData.getAnimation();
        //TODO custom weight

        int animationTime = getTimeline().getAnimationTime(systemTime);
        animation.apply(model, blendType, outerWeight, env, animationTime);
    }

    @Override
    public boolean canIgnore(AnimationStarter.AnimationData data) {
        return getAnimationData().equals(data);
    }

    @Override
    public boolean isTransition() {
        return false;
    }

    public static class Serializer implements AnimationTickerSerializer<AnimationTickerImpl> {
        @Override
        public void serialize(AnimationTickerImpl ticker, FriendlyByteBuf buffer) {
            AnimationStarter.AnimationData.encode(ticker.getAnimationData(), buffer);
            buffer.writeVarInt(ticker.getTimeline().getElapsedTime(System.currentTimeMillis()));
        }

        @Override
        public AnimationTickerImpl deserialize(FriendlyByteBuf buffer) {
            AnimationStarter.AnimationData data = AnimationStarter.AnimationData.decode(buffer);
            int elapsedTime = buffer.readVarInt();
            AnimationTickerImpl ticker = new AnimationTickerImpl(data);
            ticker.getTimeline().setFromElapsed(elapsedTime);
            return ticker;
        }
    }
}
