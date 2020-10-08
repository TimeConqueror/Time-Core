package ru.timeconqueror.timecore.animation.builders;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.PredefinedAnimation;
import ru.timeconqueror.timecore.api.animation.AnimationManager;

import java.util.HashSet;
import java.util.Set;

public class PredefinedAnimations implements IPredefinedAnimations {
    protected PredefinedAnimation create(AnimationStarter starter, String layerName) {
        return new PredefinedAnimation(layerName, starter);
    }

    public static class EntityPredefinedAnimations extends PredefinedAnimations implements IEntityPredefinedAnimations {
        private PredefinedAnimation walking;

        @Nullable
        public PredefinedAnimation getWalkingAnimation() {
            return walking;
        }

        public void setWalkingAnimation(AnimationStarter walkingAnimationStarter, String layerName) {
            walking = create(walkingAnimationStarter, layerName);
        }
    }

    public static class Builder<T> extends PredefinedAnimations {
        private final Set<String> layers = new HashSet<>();
        private final T predefinedAnimations;

        private Builder(T predefinedAnimations) {
            this.predefinedAnimations = predefinedAnimations;
        }

        public static <T> Builder<T> of(T predefinedAnimations) {
            return new Builder<>(predefinedAnimations);
        }

        @Override
        protected PredefinedAnimation create(AnimationStarter starter, String layerName) {
            layers.add(layerName);
            return super.create(starter, layerName);
        }

        public T validate(AnimationManager manager) {
            for (String layer : layers) {
                if (!manager.getLayerNames().contains(layer)) {
                    throw new IllegalStateException(String.format("You need to define layer %s before adding animation handlers to it.", layer));
                }
            }

            return predefinedAnimations;
        }

        public T getInner() {
            return predefinedAnimations;
        }
    }
}
