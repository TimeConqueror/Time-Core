package examples.capability_example.player;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class CapabilityTest {

    @SubscribeEvent
    public static void onTick(TickEvent.PlayerTickEvent event) {
        if (event.side.isServer() && event.phase == TickEvent.Phase.END) {
            MyPlayerCapability cap = MyPlayerCapability.of(event.player);

            if (cap != null) {
                cap.ticks.set(cap.ticks.get() + 1);
                // System.out.println("Cap Ticks: " + cap.ticks.get());
            }
        }
    }
}
