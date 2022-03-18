package ru.timeconqueror.timecore.tests.animation.entity.registry;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.registries.ObjectHolder;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.EntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.util.Hacks;
import ru.timeconqueror.timecore.tests.animation.entity.entity.TowerGuardianEntity;

@ObjectHolder(TimeCore.MODID)
public class AnimTestEntityRegistry {
    @AutoRegistrable
    private static final EntityRegister REGISTER = new EntityRegister(TimeCore.MODID);

    public static final EntityType<TowerGuardianEntity> TOWER_GUARDIAN = Hacks.promise();

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.registerMob("tower_guardian",
                        EntityType.Builder.of(TowerGuardianEntity::new, MobCategory.MONSTER)
                                .setTrackingRange(80)
                                .setShouldReceiveVelocityUpdates(true)
                                .sized(1, 2)
                )
                .spawnEgg(0xFF00FF00, 0xFF000000, CreativeModeTab.TAB_MISC)
                .attributes(() -> TowerGuardianEntity.createAttributes().build());
    }
}
