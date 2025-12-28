package examples.capability_example.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.common.capability.CoffeeCapabilityInstance;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwnerType;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;
import ru.timeconqueror.timecore.common.capability.property.serializer.IntPropertySerializer;

public class MyPlayerCapability extends CoffeeCapabilityInstance<Entity> {
    public final CoffeeProperty<Integer> ticks = prop("ticks", 0, IntPropertySerializer.INSTANCE).synced();

    private final Player player;

    public MyPlayerCapability(CapabilityOwnerType<Entity> owner, Player player) {
        super(owner);
        this.player = player;
    }

    @NotNull
    @Override
    public Capability<? extends CoffeeCapabilityInstance<Entity>> getCapability() {
        return CEPCaps.MY_CAPABILITY;
    }

    @Override
    public void sendChangesToClient(@NotNull SimpleChannel channel, @NotNull Object message) {
        if (player instanceof ServerPlayer serverPlayer) {
            channel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), message);
        }
    }

    public void detectAndSendChanges() {
        detectAndSendChanges(player.level(), player);
    }

    public void sendAllData() {
        sendAllData(player.level(), player);
    }

    @Nullable
    public static MyPlayerCapability of(Player player) {
        LazyOptional<MyPlayerCapability> cap = player.getCapability(CEPCaps.MY_CAPABILITY);
        if (cap.isPresent()) {
            return cap.orElseThrow(IllegalStateException::new);
        }

        return null;
    }
}
