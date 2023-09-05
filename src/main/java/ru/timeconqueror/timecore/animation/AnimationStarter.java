package ru.timeconqueror.timecore.animation;

import lombok.*;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.util.MathUtils;

import java.util.Objects;

//TODO changeable weight
//TODO mod dependent config for default animation data
//TODO check if loop mode work with reversed
public class AnimationStarter {
    private final AnimationData data;

    public AnimationStarter(Animation animation) {
        Objects.requireNonNull(animation);
        this.data = new AnimationData(animation);
    }

    private AnimationStarter(AnimationData animationData) {
        this.data = animationData.copy();
    }

    public static AnimationStarter from(AnimationData data) {
        Objects.requireNonNull(data);
        return new AnimationStarter(data);
    }

    /**
     * If set to true: does not apply the animation if there's the same animation on the layer.
     * Useful for walking animations, so you don't need to worry how to control animation endings.
     * Default: true.
     */
    public AnimationStarter ignorable(boolean ignorable) {
        this.data.ignorable = ignorable;
        return this;
    }

    /**
     * When you start this animation on the layer, which is playing the same animation, it won't be re-started.
     * This method forces the bound animation to be applied despite the animation which was played before.
     * Default: true.
     */
    public AnimationStarter nonIgnorable() {
        this.data.ignorable = false;
        return this;
    }

    /**
     * In case the animation is ended and no any other animation come in its place, it will be transitioned to empty animation
     * within some time. This method allows to disable this automatic transition.
     * Default: false.
     */
    public AnimationStarter withNoTransitionToNone() {
        this.data.noTransitionToNone = true;
        return this;
    }

    /**
     * Defines the time (in milliseconds) of the transition animation between the previous animation and the one we want to start.
     * Default: {@link AnimationConstants#BASIC_TRANSITION_TIME}.
     */
    public AnimationStarter withTransitionTime(int transitionTime) {
        data.transitionTime = Math.max(transitionTime, 0);
        return this;
    }

    /**
     * Sets the factor that will speed up or slow down the animation.
     * Default: 1F.
     */
    public AnimationStarter withSpeed(float speedFactor) {
        data.speed = Math.max(speedFactor, 0);
        return this;
    }

    /**
     * Allows to run animation not from start, but from specific animation time (in milliseconds).
     * Default: 0.
     */
    public AnimationStarter startingFrom(int animationTime) {
        data.startAnimationTime = MathUtils.coerceInRange(animationTime, 0, data.animation.getLength());
        return this;
    }

    /**
     * Allows to run animation not from start, but from specific animation time.
     * As a parameter you need to present the percent [0.0; 1.0] of the animation to start from.
     * Default: 0.
     */
    public AnimationStarter startingFrom(float animationTimePercentage) {
        animationTimePercentage = MathUtils.coerceInRange(animationTimePercentage, 0, 1);
        data.startAnimationTime = MathUtils.coerceInRange(Math.round(animationTimePercentage * data.animation.getLength()), 0, data.animation.getLength());
        return this;
    }

    /**
     * Setting this, you can make a chain of played animations.
     * As soon as one ends, the next one will start immediately.
     * This setting will avoid unpleasant flickering when moving from one animation to another.
     * <br>
     * <b color=yellow>Makes the current animation, which will be played before nextAnimationStarter, have {@link LoopMode#DO_NOT_LOOP} loop mode.</b>
     * Default: null.
     */
    public AnimationStarter withNextAnimation(AnimationStarter nextAnimationStarter) {
        data.nextAnimationData = nextAnimationStarter.getData();
        return this;
    }

    /**
     * Make the animation go backwards
     * Default: false
     */
    public AnimationStarter reversed() {
        data.reversed = true;
        return this;
    }

    /**
     * Make the animation go backwards
     * Default: false
     */
    public AnimationStarter reversed(boolean reversed) {
        data.reversed = reversed;
        return this;
    }

    /**
     * Overrides the loop mode which is provided by animation file.
     * <br>
     * <b color=yellow>Doesn't take any effect, if {@link AnimationStarter#withNextAnimation(AnimationStarter)} is called in a chain with not null parameter.</b>
     * Default: null
     */
    public AnimationStarter withLoopMode(@Nullable LoopMode loopMode) {
        data.loopMode = loopMode;
        return this;
    }

    public void startAt(AnimationManager manager, String layerName) {
        manager.setAnimation(this, layerName);
    }

    public AnimationData getData() {
        return data;
    }

    @Override
    public String toString() {
        return data.toString();
    }

    @EqualsAndHashCode
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    @ToString(doNotUseGetters = true)
    public static class AnimationData {
        @Getter
        private final Animation animation;
        @Getter
        private boolean ignorable = true;
        private Integer startAnimationTime = null;
        @Getter
        private int transitionTime = AnimationConstants.BASIC_TRANSITION_TIME;
        @Getter
        private float speed = 1F;
        @Getter
        private boolean noTransitionToNone;//FIXME do in version 2
        @Getter
        private boolean reversed;
        @Nullable
        private LoopMode loopMode = null;
        @Nullable
        @Getter
        private AnimationData nextAnimationData;

        public static void encode(AnimationData animationData, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(animationData.getAnimation().getId());
            buffer.writeFloat(animationData.getSpeed());
            buffer.writeInt(animationData.getTransitionTime());

            buffer.writeBoolean(animationData.startAnimationTime != null);
            if (animationData.startAnimationTime != null) {
                buffer.writeVarInt(animationData.startAnimationTime);
            }
            buffer.writeBoolean(animationData.isIgnorable());
            buffer.writeBoolean(animationData.noTransitionToNone);
            buffer.writeBoolean(animationData.reversed);

            buffer.writeBoolean(animationData.loopMode != null);
            if (animationData.loopMode != null) {
                buffer.writeVarInt(LoopMode.ORDINAL_LOOKUP.from(animationData.loopMode));
            }

            boolean hasNextAnim = animationData.nextAnimationData != null;
            buffer.writeBoolean(hasNextAnim);
            if (hasNextAnim) {
                encode(animationData.nextAnimationData, buffer);
            }
        }

        public static AnimationData decode(FriendlyByteBuf buffer) {
            Animation animation = AnimationRegistry.getAnimation(buffer.readResourceLocation());

            AnimationData animationData = new AnimationData(animation);

            animationData.speed = buffer.readFloat();
            animationData.transitionTime = buffer.readInt();
            if (buffer.readBoolean()) {
                animationData.startAnimationTime = buffer.readVarInt();
            }
            animationData.ignorable = buffer.readBoolean();
            animationData.noTransitionToNone = buffer.readBoolean();
            animationData.reversed = buffer.readBoolean();

            boolean hasLoopMode = buffer.readBoolean();
            if (hasLoopMode) {
                animationData.loopMode = LoopMode.ORDINAL_LOOKUP.by(buffer.readVarInt());
            }

            boolean hasNextAnim = buffer.readBoolean();
            if (hasNextAnim) {
                animationData.nextAnimationData = decode(buffer);
            }

            return animationData;
        }


        public int getAnimationLength() {
            return animation.getLength();
        }

        public int getStartAnimationTime() {
            return startAnimationTime != null ? startAnimationTime : isReversed() ? this.getAnimationLength() : 0;
        }

        public LoopMode getLoopMode() {
            return loopMode != null ? loopMode : animation.getLoopMode();
        }

        @Nullable
        public AnimationData copy() {
            AnimationData animationData = new AnimationData(animation);
            animationData.speed = this.speed;
            animationData.ignorable = this.ignorable;
            animationData.startAnimationTime = startAnimationTime;
            animationData.transitionTime = this.transitionTime;
            animationData.nextAnimationData = this.nextAnimationData != null ? this.nextAnimationData.copy() : null;
            animationData.noTransitionToNone = noTransitionToNone;
            animationData.reversed = reversed;
            animationData.loopMode = loopMode;

            return animationData;
        }

        public @Nullable AnimationData getNextAnimation() {
            return nextAnimationData;
        }
    }
}
