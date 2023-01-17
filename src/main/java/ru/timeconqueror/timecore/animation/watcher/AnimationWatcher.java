package ru.timeconqueror.timecore.animation.watcher;

import net.minecraft.network.FriendlyByteBuf;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationRegistry;
import ru.timeconqueror.timecore.animation.AnimationStarter;
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
    protected final FreezableTime startTime;
    /**
     * Speed factor of the animation
     */
    protected final float speed;
    private boolean inited = false;
    protected Animation animation;
    @Nullable
    private final AnimationStarter.AnimationData nextAnimation;
    private final boolean doNotTransitToNull;

    public AnimationWatcher(AnimationStarter.AnimationData currentAnimation) {
        this(currentAnimation.getAnimation(), currentAnimation.getSpeedFactor(), currentAnimation.doNotTransitToNull(), currentAnimation.getNextAnimationData());
    }

    public AnimationWatcher(Animation animation, float speed, boolean doNotTransitToNull, @Nullable AnimationStarter.AnimationData nextAnimation) {
        Requirements.greaterThan(speed, 0);
        this.startTime = new FreezableTime(System.currentTimeMillis());
        this.animation = animation;
        this.speed = speed;
        this.nextAnimation = nextAnimation;
        this.doNotTransitToNull = doNotTransitToNull;
    }

	public boolean requiresInit() {
		return !inited;
	}

	@OverridingMethodsMustInvokeSuper
	public void init(ITimeModel model) {
		inited = true;
	}

	@Nullable
	public AnimationWatcher next() {
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

	@Nullable
	public AnimationStarter.AnimationData getNextAnimationData() {
		return nextAnimation;
	}

	public void resetTimer() {
		startTime.set(System.currentTimeMillis());
    }

    @Override
    public Animation getAnimation() {
        return animation;
    }

    public boolean isAnimationEnded(long time) {
        return time > startTime.get() + Math.round(animation.getLength() / speed);
    }

    @Override
    public int getExistingTime(long time) {
        return (int) MathUtils.coerceInRange((time - startTime.get()), 0, getLength());
    }

    @Override
    public float getSpeed() {
        return speed;
    }

    public void freeze() {
        startTime.freeze();
    }

    public void unfreeze() {
        startTime.unfreeze();
    }

    @Override
    public int getLength() {
        return Math.round(getAnimation().getLength() / speed);
    }

    /**
     * Returns animation time to be used in animation frame calculation.
     */
    @Override
    public int getCurrentAnimationTime(long time) {
        return Math.round(getExistingTime(time) * getSpeed());
    }

    @Override
    public String toString() {
        return String.format("AnimationWatcher {Animation: %s, Time Passed: %d / %d, Passed Animation Time: %d, Speed: %f, Initialized: %b}", animation, getExistingTime(), getLength(), getCurrentAnimationTime(), getSpeed(), inited);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AnimationWatcher watcher)) return false;
        return Float.compare(watcher.speed, speed) == 0 &&
                animation.equals(watcher.animation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(speed, animation);
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
        return false;
    }

    protected static class FreezableTime {
        private long time;
        private long freezingTime = -1;

        public FreezableTime(long time) {
            this.time = time;
        }

        public void freeze() {
            if (freezingTime == -1) {
                freezingTime = System.currentTimeMillis();
            }
        }

        public void unfreeze() {
            if (freezingTime != -1) {
                time += System.currentTimeMillis() - freezingTime;
                freezingTime = -1;
            }
        }

		public long get() {
			if (freezingTime != -1) {
				return time + (System.currentTimeMillis() - freezingTime);
			} else {
				return time;
			}
		}

		public void set(long time) {
			this.time = time;
		}

		@Override
		public String toString() {
			return "FreezableTime{" +
					"startTime=" + time + "ms" +
					", beingFrozen=" + (System.currentTimeMillis() - freezingTime) + "ms" +
					'}';
		}
	}

	public static class Serializer implements WatcherSerializer<AnimationWatcher> {
		public void serialize(AnimationWatcher watcher, FriendlyByteBuf buffer) {
			buffer.writeResourceLocation(watcher.getAnimation().getId());
			buffer.writeInt(watcher.getExistingTime());
            buffer.writeFloat(watcher.speed);
            buffer.writeBoolean(watcher.doNotTransitToNull);

			boolean hasNextAnimation = watcher.nextAnimation != null;
			buffer.writeBoolean(watcher.nextAnimation != null);
			if (hasNextAnimation) {
				AnimationStarter.AnimationData.encode(watcher.nextAnimation, buffer);
			}
		}

		public AnimationWatcher deserialize(FriendlyByteBuf buffer) {
			Animation animation = AnimationRegistry.getAnimation(buffer.readResourceLocation());
			int existingTime = buffer.readInt();
            float speed = buffer.readFloat();
            boolean transitNo = buffer.readBoolean();

			AnimationStarter.AnimationData nextAnimationData = null;
			boolean hasNextAnimation = buffer.readBoolean();
			if (hasNextAnimation) {
				nextAnimationData = AnimationStarter.AnimationData.decode(buffer);
			}

            AnimationWatcher watcher = new AnimationWatcher(animation, speed, transitNo, nextAnimationData);
			watcher.startTime.set(System.currentTimeMillis() - existingTime);

			return watcher;
		}
	}
}
