package ru.timeconqueror.timecore.animation_example.entity_example.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class DamagingProjectileEntity extends ThrowableProjectile {
    public float damage;

    @ApiStatus.Internal
    public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> type, Level world) {
        super(type, world);
    }

    public DamagingProjectileEntity(EntityType<? extends DamagingProjectileEntity> type, Level world, LivingEntity thrower, float damage) {
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
                remove(RemovalReason.DISCARDED);
            }
        }
    }

    @Override
    protected void onHit(HitResult result) {
        if (!level.isClientSide) {
            if (result.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHitResult = (EntityHitResult) result;
                Entity target = entityHitResult.getEntity();

                if (!Objects.equals(target, getOwner())) {
                    onEntityImpact(entityHitResult, target);

                    remove(RemovalReason.DISCARDED);
                }
            } else {
                remove(RemovalReason.DISCARDED);
            }
        }
    }

    /**
     * Called, when projectile touches target entity.
     *
     * @param result result of projectile ray-tracing
     * @param target target of projectile. Never equals to the thrower.
     */
    protected void onEntityImpact(EntityHitResult result, Entity target) {
//        target.attackEntityFrom(DamageSource.causeThrownDamage(this, getThrower()), damage);
    }

    @Override
    public Packet<?> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    protected float getGravity() {
        return 0.001F;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);

        compound.putFloat("damage", damage);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);

        damage = compound.getFloat("damage");
    }
}