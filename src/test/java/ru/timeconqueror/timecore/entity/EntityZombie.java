package ru.timeconqueror.timecore.entity;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.ActionControllerBuilder;
import ru.timeconqueror.timecore.animation.AnimationManagerBuilder;
import ru.timeconqueror.timecore.api.animation.ActionController;
import ru.timeconqueror.timecore.api.animation.AnimationProvider;

import javax.annotation.Nullable;

@SuppressWarnings("EntityConstructor")
public class EntityZombie extends EntityStupidAnimal implements AnimationProvider<EntityZombie> {
    private final ActionController<EntityZombie> actionController = new ActionControllerBuilder<EntityZombie>(new AnimationManagerBuilder(true)).build(this, world);

    public EntityZombie(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Nullable
    @Override
    public AgeableEntity createChild(AgeableEntity ageable) {
        return new EntityZombie((EntityType<? extends AnimalEntity>) getType(), this.world);
    }

    @Override
    public double getSpeed() {
        return 0.37D;
    }

    public boolean isFemale() {
        return false;
    }

    @Override
    public void livingTick() {
        super.livingTick();

        if ((System.currentTimeMillis() / 1000) % 5 == 0) {
//            AnimationAPI.newAnimationStarter(TEntities.SCALING_ANIMATION).startAt(animationManager.getMainLayer());
//            animationManager.startAnimationIgnorable(TEntities.HIT_ANIMATION, 333);
//            animationManager.newAnimationStarter(TEntities.OFFSETTING_ANIMATION, InsertType.IGNORE);
        }
    }

    @Override
    public @NotNull ActionController<EntityZombie> getActionController() {
        return actionController;
    }
}
