package ru.timeconqueror.timecore.common.capability.listener;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityAttacher;
import ru.timeconqueror.timecore.common.capability.owner.attach.CoffeeCapabilityProvider;
import ru.timeconqueror.timecore.common.capability.owner.attach.getter.CoffeeCapabilityGetter;

import java.util.ArrayList;

@Mod.EventBusSubscriber
public class DefaultAttachCapabilityHandler {

    @SubscribeEvent
    public static void onTileAttachCapability(AttachCapabilitiesEvent<TileEntity> event) {
        attachCaps(event, CapabilityOwner.TILE_ENTITY);
    }

    @SubscribeEvent
    public static void onEntityAttachCapability(AttachCapabilitiesEvent<Entity> event) {
        attachCaps(event, CapabilityOwner.ENTITY);
    }

    // TODO: Village capability
//    @SubscribeEvent
//    public void onVillageAttachCapability(AttachCapabilitiesEvent<VillagePieces.Village> event) {
//        tryAttachCapability(event, CapabilityOwner.VILLAGE);
//    }

    @SubscribeEvent
    public static void onWorldAttachCapability(AttachCapabilitiesEvent<World> event) {
        attachCaps(event, CapabilityOwner.WORLD);
    }

    @SubscribeEvent
    public static void onChunkAttachCapability(AttachCapabilitiesEvent<Chunk> event) {
        attachCaps(event, CapabilityOwner.CHUNK);
    }

    @SubscribeEvent
    public static void onItemStackAttachCapability(AttachCapabilitiesEvent<ItemStack> event) {
        attachCaps(event, CapabilityOwner.ITEM_STACK);
    }

    private static <T extends ICapabilityProvider> void attachCaps(AttachCapabilitiesEvent<T> event, CapabilityOwner<T> owner) {
        CoffeeCapabilityProvider<?> provider = new CoffeeCapabilityProvider<>(event.getObject());
        ArrayList<CoffeeCapabilityAttacher<T, ?>> attachers = owner.getAttachers();

        boolean attached = false;
        if (attachers != null) {
            for (CoffeeCapabilityAttacher<T, ?> attacher : attachers) {
                if (attacher.getPredicate().test(event.getObject())) {
                    provider.addCapability(attacher.getCapability(), (CoffeeCapabilityGetter) attacher.getGetterFactory().get());
                    attached = true;
                }
            }
        }

        if (attached) {
            event.addCapability(TimeCore.rl("capability-provider"), provider);
        }
    }
}
