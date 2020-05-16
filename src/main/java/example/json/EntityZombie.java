package example.json;

import example.EntityStupidAnimal;
import example.ModEntities;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.api.client.render.IAnimationProvider;
import ru.timeconqueror.timecore.api.client.render.InsertType;
import ru.timeconqueror.timecore.client.render.animation.AnimationManager;

import javax.annotation.Nullable;

public class EntityZombie extends EntityStupidAnimal implements IAnimationProvider {
    private AnimationManager animationManager = new AnimationManager();

    public EntityZombie(World worldIn) {
        super(worldIn);
        this.setSize(1.2F, 2F);
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new EntityZombie(this.world);
    }

    @Override
    public double getSpeed() {
        return 0.37D;
    }

    public boolean isFemale() {
        return false;
    }

    @Override
    public AnimationManager getAnimationManager() {
        return animationManager;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if ((System.currentTimeMillis() / 1000) % 5 == 0) {
            animationManager.startAnimation(ModEntities.hitAnimation, InsertType.IGNORE);
            animationManager.startAnimation(ModEntities.scalingAnimation, InsertType.IGNORE);
            animationManager.startAnimation(ModEntities.offsettingAnimation, InsertType.IGNORE);
        }
    }
}
