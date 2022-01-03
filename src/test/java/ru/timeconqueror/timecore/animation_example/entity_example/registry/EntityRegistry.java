//package ru.timeconqueror.timecore.animation_example.entity_example.registry;
//
//import net.minecraft.entity.EntityClassification;
//import net.minecraft.entity.EntityType;
//import net.minecraft.item.ItemGroup;
//import ru.timeconqueror.timecore.TimeCore;
//import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroDirtProjectileEntity;
//import ru.timeconqueror.timecore.animation_example.entity_example.entity.FloroEntity;
//import ru.timeconqueror.timecore.api.registry.EntityRegister;
//import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
// TODO port examples
//public class EntityRegistry {
//    @AutoRegistrable
//    private static final EntityRegister REGISTER = new EntityRegister(TimeCore.MODID);
//
//    public static final EntityType<FloroEntity> FLORO = REGISTER.registerLiving("floro",
//            EntityType.Builder.of(FloroEntity::new, EntityClassification.MONSTER)
//                    .setTrackingRange(80)
//                    .setShouldReceiveVelocityUpdates(true)
//                    .sized(1, 2)
//    )
//            .attributes(() -> FloroEntity.createAttributes().build())
//            .spawnEgg(0xFF00FF00, 0xFF000000, ItemGroup.TAB_MISC)
//            .retrieve();
//    public static final EntityType<FloroDirtProjectileEntity> FLORO_PROJ = REGISTER.register("floro_proj",
//            EntityType.Builder.<FloroDirtProjectileEntity>of(FloroDirtProjectileEntity::new, EntityClassification.MISC)
//                    .setTrackingRange(80)
//                    .setShouldReceiveVelocityUpdates(true)
//                    .sized(0.5F, 0.5F))
//            .retrieve();
//}
