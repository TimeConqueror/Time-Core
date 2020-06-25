package ru.timeconqueror.timecore.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.RandomWalkingGoal;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.AnimationManagerBuilder;
import ru.timeconqueror.timecore.animation.StateMachineBuilder;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;
import ru.timeconqueror.timecore.api.animation.StateMachine;
import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.registry.TEntities;

import javax.annotation.Nullable;

@SuppressWarnings("EntityConstructor")
public class EntityFloro extends EntityStupidAnimal implements AnimationProvider<EntityFloro> {
    private final StateMachine<EntityFloro> stateMachine;

    public EntityFloro(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);

        stateMachine = new StateMachineBuilder<EntityFloro>(
                new AnimationManagerBuilder(true)
//                        .addLayer("walking", 0, BlendType.OVERRIDE, 1F)
                        .setWalkingAnimation(TEntities.FLORO_WALK)
//                        .addMainLayer()
                        .addLayer("attack", 1, BlendType.ADDING, 0.9F)
        ).build(this, world);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new RandomWalkingGoal(this, getSpeed()));
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        return new EntityFloro((EntityType<? extends AnimalEntity>) getType(), this.world);
    }

    @Override
    public double getSpeed() {
        return 0.3D;
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
