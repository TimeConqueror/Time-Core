package ru.timeconqueror.timecore.capability_example.player;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.CapabilityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.api.util.Hacks;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CEPCaps {
    @AutoRegistrable
    private static final CapabilityRegister REGISTER = new CapabilityRegister(TimeCore.MODID);

    @CapabilityInject(MyPlayerCapability.class)
    public static final Capability<MyPlayerCapability> MY_CAPABILITY = Hacks.promise();

    @AutoRegistrable.InitMethod
    private static void register() {
        REGISTER.regCapability(MyPlayerCapability.class);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> TimeCore.INSTANCE.getCapabilityManager().attachStaticCoffeeCapability(CapabilityOwner.ENTITY, MY_CAPABILITY, entity -> entity instanceof PlayerEntity, entity -> new MyPlayerCapability(((PlayerEntity) entity))));
    }
}
