package example;

import net.minecraft.entity.EntityAgeable;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityPhoenix extends EntityStupidAnimal {

    public EntityPhoenix(World worldIn) {
        super(worldIn);
        this.setSize(1.2F, 2F);
    }

    @Nullable
    @Override
    public EntityAgeable createChild(EntityAgeable ageable) {
        return new EntityPhoenix(this.world);
    }

    @Override
    public double getSpeed() {
        return 0.37D;
    }

    public boolean isFemale() {
        return false;
    }
}
