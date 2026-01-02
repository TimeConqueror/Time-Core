//package ru.timeconqueror.timecore.common.capability.listener;
//
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.block.entity.BlockEntity;
//import net.minecraft.world.level.chunk.LevelChunk;
//import net.minecraftforge.common.capabilities.ICapabilityProvider;
//import net.minecraftforge.event.AttachCapabilitiesEvent;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import ru.timeconqueror.timecore.TimeCore;
//import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwnerType;
//import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityAttacher;
//import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityProvider;
//import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CapabilityProviderAdapter;
//
//import java.util.ArrayList;
//
//@Mod.EventBusSubscriber
//public class CapabilityEventHandler {
//
//    @SubscribeEvent
//    public static void onTileAttachCapability(AttachCapabilitiesEvent<BlockEntity> event) {
//        attachCaps(event, CapabilityOwnerType.BLOCK_ENTITY);
//    }
//
//    @SubscribeEvent
//    public static void onEntityAttachCapability(AttachCapabilitiesEvent<Entity> event) {
//        attachCaps(event, CapabilityOwnerType.ENTITY);
//    }
//
//    @SubscribeEvent
//    public static void onWorldAttachCapability(AttachCapabilitiesEvent<Level> event) {
//        attachCaps(event, CapabilityOwnerType.LEVEL);
//    }
//
//    @SubscribeEvent
//    public static void onChunkAttachCapability(AttachCapabilitiesEvent<LevelChunk> event) {
//        attachCaps(event, CapabilityOwnerType.CHUNK);
//    }
//
//    @SubscribeEvent
//    public static void onItemStackAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
//        attachCaps(event, CapabilityOwnerType.ITEM_STACK);
//    }
//
//    private static <T extends ICapabilityProvider> void attachCaps(AttachCapabilitiesEvent<T> event, CapabilityOwnerType<T> owner) {
//        T object = event.getObject();
//        CoffeeCapabilityProvider<T> provider = new CoffeeCapabilityProvider<>(object);
//        ArrayList<CoffeeCapabilityAttacher<T, ?>> attachers = owner.getAttachers();
//
//        boolean attached = false;
//        if (attachers != null) {
//            for (CoffeeCapabilityAttacher<T, ?> attacher : attachers) {
//                if (attacher.predicate().test(object)) {
//                    provider.addCapability(attacher.capability(), (CapabilityProviderAdapter) attacher.getterFactory().get());
//                    attached = true;
//                }
//            }
//        }
//
//        if (attached) {
//            event.addCapability(TimeCore.rl("capability-provider"), provider);
//        }
//    }
//}
