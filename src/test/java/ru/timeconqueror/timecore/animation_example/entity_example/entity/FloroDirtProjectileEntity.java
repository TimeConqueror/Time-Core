package ru.timeconqueror.timecore.animation_example.entity_example.entity;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.IRendersAsItem;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;
import ru.timeconqueror.timecore.animation_example.entity_example.registry.EntityRegistry;

@OnlyIn(
        value = Dist.CLIENT,
        _interface = IRendersAsItem.class
)
public class FloroDirtProjectileEntity extends DamagingProjectileEntity implements IRendersAsItem {
    @ApiStatus.Internal
    public FloroDirtProjectileEntity(EntityType<FloroDirtProjectileEntity> type, World world) {
        super(type, world);
    }

    public FloroDirtProjectileEntity(World world, LivingEntity thrower, float damage) {
        super(EntityRegistry.FLORO_PROJECTILE_TYPE, world, thrower, damage);
    }

    @Override
    protected float getGravity() {
        return 0.032F;
    }

    @Override
    public ItemStack getItem() {
        return new ItemStack(Items.SNOWBALL);
    }
}