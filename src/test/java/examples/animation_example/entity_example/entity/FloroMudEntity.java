package examples.animation_example.entity_example.entity;

import examples.animation_example.entity_example.registry.EntityRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ItemSupplier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.ApiStatus;

@OnlyIn(
        value = Dist.CLIENT,
        _interface = ItemSupplier.class
)
public class FloroMudEntity extends DamagingProjectileEntity implements ItemSupplier {
    @ApiStatus.Internal
    public FloroMudEntity(EntityType<FloroMudEntity> type, Level world) {
        super(type, world);
    }

    public FloroMudEntity(Level world, LivingEntity thrower, float damage) {
        super(EntityRegistry.FLORO_PROJ, world, thrower, damage);
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