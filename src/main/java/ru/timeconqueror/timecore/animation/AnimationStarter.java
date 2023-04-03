package ru.timeconqueror.timecore.animation;

import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.AnimationManager;

import java.util.Objects;

//TODO changeable weight
//TODO startFrom
public class AnimationStarter {
    private final AnimationData data;

    public AnimationStarter(Animation animation) {
        Objects.requireNonNull(animation);
        this.data = new AnimationData(animation);
    }

    private AnimationStarter(AnimationData animationData) {
        this.data = animationData.copy();
    }

    public static AnimationStarter fromAnimationData(AnimationData data) {
        Objects.requireNonNull(data);
        return new AnimationStarter(data);
    }

    /**
     * If set to false: when you start this animation on the layer, which is playing the same animation, it won't be re-started.
     * Useful for walking animations, so you don't need to worry how to control animation endings.
     * Default: true.
     */
    public AnimationStarter setIgnorable(boolean ignorable) {
        this.data.ignorable = ignorable;
        return this;
    }

    public AnimationStarter doNotTransitToNull(boolean doNotTransitToNull) {
        this.data.doNotTransitToNull = doNotTransitToNull;
        return this;
    }

    /**
     * Defines the time (in milliseconds) of the transition animation between the previous animation and the one we want to start.
     * Default: {@link AnimationConstants#BASIC_TRANSITION_TIME}.
     */
    public AnimationStarter setTransitionTime(int transitionTime) {
        data.transitionTime = Math.max(transitionTime, 0);
        return this;
    }

    /**
     * Sets the factor that will speed up or slow down the animation.
     * Default: 1F.
     */
    public AnimationStarter setSpeed(float speedFactor) {
        data.speed = Math.max(speedFactor, 0);
        return this;
    }

    /**
     * Setting this, you can make a chain of played animations.
     * As soon as one ends, the next one will start immediately.
     * This setting will avoid unpleasant flickering when moving from one animation to another.
     * Default: null.
     */
    public AnimationStarter setNextAnimation(AnimationStarter nextAnimationStarter) {
        data.nextAnimationData = nextAnimationStarter.getData();
        return this;
    }

    public AnimationStarter reversed() {
        data.reversed = true;
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

    public static class AnimationData {
        private final Animation animation;
        @Nullable
        private AnimationData nextAnimationData;
        private boolean ignorable = true;
        private int transitionTime = AnimationConstants.BASIC_TRANSITION_TIME;
        private float speed = 1F;
        private boolean doNotTransitToNull;
        private boolean reversed;

        private AnimationData(Animation animation) {
            this.animation = animation;
        }

        public static void encode(AnimationData animationData, PacketBuffer buffer) {
            buffer.writeResourceLocation(animationData.getAnimation().getId());
            buffer.writeFloat(animationData.getSpeed());
            buffer.writeInt(animationData.getTransitionTime());
            buffer.writeBoolean(animationData.isIgnorable());
            buffer.writeBoolean(animationData.doNotTransitToNull);
            buffer.writeBoolean(animationData.reversed);

            boolean hasNextAnim = animationData.nextAnimationData != null;
            buffer.writeBoolean(hasNextAnim);
            if (hasNextAnim) {
                encode(animationData.nextAnimationData, buffer);
            }
        }

        public static AnimationData decode(PacketBuffer buffer) {
            Animation animation = AnimationRegistry.getAnimation(buffer.readResourceLocation());

            AnimationData animationData = new AnimationData(animation);

            animationData.speed = buffer.readFloat();
            animationData.transitionTime = buffer.readInt();
            animationData.ignorable = buffer.readBoolean();
            animationData.doNotTransitToNull = buffer.readBoolean();
            animationData.reversed = buffer.readBoolean();

            boolean hasNextAnim = buffer.readBoolean();
            if (hasNextAnim) {
                animationData.nextAnimationData = decode(buffer);
            }

            return animationData;
        }

        public Animation getAnimation() {
            return animation;
        }

        public float getSpeed() {
            return speed;
        }

        public int getAnimationLength() {
            return animation.getLength();
        }

        public boolean isIgnorable() {
            return ignorable;
        }

        public boolean doesNotTransitToNull() {
            return doNotTransitToNull;
        }

        public boolean isReversed() {
            return reversed;
        }

        public int getTransitionTime() {
            return transitionTime;
        }

        @Nullable
        public AnimationData copy() {
            AnimationData animationData = new AnimationData(animation);
            animationData.speed = this.speed;
            animationData.ignorable = this.ignorable;
            animationData.transitionTime = this.transitionTime;
            animationData.nextAnimationData = this.nextAnimationData != null ? this.nextAnimationData.copy() : null;
            animationData.doNotTransitToNull = doNotTransitToNull;
            animationData.reversed = reversed;

            return animationData;
        }

        public @Nullable AnimationData getNextAnimation() {
            return nextAnimationData;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnimationData)) return false;

            AnimationData that = (AnimationData) o;

            if (ignorable != that.ignorable) return false;
            if (transitionTime != that.transitionTime) return false;
            if (Float.compare(that.speed, speed) != 0) return false;
            if (doNotTransitToNull != that.doNotTransitToNull) return false;
            if (reversed != that.reversed) return false;
            if (!animation.equals(that.animation)) return false;
            return Objects.equals(nextAnimationData, that.nextAnimationData);
        }

        @Override
        public int hashCode() {
            int result = animation.hashCode();
            result = 31 * result + (nextAnimationData != null ? nextAnimationData.hashCode() : 0);
            result = 31 * result + (ignorable ? 1 : 0);
            result = 31 * result + transitionTime;
            result = 31 * result + (speed != +0.0f ? Float.floatToIntBits(speed) : 0);
            result = 31 * result + (doNotTransitToNull ? 1 : 0);
            result = 31 * result + (reversed ? 1 : 0);
            return result;
        }

        @Override
        public String toString() {
            return "AnimationData{" +
                    "animation=" + animation +
                    ", nextAnimationData=" + nextAnimationData +
                    ", ignorable=" + ignorable +
                    ", transitionTime=" + transitionTime +
                    ", speed=" + speed +
                    ", doNotTransitToNull=" + doNotTransitToNull +
                    ", reversed=" + reversed +
                    '}';
        }
    }
}
