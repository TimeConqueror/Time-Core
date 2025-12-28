package examples.capability_example.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.CapabilityManagerAPI;
import ru.timeconqueror.timecore.api.registry.CapabilityRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwnerType;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class CEPCaps {
    @AutoRegistrable
    private static final CapabilityRegister REGISTER = new CapabilityRegister(TimeCore.MODID);

    public static final Capability<MyPlayerCapability> MY_CAPABILITY = REGISTER.register(MyPlayerCapability.class);

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> CapabilityManagerAPI.registerDirectionIndependentCoffeeAttacher(CapabilityOwnerType.ENTITY, MY_CAPABILITY, entity -> entity instanceof Player, (owner, entity) -> new MyPlayerCapability(owner, ((Player) entity))));
    }
}
