package ru.timeconqueror.timecore.animation.watcher;

import net.minecraft.network.PacketBuffer;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationRegistry;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.util.WatcherSerializer;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationConstants;
import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.Requirements;

import javax.annotation.OverridingMethodsMustInvokeSuper;
import java.util.Objects;

public class AnimationWatcher implements IAnimationWatcherInfo {
    private final Timeline timeline;
    private final boolean doNotTransitToNull;
    private final LoopMode loopMode;
    @Nullable
    private final AnimationStarter.AnimationData nextAnimation;
    protected Animation animation;
    private boolean inited = false;

    public AnimationWatcher(AnimationStarter.AnimationData data) {
        this(data.getAnimation(),
                data.getSpeed(),
                data.doesNotTransitToNull(),
                data.isReversed(),
                data.getLoopMode() != null ? data.getLoopMode() : data.getAnimation().getLoopMode(),
                data.getNextAnimation());
    }

    public AnimationWatcher(Animation animation, float speed, boolean doNotTransitToNull, boolean reversed, LoopMode loopMode, @Nullable AnimationStarter.AnimationData nextAnimation) {
        this(animation, animation.getLength(), speed, doNotTransitToNull, reversed, loopMode, nextAnimation);
    }

    protected AnimationWatcher(Animation animation, int length, float speed, boolean doNotTransitToNull, boolean reversed, LoopMode loopMode, @Nullable AnimationStarter.AnimationData nextAnimation) {
        Requirements.greaterOrEquals(length, 0);
        Requirements.greaterOrEquals(speed, 0);

        this.timeline = new Timeline(length, speed, reversed, System.currentTimeMillis());
        this.animation = animation;
        this.nextAnimation = nextAnimation;
        this.doNotTransitToNull = doNotTransitToNull;
        this.loopMode = loopMode;
    }

    public boolean requiresInit() {
        return !inited;
    }

    @OverridingMethodsMustInvokeSuper
    public void init(ITimeModel model) {
        inited = true;
    }

    @Nullable
    public AnimationWatcher next() {//TODO make check for NULL instead
        if (nextAnimation != null) {
            if (nextAnimation.getTransitionTime() <= 0) {
                return new AnimationWatcher(nextAnimation);
            } else {
                return TransitionWatcher.from(this, nextAnimation);
            }
        } else if (doNotTransitToNull) {
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
    public LoopMode getLoopMode() {
        return loopMode;
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
        if (!(o instanceof AnimationWatcher)) return false;
        AnimationWatcher watcher = (AnimationWatcher) o;
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

    @Override
    public Animation getAnimation() {
        return animation;
    }

    @Override
    public @Nullable AnimationStarter.AnimationData getNextAnimation() {
        return nextAnimation;
    }

    public static class Serializer implements WatcherSerializer<AnimationWatcher> {
        public void serialize(AnimationWatcher watcher, PacketBuffer buffer) {
            buffer.writeResourceLocation(watcher.getAnimation().getId());
            buffer.writeInt(watcher.getElapsedTime());
            buffer.writeFloat(watcher.timeline.getSpeed());
            buffer.writeBoolean(watcher.doNotTransitToNull);
            buffer.writeBoolean(watcher.timeline.isReversed());
            buffer.writeVarInt(LoopMode.ORDINAL_LOOKUP.from(watcher.loopMode));

            boolean hasNextAnimation = watcher.nextAnimation != null;
            buffer.writeBoolean(watcher.nextAnimation != null);
            if (hasNextAnimation) {
                AnimationStarter.AnimationData.encode(watcher.nextAnimation, buffer);
            }
        }

		public AnimationWatcher deserialize(PacketBuffer buffer) {
            Animation animation = AnimationRegistry.getAnimation(buffer.readResourceLocation());
            int elapsedTime = buffer.readInt();
            float speed = buffer.readFloat();
            boolean transitNo = buffer.readBoolean();
            boolean reversed = buffer.readBoolean();
            LoopMode loopMode = LoopMode.ORDINAL_LOOKUP.by(buffer.readVarInt());

            AnimationStarter.AnimationData nextAnimationData = null;
            boolean hasNextAnimation = buffer.readBoolean();
            if (hasNextAnimation) {
                nextAnimationData = AnimationStarter.AnimationData.decode(buffer);
            }

            AnimationWatcher watcher = new AnimationWatcher(animation, speed, transitNo, reversed, loopMode, nextAnimationData);
            watcher.timeline.set(System.currentTimeMillis() - elapsedTime);

            return watcher;
        }
	}
}
