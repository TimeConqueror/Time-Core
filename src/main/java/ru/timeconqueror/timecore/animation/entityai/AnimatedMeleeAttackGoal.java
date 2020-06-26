package ru.timeconqueror.timecore.animation.entityai;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.util.Hand;
import ru.timeconqueror.timecore.animation.DelayedAction;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.api.animation.StateMachine;

import java.util.Objects;
import java.util.function.Consumer;

public class AnimatedMeleeAttackGoal<T extends CreatureEntity & AnimationProvider<T>> extends MeleeAttackGoal {
    public static final Consumer<? super CreatureEntity> BASIC_MELEE_ATTACK_ACTION = entity -> {
        entity.swingArm(Hand.MAIN_HAND);
        entity.attackEntityAsMob(Objects.requireNonNull(entity.getAttackTarget()));
    };
    private final DelayedAction<T> attackAction;

    public AnimatedMeleeAttackGoal(T creature, DelayedAction<T> attackAction, double speedIn, boolean useLongMemory) {
        super(creature, speedIn, useLongMemory);

        this.attackAction = attackAction;
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void checkAndPerformAttack(LivingEntity enemy, double distToEnemySqr) {
        double d0 = this.getAttackReachSqr(enemy);

        AnimationProvider<T> stateProvider = (AnimationProvider<T>) attacker;
        StateMachine<T> stateMachine = stateProvider.getStateMachine();

        if (distToEnemySqr <= d0 && !stateMachine.isActionEnabled(attackAction)) {
            stateMachine.enableAction(attackAction);
        }
    }
}
