package ru.timeconqueror.timecore.capability_example.player;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.common.capability.CoffeeCapability;
import ru.timeconqueror.timecore.common.capability.owner.CapabilityOwner;
import ru.timeconqueror.timecore.common.capability.owner.serializer.CapabilityOwnerSerializer;
import ru.timeconqueror.timecore.common.capability.property.CoffeeProperty;
import ru.timeconqueror.timecore.common.capability.property.serializer.IntPropertySerializer;

public class MyPlayerCapability extends CoffeeCapability<Entity> {
    public final CoffeeProperty<Integer> ticks = prop("ticks", 0, IntPropertySerializer.INSTANCE).synced();

    private final PlayerEntity player;

    public MyPlayerCapability(PlayerEntity player) {
        this.player = player;
    }

    @NotNull
    @Override
    public CapabilityOwnerSerializer<Entity> getOwnerSerializer() {
        return CapabilityOwner.ENTITY.getSerializer();
    }

    @NotNull
    @Override
    public Capability<? extends CoffeeCapability<Entity>> getCapability() {
        return CEPCaps.MY_CAPABILITY;
    }

    @Override
    public void sendChangesToClients(@NotNull SimpleChannel channel, @NotNull Object data) {
        if (player instanceof ServerPlayerEntity) {
            channel.send(PacketDistributor.PLAYER.with(() -> ((ServerPlayerEntity) player)), data);
        }
    }

    public void detectAndSendChanges() {
        detectAndSendChanges(player.level, player);
    }

    public void sendAllData() {
        sendAllData(player.level, player);
    }

    @Nullable
    public static MyPlayerCapability of(PlayerEntity player) {
        LazyOptional<MyPlayerCapability> cap = player.getCapability(CEPCaps.MY_CAPABILITY);
        if (cap.isPresent()) {
            return cap.orElseThrow(IllegalStateException::new);
        }

        return null;
    }
}
