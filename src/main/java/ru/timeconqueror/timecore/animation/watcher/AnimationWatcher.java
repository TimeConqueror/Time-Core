package ru.timeconqueror.timecore.animation.watcher;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationRegistry;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.Layer;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.util.WatcherSerializer;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.MathUtils;
import ru.timeconqueror.timecore.api.util.Requirements;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

public class AnimationWatcher implements IAnimationWatcherInfo {
    @Getter
    private final Timeline timeline;
    private final boolean noTransitionToNone;
    @Getter
    private final LoopMode loopMode;
    @Nullable
    @Getter
    private final AnimationStarter.AnimationData nextAnimation;
    @Getter
    protected Animation animation;
    private boolean inited = false;

    public AnimationWatcher(AnimationStarter.AnimationData data) {
        this(data.getAnimation(),
                data.getSpeed(),
                data.getStartAnimationTime(),
                data.isNoTransitionToNone(),
                data.isReversed(),
                data.getLoopMode(),
                data.getNextAnimation());
    }

    public AnimationWatcher(Animation animation, float speed, int animationTimeStartFrom, boolean noTransitionToNone, boolean reversed, LoopMode loopMode, @Nullable AnimationStarter.AnimationData nextAnimation) {
        var length = animation.getLength();
        Requirements.greaterOrEquals(length, 0);
        Requirements.greaterOrEquals(speed, 0);

        this.timeline = new Timeline(length, speed, reversed, System.currentTimeMillis(), animationTimeStartFrom);
        this.animation = animation;
        this.nextAnimation = nextAnimation;
        this.noTransitionToNone = noTransitionToNone;
        this.loopMode = loopMode;
    }

    protected AnimationWatcher(Animation animation, int length, float speed, int animationTimeStartFrom, boolean noTransitionToNone, boolean reversed, LoopMode loopMode, @Nullable AnimationStarter.AnimationData nextAnimation) {
        Requirements.greaterOrEquals(length, 0);
        Requirements.greaterOrEquals(speed, 0);

        this.timeline = new Timeline(length, speed, reversed, System.currentTimeMillis(), animationTimeStartFrom);
        this.animation = animation;
        this.nextAnimation = nextAnimation;
        this.noTransitionToNone = noTransitionToNone;
        this.loopMode = loopMode;
    }

    public boolean requiresInit() {
        return !inited;
    }

    @OverridingMethodsMustInvokeSuper
    public void init(Layer layer, ITimeModel model) {
        inited = true;
    }

    @Override
    public boolean playsSame(AnimationStarter.AnimationData data) {
        return data.getAnimation() == animation
                && data.isReversed() == isReversed()
                && data.isNoTransitionToNone() == noTransitionToNone
                && data.getStartAnimationTime() == timeline.getAnimationStartTime()
                && data.getLoopMode() == loopMode
                && MathUtils.equals(data.getSpeed(), getSpeed())
                && Objects.equals(data.getNextAnimationData(), getNextAnimation());
    }

    @Nullable
    public AnimationWatcher next() {//TODO make check for NULL instead
        if (nextAnimation != null) {
            if (nextAnimation.getTransitionTime() <= 0) {
                return new AnimationWatcher(nextAnimation);
            } else {
                return TransitionWatcher.from(this, nextAnimation);
            }
        } else if (noTransitionToNone) {
            return null;
        } else {
            return TransitionWatcher.toNullDestination(this, AnimationConstants.BASIC_TRANSITION_TIME);
        }
    }

    public void resetTimer() {
        timeline.reset();
    }

    public boolean isAnimationEnded(long time) {
        return timeline.isEnded(time);
    }

    @Override
    public int getElapsedTime(long time) {
        return timeline.getElapsedTime(time);
    }

    @Override
    public int getAnimationTime(long time) {
        return timeline.getAnimationTime(time);
    }

    public void freeze(FreezableTime.FreezeCause cause) {
        timeline.freeze(cause);
    }

    public void unfreeze(FreezableTime.FreezeCause cause) {
        timeline.unfreeze(cause);
    }

    @Override
    public int getElapsedLength() {
        return timeline.getElapsedLength();
    }

    @Override
    public int getAnimationLength() {
        return timeline.getLength();
    }

    @Override
    public String toString() {
        long time = System.currentTimeMillis();
        return "Animation{" +
                "Id: " + animation.getId() +
                ", Progress Time: " + getAnimationTime(time) + "/" + animation.getLength() + " (" + getElapsedTime(time) + "/" + getElapsedLength() + "ms)" +
                ", Speed: " + getSpeed() +
                ", Reversed: " + isReversed() +
                ", Initialized: " + inited +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnimationWatcher watcher)) return false;
        return Float.compare(watcher.timeline.getSpeed(), timeline.getSpeed()) == 0 &&
                animation.equals(watcher.animation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(timeline.getSpeed(), animation);
    }

    @Override
    public boolean isAutoTransition() {
        return false;
    }

    @Override
    public boolean autoTransitsFrom(Animation animation) {
        return false;
    }

    @Override
    public boolean autoTransitsTo(Animation animation) {
        return false;
    }

    @Override
    public boolean isNull() {
        return getAnimation() == Animation.NULL;
    }

    @Override
    public boolean isReversed() {
        return timeline.isReversed();
    }

    @Override
    public float getSpeed() {
        return timeline.getSpeed();
    }

    public static class Serializer implements WatcherSerializer<AnimationWatcher> {
        public void serialize(AnimationWatcher watcher, FriendlyByteBuf buffer) {
            buffer.writeResourceLocation(watcher.getAnimation().getId());
            buffer.writeInt(watcher.getElapsedTime());
            buffer.writeFloat(watcher.timeline.getSpeed());
            buffer.writeVarInt(watcher.timeline.getAnimationStartTime());
            buffer.writeBoolean(watcher.timeline.isReversed());
            buffer.writeBoolean(watcher.noTransitionToNone);
            buffer.writeVarInt(LoopMode.ORDINAL_LOOKUP.from(watcher.loopMode));

            boolean hasNextAnimation = watcher.nextAnimation != null;
            buffer.writeBoolean(watcher.nextAnimation != null);
            if (hasNextAnimation) {
                AnimationStarter.AnimationData.encode(watcher.nextAnimation, buffer);
            }
        }

        public AnimationWatcher deserialize(FriendlyByteBuf buffer) {
            Animation animation = AnimationRegistry.getAnimation(buffer.readResourceLocation());
            if (animation == null) throw new IllegalArgumentException();

            int elapsedTime = buffer.readInt();
            float speed = buffer.readFloat();
            var animationTimeStartFrom = buffer.readVarInt();
            boolean reversed = buffer.readBoolean();
            boolean transitNo = buffer.readBoolean();
            LoopMode loopMode = LoopMode.ORDINAL_LOOKUP.by(buffer.readVarInt());

            AnimationStarter.AnimationData nextAnimationData = null;
            boolean hasNextAnimation = buffer.readBoolean();
            if (hasNextAnimation) {
                nextAnimationData = AnimationStarter.AnimationData.decode(buffer);
            }

            AnimationWatcher watcher = new AnimationWatcher(animation, speed, animationTimeStartFrom, transitNo, reversed, loopMode, nextAnimationData);
            watcher.timeline.set(System.currentTimeMillis() - elapsedTime);

            return watcher;
        }
    }
}
