package ru.timeconqueror.timecore.animation.builders;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.PredefinedAnimation;
import ru.timeconqueror.timecore.api.animation.AnimationManager;
import ru.timeconqueror.timecore.api.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.animation.builders.IPredefinedAnimations;

import java.util.HashSet;
import java.util.Set;

public class PredefinedAnimations implements IPredefinedAnimations {
    protected PredefinedAnimation create(AnimationStarter starter, String layerName) {
        return new PredefinedAnimation(layerName, starter);
    }

    public static boolean areLayersEqual(@NotNull PredefinedAnimation anim1, @NotNull PredefinedAnimation anim2) {
        return anim1.getLayerName().equals(anim2.getLayerName());
    }

    //FIXME
//    public static boolean areAnimationsEqual(@NotNull PredefinedAnimation anim, Layer layer) {
//        return anim.getAnimationStarter().getData().getAnimation().equals(layer.getWatcherInfo().getAnimation());
//    }

    public static class EntityPredefinedAnimations extends PredefinedAnimations implements IEntityPredefinedAnimations {
        @Nullable
        private PredefinedAnimation walking;
        @Nullable
        private PredefinedAnimation idle;

        @Nullable
        public PredefinedAnimation getWalkingAnimation() {
            return walking;
        }

        @Nullable
        public PredefinedAnimation getIdleAnimation() {
            return idle;
        }

        @Override
        public void setWalkingAnimation(AnimationStarter walkingAnimationStarter, String layerName) {
            walking = create(walkingAnimationStarter, layerName);
        }

        @Override
        public void setIdleAnimation(AnimationStarter idleAnimationStarter, String layerName) {
            this.idle = create(idleAnimationStarter, layerName);
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
