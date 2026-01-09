package examples.visual_tests.animation.entity.registry;

import examples.visual_tests.animation.entity.entity.DebugBeaconsEntity;
import examples.visual_tests.animation.entity.entity.TowerGuardianEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.CreativeModeTabs;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.EntityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.registry.util.RegistryKeys;
import ru.timeconqueror.timecore.api.util.Hacks;

@AutoRegistrable.Entries(RegistryKeys.ENTITY_TYPES)
public class AnimTestEntityRegistry {
    @AutoRegistrable
    private static final EntityRegister REGISTER = new EntityRegister(TimeCore.MODID);

    public static EntityType<TowerGuardianEntity> TOWER_GUARDIAN = Hacks.promise();
    public static EntityType<DebugBeaconsEntity> DEBUG_BEACONS = Hacks.promise();

    @AutoRegistrable.Init
    private static void register() {
        REGISTER.registerMob("tower_guardian",
                        EntityType.Builder.of(TowerGuardianEntity::new, MobCategory.MONSTER)
                                .setTrackingRange(80)
                                .setShouldReceiveVelocityUpdates(true)
                                .sized(1, 2)
                )
                .spawnEgg(0xFF00FF00, 0xFF000000, CreativeModeTabs.SPAWN_EGGS)
                .attributes(() -> TowerGuardianEntity.createAttributes().build());

        REGISTER.registerMob("debug_beacons",
                        EntityType.Builder.of(DebugBeaconsEntity::new, MobCategory.MONSTER)
                                .setTrackingRange(80)
                                .setShouldReceiveVelocityUpdates(true)
                                .sized(1, 2)
                )
                .spawnEgg(0xFF00FF00, 0xFF000000, CreativeModeTabs.SPAWN_EGGS)
                .attributes(() -> DebugBeaconsEntity.createAttributes().build());
    }
}
