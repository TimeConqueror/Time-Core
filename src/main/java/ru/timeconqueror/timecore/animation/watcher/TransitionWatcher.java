package ru.timeconqueror.timecore.animation.watcher;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationRegistry;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.component.Transition;
import ru.timeconqueror.timecore.animation.util.WatcherSerializer;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.Requirements;

import java.util.Objects;

public class TransitionWatcher extends AnimationWatcher {
    public static final Animation TRANSITION = new BasicAnimation(LoopMode.DO_NOT_LOOP, TimeCore.rl("internal/transition"), "transition", 0, null) {
    };

    private final int transitionTime;
    @Nullable
    private final AnimationStarter.AnimationData destination;
    private final Animation source;
    private final int sourceExistingTime;

    private TransitionWatcher(Animation source, int sourceExistingTime, int transitionTime, @Nullable AnimationStarter.AnimationData destination) {
        super(TRANSITION, transitionTime, 1.0F, false, false, LoopMode.DO_NOT_LOOP, destination);

        Requirements.greaterOrEquals(transitionTime, 0);

        this.transitionTime = transitionTime;
        this.destination = destination;
        this.source = source;
        this.sourceExistingTime = sourceExistingTime;
    }

    public static TransitionWatcher fromNullSource(AnimationStarter.AnimationData destination) {
        return new TransitionWatcher(TRANSITION, 0, destination.getTransitionTime(), destination);
    }

    public static TransitionWatcher from(AnimationWatcher source, AnimationStarter.AnimationData destination) {
        return new TransitionWatcher(source.getAnimation(), source.getAnimationTime(), destination.getTransitionTime(), destination);
    }

    public static TransitionWatcher toNullDestination(AnimationWatcher source, int transitionTime) {
        return new TransitionWatcher(source.getAnimation(), source.getAnimationTime(), transitionTime, null);
    }

    @Override
    public void init(ITimeModel model) {
        super.init(model);

        if (model != null) {
            AnimationStarter.AnimationData notNullDest = destination != null ? destination : new AnimationStarter(Animation.NULL).getData();//TODO remove nulls
            animation = Transition.create(source, notNullDest, model, sourceExistingTime, transitionTime);
        } else {
            animation = Transition.createForServer(source, getDestination(), transitionTime);
        }
    }

    @Override
    @Nullable
    public AnimationWatcher next() {
        return destination != null ? new AnimationWatcher(destination) : null;
    }

    public Animation getDestination() {
        return destination != null ? destination.getAnimation() : Animation.NULL;
    }

    @Override
    public boolean isAutoTransition() {
        return true;
    }

    @Override
    public boolean autoTransitsFrom(Animation animation) {
        return animation.equals(source);
    }

    @Override
    public int getElapsedLength() {
        return transitionTime;
    }

    @Override
    public boolean autoTransitsTo(Animation animation) {
        return animation.equals(getDestination());
    }

    @Override
    public String toString() {
        long time = System.currentTimeMillis();
        return "Transition{" +
                "Progress Time: " + getAnimationTime(time) + " / " + transitionTime + " ms" +
                ", From: " + source.getId() + "(" + sourceExistingTime + "/" + source.getLength() + ")" +
                ", To: " + (destination != null ? destination.getAnimation().getId() : null) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TransitionWatcher)) return false;
        TransitionWatcher that = (TransitionWatcher) o;

        boolean destEquals = false;
        if (destination == null && that.destination == null) {
            destEquals = true;
        } else if (destination != null && that.destination != null) {
            if (destination.getAnimation().equals(that.destination.getAnimation())
                    && Float.compare(destination.getSpeed(), that.destination.getSpeed()) == 0) {
                destEquals = true;
            }
        }

        return destEquals &&
                transitionTime == that.transitionTime &&
                Objects.equals(source, that.source);
    }

    @Override
    public int hashCode() {
        Animation dest = getDestination();
        float destSpeed = destination != null ? destination.getSpeed() : 0;

        return Objects.hash(super.hashCode(), transitionTime, dest, destSpeed, source);
    }

    public static class Serializer implements WatcherSerializer<TransitionWatcher> {
        @Override
        public void serialize(TransitionWatcher watcher, PacketBuffer buffer) {
            boolean hasSource = watcher.source != null;
            buffer.writeBoolean(hasSource);
            if (hasSource) {
                buffer.writeResourceLocation(watcher.source.getId());
                buffer.writeInt(watcher.sourceExistingTime);
            }

            boolean hasDestination = watcher.destination != null;
            buffer.writeBoolean(hasDestination);
            if (hasDestination) {
                AnimationStarter.AnimationData.encode(watcher.destination, buffer);
            }

            int transitionTime = Math.max(watcher.getElapsedLength() - watcher.getElapsedTime(), 0);
            buffer.writeInt(transitionTime);
        }

        @Override
        public TransitionWatcher deserialize(PacketBuffer buffer) {
            boolean hasSource = buffer.readBoolean();

            Animation source = Animation.NULL;
            int sourceExistingTime = -1;
            if (hasSource) {
                ResourceLocation id = buffer.readResourceLocation();
                source = AnimationRegistry.getAnimation(id);
                sourceExistingTime = buffer.readInt();
            }

            AnimationStarter.AnimationData destination = null;
            boolean hasDestination = buffer.readBoolean();
            if (hasDestination) {
                destination = AnimationStarter.AnimationData.decode(buffer);
            }

            int transitionTime = buffer.readInt();

            return new TransitionWatcher(source, sourceExistingTime, transitionTime, destination);
        }
    }
}
