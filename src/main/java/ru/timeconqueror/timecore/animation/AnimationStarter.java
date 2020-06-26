package ru.timeconqueror.timecore.animation;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.internal.AnimationRegistry;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.client.render.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;

public class AnimationStarter {
    private final AnimationData data;

    public AnimationStarter(IAnimation animation) {
        this.data = new AnimationData(animation);
    }

    private AnimationStarter(AnimationData animationData) {
        this.data = animationData;
    }

    public AnimationStarter setIgnorable(boolean ignorable) {
        this.data.ignorable = ignorable;
        return this;
    }

    public AnimationStarter setTransitionTime(int transitionTime) {
        data.transitionTime = Math.max(transitionTime, 0);
        return this;
    }

    public AnimationStarter setSpeed(float speedFactor) {
        data.speedFactor = Math.max(speedFactor, 0.0001F);
        return this;
    }

    public void startAt(AnimationManager manager, String layerName) {
        if (manager.containsLayer(layerName)) {
            AnimationLayer layer = manager.getLayer(layerName);
            if (data.ignorable) {
                AnimationWatcher watcher = layer.getAnimationWatcher();
                if (watcher != null) {
                    if (data.prototype.equals(watcher.getAnimation()) || (watcher instanceof TransitionWatcher && data.prototype.equals(((TransitionWatcher) watcher).getDestination()))) {
                        return;//TODO add check for speed
                    }
                }
            }

            manager.setAnimation(this.data, layerName);

        } else {
            TimeCore.LOGGER.error("Can't start animation: layer with name " + layerName + " doesn't exist in provided animation manager.");
        }
    }

    public AnimationData buildData() {
        return data;
    }

    public static AnimationStarter fromAnimationData(AnimationData data) {
        return new AnimationStarter(data);
    }

    public static class AnimationData {
        final IAnimation prototype;
        boolean ignorable = true;
        int transitionTime = AnimationConstants.BASIC_TRANSITION_TIME;
        float speedFactor = 1F;

        private AnimationData(IAnimation prototype) {
            this.prototype = prototype;
        }

        public IAnimation getPrototype() {
            return prototype;
        }

        public float getSpeedFactor() {
            return speedFactor;
        }

        public int getTransitionTime() {
            return transitionTime;
        }

        public boolean isIgnorable() {
            return ignorable;
        }

        public static void encode(AnimationStarter.AnimationData animationData, PacketBuffer buffer) {
            buffer.writeResourceLocation(animationData.getPrototype().getId());
            buffer.writeFloat(animationData.getSpeedFactor());
            buffer.writeInt(animationData.getTransitionTime());
            buffer.writeBoolean(animationData.isIgnorable());
        }

        public static AnimationStarter.AnimationData decode(PacketBuffer buffer) {
            IAnimation animation = AnimationRegistry.getAnimation(buffer.readResourceLocation());

            AnimationData animationData = new AnimationData(animation);

            animationData.speedFactor = buffer.readFloat();
            animationData.transitionTime = buffer.readInt();
            animationData.ignorable = buffer.readBoolean();

            return animationData;
        }
    }
}
