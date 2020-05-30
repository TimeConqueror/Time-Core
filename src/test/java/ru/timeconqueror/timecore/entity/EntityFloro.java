package ru.timeconqueror.timecore.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.client.render.animation.AnimationAPI;
import ru.timeconqueror.timecore.api.client.render.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationManager;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationProvider;
import ru.timeconqueror.timecore.registry.TEntities;

import javax.annotation.Nullable;
import java.util.Random;

@SuppressWarnings("EntityConstructor")
public class EntityFloro extends EntityStupidAnimal implements IAnimationProvider {
    private IAnimationManager animationManager = AnimationAPI.newManagerFactory()
            .addMainLayer()
            .addLayer("attack", 1, BlendType.ADDING, 0.9F)
            .build();

    public EntityFloro(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        return new EntityFloro((EntityType<? extends AnimalEntity>) getType(), this.world);
    }

    @Override
    public double getSpeed() {
        return 0.37D;
    }

    public boolean isFemale() {
        return false;
    }

    @NotNull
    @Override
    public IAnimationManager getAnimationManager() {
        return animationManager;
    }

    @Override
    public void livingTick() {
        super.livingTick();

        if ((System.currentTimeMillis() / 1000) % 5 == 0) {
//            animationManager.getMainLayer().removeAnimation();
//            animationManager.getLayer("main").removeAnimation();
            AnimationAPI.startAnimation(TEntities.FLORO_WALK)
                    .setIgnorable(true)
                    .setSpeed(1.5F)
                    .startAt(animationManager.getMainLayer());
//            animationManager.startAnimationIgnorable(TEntities.FLORO_WALK, 333);
//            animationManager.removeAnimation(2000);
//            animationManager.startAnimation(TEntities.SCALING_ANIMATION, InsertType.IGNORE);
//            animationManager.startAnimation(TEntities.OFFSETTING_ANIMATION, InsertType.IGNORE);
        }

        if (new Random().nextInt(20) == 0) {
            AnimationAPI.startAnimation(TEntities.FLORO_SHOOT)
                    .setIgnorable(true)
                    .startAt(animationManager.getLayer("attack"));
        }
    }
}
