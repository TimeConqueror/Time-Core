package ru.timeconqueror.timecore.animation.entityai;

import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.MathHelper;
import ru.timeconqueror.timecore.animation.AnimationStarter.AnimationData;
import ru.timeconqueror.timecore.animation.component.DelayedAction;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

import java.util.EnumSet;
import java.util.function.BiConsumer;

public class AnimatedRangedAttackGoal<T extends MobEntity & AnimatedObject<T>> extends Goal {
    public static final BiConsumer<IRangedAttackMob, ActionData> STANDARD_RUNNER =
            (entity, actionData) -> entity.performRangedAttack(actionData.getAttackTarget(), actionData.getDistanceFactor());

    private final T entity;
    private LivingEntity attackTarget;
    private int rangedAttackTime = -1;
    private final double entityMoveSpeed;
    private int seeTime;
    private final float attackInterval;
    private final float attackRadius;
    private final float maxAttackDistance;
    private final DelayedAction<T, ActionData> attackAction;

    public AnimatedRangedAttackGoal(T attacker, DelayedAction<T, ActionData> attackAction, double moveSpeed, float maxAttackDistance) {
        this.entity = attacker;
        this.entityMoveSpeed = moveSpeed;
        this.attackRadius = maxAttackDistance;
        this.maxAttackDistance = maxAttackDistance * maxAttackDistance;
        this.attackAction = attackAction;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));

        AnimationData data = attackAction.getAnimationStarter().getData();
        this.attackInterval = AnimationUtils.milliSecondsToTicks(data.getAnimation().getLength()) * data.getSpeedFactor();
    }

    /**
     * Returns whether execution should begin. You can also read and cache any state necessary for execution in this
     * method as well.
     */
    @Override
    public boolean canUse() {
        LivingEntity livingentity = this.entity.getTarget();
        if (livingentity != null && livingentity.isAlive()) {
            this.attackTarget = livingentity;
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns whether an in-progress EntityAIBase should continue executing
     */
    @Override
    public boolean canContinueToUse() {
        return this.canUse() || !this.entity.getNavigation().isDone();
    }

    /**
     * Reset the task's internal state. Called when this task is interrupted by another one
     */
    @Override
    public void stop() {
        this.attackTarget = null;
        this.seeTime = 0;
        this.rangedAttackTime = -1;
    }

    /**
     * Keep ticking a continuous task that has already been started
     */
    @Override
    public void tick() {
        double d0 = this.entity.distanceToSqr(this.attackTarget.getX(), this.attackTarget.getY(), this.attackTarget.getZ());
        boolean flag = this.entity.getSensing().canSee(this.attackTarget);
        if (flag) {
            ++this.seeTime;
        } else {
            this.seeTime = 0;
        }

        if (!(d0 > (double) this.maxAttackDistance) && this.seeTime >= 5) {
            this.entity.getNavigation().stop();
        } else {
            this.entity.getNavigation().moveTo(this.attackTarget, this.entityMoveSpeed);
        }

        this.entity.getLookControl().setLookAt(this.attackTarget, 30.0F, 30.0F);
        if (--this.rangedAttackTime == 0) {
            if (!flag) {
                return;
            }

            float f = MathHelper.sqrt(d0) / this.attackRadius;
            float distanceFactor = MathHelper.clamp(f, 0.1F, 1.0F);

            entity.getSystem().getActionManager().enableAction(attackAction, new ActionData(distanceFactor, attackTarget));

            this.rangedAttackTime = MathHelper.ceil(this.attackInterval);
        } else if (this.rangedAttackTime < 0) {
            this.rangedAttackTime = MathHelper.ceil(this.attackInterval);
        }
    }

    public static class ActionData {
        private final float distanceFactor;
        private final LivingEntity attackTarget;

        public ActionData(float distanceFactor, LivingEntity attackTarget) {
            this.distanceFactor = distanceFactor;
            this.attackTarget = attackTarget;
        }

        /**
         * This is an attack target, that was captured, when action was only started.
         * So, attack target may be invulnerable, already killed, etc, at the moment of damage action calling time,
         * that's why you need to check it there.
         */
        public LivingEntity getAttackTarget() {
            return attackTarget;
        }

        public float getDistanceFactor() {
            return distanceFactor;
        }
    }
}
