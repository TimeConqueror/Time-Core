package ru.timeconqueror.timecore.tests.animation.entity.registry;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemGroup;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.EntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.tests.animation.entity.entity.TowerGuardianEntity;

public class AnimTestEntityRegistry {
    @AutoRegistrable
    private static final EntityRegister REGISTER = new EntityRegister(TimeCore.MODID);

    public static final EntityType<TowerGuardianEntity> TOWER_GUARDIAN = REGISTER.registerLiving("tower_guardian",
                    EntityType.Builder.of(TowerGuardianEntity::new, EntityClassification.MONSTER)
                            .setTrackingRange(80)
                            .setShouldReceiveVelocityUpdates(true)
                            .sized(1, 2)
            )
            .attributes(() -> TowerGuardianEntity.createAttributes().build())
            .spawnEgg(0xFF00FF00, 0xFF000000, ItemGroup.TAB_MISC)
            .retrieve();
}
