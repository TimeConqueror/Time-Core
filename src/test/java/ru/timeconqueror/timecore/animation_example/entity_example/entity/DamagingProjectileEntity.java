package ru.timeconqueror.timecore.animation_example.entity_example.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.EntityRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class DamagingProjectileEntity extends ThrowableEntity {
    public float damage;

    @ApiStatus.Internal
    public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> type, World world) {
        super(type, world);
    }

    public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> type, World world, LivingEntity thrower, float damage) {
        super(type, thrower, world);
        this.damage = damage;
    }

    public float getDamage() {
        return damage;
    }

    @Override
    protected void defineSynchedData() {

    }

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide) {
            if (tickCount >= 20 * 10) {
                remove();
            }
        }
    }

    @Override
    protected void onHit(RayTraceResult result) {
        if (!level.isClientSide) {
            if (result.getType() == RayTraceResult.Type.ENTITY) {
                Entity target = ((EntityRayTraceResult) result).getEntity();

                if (!Objects.equals(target, getOwner())) {
                    onEntityImpact(result, target);

                    remove();
                }
            } else {
                remove();
            }
        }
    }

    /**
     * Called, when projectile touches target entity.
     *
     * @param result result of projectile ray-tracing
     * @param target target of projectile. Never equals to the thrower.
     */
    protected void onEntityImpact(RayTraceResult result, Entity target) {
//        target.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), damage);
    }



    @Override
    public IPacket<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getGravity() {
        return 0.001F;
    }

    @Override
    protected void addAdditionalSaveData(CompoundNBT compound) {
        super.addAdditionalSaveData(compound);

        compound.putFloat("damage", damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundNBT compound) {
        super.readAdditionalSaveData(compound);

        damage = compound.getFloat("damage");
    }
}