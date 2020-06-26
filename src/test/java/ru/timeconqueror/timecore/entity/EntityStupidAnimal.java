package ru.timeconqueror.timecore.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.world.World;

@SuppressWarnings("EntityConstructor")
public abstract class EntityStupidAnimal extends AnimalEntity {

    public EntityStupidAnimal(EntityType<? extends AnimalEntity> type, World worldIn) {
        super(type, worldIn);
    }

    @Override
    protected void registerGoals() {
//        this.goalSelector.addGoal(0, new SwimGoal(this));
//        this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
//        this.goalSelector.addGoal(3, new BreedGoal(this, 1.0D));
//        this.goalSelector.addGoal(5, new FollowParentGoal(this, 1.1D));
//        this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
//        this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
//        this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
    }

    public double getSpeed() {
        return 0.3D;
    }

    @Override
    protected void registerAttributes() {
        super.registerAttributes();
        this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getSpeed());
        this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(getSpeed());
    }
}
