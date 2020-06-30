package ru.timeconqueror.timecore.animation.entityai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;
import ru.timeconqueror.timecore.animation.component.DelayedAction;
import ru.timeconqueror.timecore.api.animation.ActionManager;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;

import java.util.Objects;
import java.util.function.Consumer;

public class AnimatedMeleeAttackGoal<T extends CreatureEntity & AnimationProvider<T>> extends MeleeAttackGoal {
    public static final Consumer<? super CreatureEntity> BASIC_MELEE_ATTACK_ACTION = entity -> {
        entity.swingArm(Hand.MAIN_HAND);
        entity.attackEntityAsMob(Objects.requireNonNull(entity.getAttackTarget()));
    };
    private final DelayedAction<T> delayedAttackAction;

    public AnimatedMeleeAttackGoal(T creature, DelayedAction<T> delayedAttackAction, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);

        this.delayedAttackAction = delayedAttackAction;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);

        AnimationProvider<T> stateProvider = (AnimationProvider<T>) attacker;
        ActionManager<T> actionManager = stateProvider.getActionManager();

        if (distToEnemySqr <= d0 && !actionManager.isActionEnabled(delayedAttackAction)) {
            actionManager.enableAction(delayedAttackAction);
        }
    }
}
