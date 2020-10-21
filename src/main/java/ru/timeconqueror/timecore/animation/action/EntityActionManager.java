package ru.timeconqueror.timecore.animation.action;

import net.minecraft.entity.Entity;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.PredefinedAnimation;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations.EntityPredefinedAnimations;

public class EntityActionManager<T extends Entity> extends ActionManagerImpl<T> {
    private final EntityPredefinedAnimations predefinedAnimations;

    public EntityActionManager(BaseAnimationManager animationManager, T entity, EntityPredefinedAnimations predefinedAnimations) {
        super(animationManager, entity);
        this.predefinedAnimations = predefinedAnimations;
    }

    public void onTick() {
        T entity = getBoundObject();
        if (entity.level.isClientSide()) {
            BaseAnimationManager animationManager = getAnimationManager();

            PredefinedAnimation predefinedWalkingAnim = predefinedAnimations.getWalkingAnimation();
            PredefinedAnimation predefinedIdleAnim = predefinedAnimations.getIdleAnimation();

            // floats of movement can be almost the same (like 0 and 0.000000001), so entity moves a very short distance, which is invisible for eyes.
            // this can be because of converting coords to bytes to send them to client.
            // so checking if it's more than 1/256 of the block will fix the issue
            boolean posChanged = Math.abs(entity.getX() - entity.xo) >= 1 / 256F
                    || Math.abs(entity.getZ() - entity.zo) >= 1 / 256F;

            if (posChanged) {
                // if there is no walking anim or the idle and walking anim's layers aren't the same
                // we removes the idle animation
                if (predefinedIdleAnim != null && (predefinedWalkingAnim == null || !PredefinedAnimations.areLayersEqual(predefinedIdleAnim, predefinedWalkingAnim))) {
                    animationManager.removeAnimation(predefinedIdleAnim.getLayerName());
                }

                if (predefinedWalkingAnim != null) {
                    predefinedWalkingAnim.startAt(animationManager);
                }
            } else {
                // if there is no idle anim or the idle and walking anim's layers aren't the same
                // we removes the walking animation
                if (predefinedWalkingAnim != null && (predefinedIdleAnim == null || !PredefinedAnimations.areLayersEqual(predefinedIdleAnim, predefinedWalkingAnim))) {
                    animationManager.removeAnimation(predefinedWalkingAnim.getLayerName());
                }

                if (predefinedIdleAnim != null) {
                    predefinedIdleAnim.startAt(animationManager);
                }
            }
        }
    }
}
