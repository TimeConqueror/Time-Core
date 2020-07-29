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
import ru.timeconqueror.timecore.animation.ActionManagerBuilder;
import ru.timeconqueror.timecore.animation.AnimationManagerBuilder;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.component.DelayedAction;
import ru.timeconqueror.timecore.animation.entityai.AnimatedMeleeAttackGoal;
import ru.timeconqueror.timecore.animation.util.StandardDelayPredicates;
import ru.timeconqueror.timecore.api.animation.ActionManager;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.registry.EntityCommonRegistryExample;

public class EntityFloro extends MonsterEntity implements AnimationProvider<EntityFloro> {
    private static final DelayedAction<EntityFloro, Object> MELEE_ATTACK_ACTION = new DelayedAction<EntityFloro, Object>(new ResourceLocation(TimeCore.MODID, "floro/melee_attack"),
            new AnimationStarter(EntityCommonRegistryExample.FLORO_SHOOT), "attack")
            .setDelayPredicate(StandardDelayPredicates.whenPassed(0.5F))
            .setOnCall(AnimatedMeleeAttackGoal.BASIC_MELEE_ATTACK_ACTION);

    private final ActionManager<EntityFloro> actionManager;

    private static final String LAYER_WALKING = "walking";
    private static final String LAYER_ATTACK = "attack";

    public EntityFloro(EntityType<? extends MonsterEntity> type, World worldIn) {
        super(type, worldIn);

        actionManager = new ActionManagerBuilder<EntityFloro>(
                AnimationManagerBuilder.create()
                        .addLayer(LAYER_WALKING, BlendType.ADDING, 1F)
                        .addLayer(LAYER_ATTACK, BlendType.ADDING, 0.9F)
                        .addWalkingAnimationHandling(new AnimationStarter(EntityCommonRegistryExample.FLORO_WALK).setSpeed(2.0F), LAYER_WALKING)
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

    @Override
    public @NotNull ActionManager<EntityFloro> getActionManager() {
        return actionManager;
    }
}
