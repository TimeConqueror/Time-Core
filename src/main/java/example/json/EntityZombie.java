package example.json;

import example.EntityStupidAnimal;
import example.ModEntities;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.world.World;
import ru.timeconqueror.timecore.api.client.render.animation.AnimationManager;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimationProvider;

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
            animationManager.startAnimationIgnorable(ModEntities.hitAnimation, 333);
            animationManager.startAnimationIgnorable(ModEntities.scalingAnimation, 333);
            animationManager.startAnimationIgnorable(ModEntities.offsettingAnimation, 333);
        }
    }
}
