package ru.timeconqueror.timecore.animation_example.entity_example.entity;

import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.monster.MonsterEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.component.DelayedAction;
import ru.timeconqueror.timecore.animation.entityai.AnimatedRangedAttackGoal;
import ru.timeconqueror.timecore.animation.util.StandardDelayPredicates;
import ru.timeconqueror.timecore.animation_example.entity_example.registry.EntityAnimations;
import ru.timeconqueror.timecore.api.animation.ActionManager;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationAPI;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.builders.AnimationSystemBuilder;

import java.util.EnumSet;

/**
 * How vanilla tasks work:
 * If flags, from which task's mutex consists of, are disabled, then tasks with this mutex won't be run.
 * <p>
 * If task has higher priority (lower number), than system will check if current task with lower priority is interruptible.
 * If it is so, then it will be finished, and new task will task its place
 * <p>
 * If task has a lower priority (higher number), it's checked by system if it can work in parallel (if mutex isn't the same).
 */
public class FloroEntity extends MonsterEntity implements IRangedAttackMob, AnimatedObject<FloroEntity> {
    private static final DataParameter<Boolean> HIDDEN = EntityDataManager.createKey(FloroEntity.class, DataSerializers.BOOLEAN);

    private static final DelayedAction<FloroEntity, AnimatedRangedAttackGoal.ActionData> RANGED_ATTACK_ACTION;
    private static final DelayedAction<FloroEntity, Object> REVEALING_ACTION;
    private static final DelayedAction<FloroEntity, Void> HIDING_ACTION;

    private static final String LAYER_SHOWING = "showing";
    private static final String LAYER_WALKING = "walking";
    private static final String LAYER_ATTACK = "attack";

    static {
        RANGED_ATTACK_ACTION = new DelayedAction<FloroEntity, AnimatedRangedAttackGoal.ActionData>(TimeCore.rl("floro/shoot"), new AnimationStarter(EntityAnimations.floroShoot).setSpeed(1.5F), "attack")
                .setDelayPredicate(StandardDelayPredicates.whenPassed(0.5F))
                .setOnCall(AnimatedRangedAttackGoal.STANDARD_RUNNER);

        REVEALING_ACTION = new DelayedAction<FloroEntity, Object>(TimeCore.rl("floro/reveal"), new AnimationStarter(EntityAnimations.floroReveal).setTransitionTime(0), LAYER_SHOWING)
                .setDelayPredicate(StandardDelayPredicates.onEnd())
                .setOnCall((floroEntity, o) -> floroEntity.setHidden(false));
        HIDING_ACTION = new DelayedAction<FloroEntity, Void>(TimeCore.rl("floro/hiding"), new AnimationStarter(EntityAnimations.floroHide).setNextAnimation(AnimationAPI.createStarter(EntityAnimations.floroHidden).setTransitionTime(0)), LAYER_SHOWING)
                .setDelayPredicate(StandardDelayPredicates.onEnd())
                .setOnCall((floroEntity, nothing) -> floroEntity.setHidden(true));
    }

    private final AnimationSystem<FloroEntity> animationSystem;
    //server side only
    private boolean isHiding = false;

    public FloroEntity(EntityType<? extends FloroEntity> type, World world) {
        super(type, world);

        animationSystem = AnimationSystemBuilder.forEntity(this, world, builder -> {
            builder.addLayer(LAYER_SHOWING, BlendType.OVERRIDE, 1F);
            builder.addLayer(LAYER_WALKING, BlendType.ADDING, 1F);
            builder.addLayer(LAYER_ATTACK, BlendType.ADDING, 0.9F);
        }, predefinedAnimations -> predefinedAnimations.setWalkingAnimation(new AnimationStarter(EntityAnimations.floroWalk).setSpeed(3F), LAYER_WALKING));
    }

    @Override
    protected void registerData() {
        super.registerData();

        getDataManager().register(HIDDEN, true);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);

        compound.putBoolean("hidden", isHidden());
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);

        if (compound.contains("hidden")) {
            setHidden(compound.getBoolean("hidden"));
        }
    }

    @Override
    public void onAddedToWorld() {
        if (isServerWorld() && isHidden()) {
            startHiddenAnimation();
        }
    }

    @Override
    protected void registerGoals() {
        goalSelector.addGoal(0, new FloroRevealingGoal()); //mutex 1
        goalSelector.addGoal(1, new FloroHidingGoal()); //mutex 1
        goalSelector.addGoal(2, new FloroHiddenGoal()); //mutex 1
        goalSelector.addGoal(3, new SwimGoal(this));//mutex 4
        goalSelector.addGoal(4, new AvoidEntityGoal<>(this, WolfEntity.class, 6.0F, 1.0D, 1.2D));//mutex 1

        goalSelector.addGoal(5, new AnimatedRangedAttackGoal<>(this, RANGED_ATTACK_ACTION, 1.0F, 16.0F));//mutex 3

        goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));//mutex 1
        goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 8.0F));//mutex 2
        goalSelector.addGoal(7, new LookRandomlyGoal(this));//mutex 3

        targetSelector.addGoal(1, new HurtByTargetGoal(this));
        targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, PlayerEntity.class, 5/*will target if rand.next(chance) == 0*/, true, false, null));
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();

        getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25);
        getAttribute(SharedMonsterAttributes.FOLLOW_RANGE).setBaseValue(25);
        getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.26);
    }

    @Override
    public void livingTick() {
        if (isServerWorld()) {
            if (isHidden()) {
                isHiding = false;
            }

            if (canMove()) {
                goalSelector.enableFlag(Goal.Flag.LOOK);
                goalSelector.enableFlag(Goal.Flag.JUMP);
            } else {
                goalSelector.disableFlag(Goal.Flag.LOOK);
                goalSelector.disableFlag(Goal.Flag.JUMP); //actually doesn't help in some situations as these tasks still can be activated, if executingTasks list in EntityAITasks is empty.
            }
        }

        super.livingTick();//this method should be the last, because here we update AI task. If it is before our own code, then disabling control flags won't work
    }

    @Override
    public void addVelocity(double x, double y, double z) {
        if (canMove()) {
            super.addVelocity(x, y, z);
        } else {
            super.addVelocity(0, 0, 0);
        }
    }

    @Override
    public void knockBack(Entity entityIn, float strength, double xRatio, double zRatio) {
        if (canMove()) {
            super.knockBack(entityIn, strength, xRatio, zRatio);
        }
    }

    private boolean canMove() {
        return !isHidden() && !getActionManager().isActionEnabled(REVEALING_ACTION) && !isHiding;
    }

    private void startHiddenAnimation() {
        AnimationAPI.createStarter(EntityAnimations.floroHidden).setTransitionTime(0)
                .startAt(getActionManager().getAnimationManager(), LAYER_SHOWING);
    }

    @Override
    public void attackEntityWithRangedAttack(LivingEntity target, float distanceFactor) {
        FloroDirtProjectileEntity projectile = new FloroDirtProjectileEntity(this.world, this, 1.0F);
        double dX = target.getPosX() - this.getPosX();
        double dY = target.getBoundingBox().minY + (double) (target.getHeight() / 3.0F) - projectile.getPosY();
        double dZ = target.getPosZ() - this.getPosZ();
        double distortion = MathHelper.sqrt(dX * dX + dZ * dZ);
        projectile.shoot(dX, dY + distortion * 0.20000000298023224D, dZ, 1.6F, (float) (7 - this.world.getDifficulty().getId()));

        this.world.addEntity(projectile);
    }

    public boolean isHidden() {
        return getDataManager().get(HIDDEN);
    }

    public void setHidden(boolean value) {
        getDataManager().set(HIDDEN, value);
    }


    public @NotNull AnimationSystem<FloroEntity> getSystem() {
        return animationSystem;
    }

    @Override
    protected float getStandingEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return this.getHeight() * 0.78F;
    }

    private class FloroRevealingGoal extends Goal {
        public FloroRevealingGoal() {
            setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            return !canMove() && getAttackTarget() != null;
        }

        @Override
        public void startExecuting() {
            ActionManager<FloroEntity> actionManager = getActionManager();
            if (isHidden()) {
                actionManager.enableAction(REVEALING_ACTION, null);
            } else {
                actionManager.disableAction(HIDING_ACTION);
            }
        }

        @Override
        public boolean isPreemptible() {
            return false;
        }
    }

    private class FloroHiddenGoal extends Goal {
        public FloroHiddenGoal() {
            setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            return isHidden();
        }
    }

    private class FloroHidingGoal extends Goal {
        public FloroHidingGoal() {
            setMutexFlags(EnumSet.of(Flag.MOVE));
        }

        @Override
        public boolean shouldExecute() {
            return !isHidden() && getAttackTarget() == null;
        }

        @Override
        public boolean shouldContinueExecuting() {
            return !isHidden();
        }

        @Override
        public void startExecuting() {
            isHiding = true;
        }

        @Override
        public void tick() {
            ActionManager<FloroEntity> actionManager = getActionManager();
            if (!actionManager.isActionEnabled(HIDING_ACTION) && ticksExisted % (12 * 20) == 0) {
                actionManager.enableAction(HIDING_ACTION, null);
            }
        }

        @Override
        public void resetTask() {
            isHiding = false;
        }
    }
}
