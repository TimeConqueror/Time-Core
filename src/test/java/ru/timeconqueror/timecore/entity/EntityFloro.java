package ru.timeconqueror.timecore.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.NearestAttackableTargetGoal;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.*;
import ru.timeconqueror.timecore.animation.entityai.AnimatedMeleeAttackGoal;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.api.animation.StateMachine;
import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.registry.TEntities;

@SuppressWarnings("EntityConstructor")
public class EntityFloro extends MonsterEntity implements AnimationProvider<EntityFloro> {
    private static final DelayedAction<EntityFloro> MELEE_ATTACK_ACTION = new DelayedAction<EntityFloro>(new ResourceLocation(TimeCore.MODID, "floro/melee_attack"),
            new AnimationStarter(TEntities.FLORO_SHOOT), "attack")
            .setDelayPredicate(StandardDelayPredicates.whenPassed(0.5F))
            .setOnCall(AnimatedMeleeAttackGoal.BASIC_MELEE_ATTACK_ACTION);
    private final StateMachine<EntityFloro> stateMachine;

    public EntityFloro(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);

        stateMachine = new StateMachineBuilder<EntityFloro>(
                new AnimationManagerBuilder(true)
                        .setWalkingAnimationStarter(new AnimationStarter(TEntities.FLORO_WALK).setSpeed(2.0F))
                        .addLayer("attack", 1, BlendType.ADDING, 0.9F)
        ).build(this, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new AnimatedMeleeAttackGoal<>(this, MELEE_ATTACK_ACTION, getAIMoveSpeed(), true));
        this.goalSelector.addGoal(1, new RandomWalkingGoal(this, getAIMoveSpeed()));

        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, true));
    }

    @Override
    public float getAIMoveSpeed() {
        return 0.3F;
    }

    public boolean isFemale() {
        return false;
    }

    @Override
    public void livingTick() {
        super.livingTick();

        if ((System.currentTimeMillis() / 1000) % 5 == 0) {
//            animationManager.getMainLayer().removeAnimation();
//            animationManager.getLayer("main").removeAnimation();
//            AnimationAPI.newAnimationStarter(TEntities.FLORO_WALK)
//                    .setIgnorable(true)
//                    .setSpeed(1.5F)
//                    .startAt(animationManager.getMainLayer());
//            animationManager.startAnimationIgnorable(TEntities.FLORO_WALK, 333);
//            animationManager.removeAnimation(2000);
//            animationManager.newAnimationStarter(TEntities.SCALING_ANIMATION, InsertType.IGNORE);
//            animationManager.newAnimationStarter(TEntities.OFFSETTING_ANIMATION, InsertType.IGNORE);
        }

//        if (new Random().nextInt(20) == 0) {
//            AnimationAPI.newAnimationStarter(TEntities.FLORO_SHOOT)
//                    .setIgnorable(true)
//                    .startAt(animationManager.getLayer("attack"));
//        }
    }

    @Override
    public @NotNull StateMachine<EntityFloro> getStateMachine() {
        return stateMachine;
    }
}
