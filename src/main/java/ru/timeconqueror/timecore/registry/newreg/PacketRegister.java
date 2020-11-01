package ru.timeconqueror.timecore.registry.newreg;

import com.google.common.base.Preconditions;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class PacketRegister extends TimeRegister {
    private HashMap<SimpleChannel, Integer> lastIndexes = new HashMap<>();
    private List<Runnable> runnables = new ArrayList<>();

    public PacketRegister(String modid) {
        super(modid);
    }

    /**
     * Create a new {@link SimpleChannel}.
     *
     * @param modid                  your mod id
     * @param name                   The location for this channel. Must be unique.
     *                               It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     * @param networkProtocolVersion The network protocol version string that will be offered to the remote side {@link NetworkRegistry.ChannelBuilder#networkProtocolVersion(Supplier)}
     * @param clientAcceptedVersions Called on the client with the networkProtocolVersion string from the server {@link NetworkRegistry.ChannelBuilder#clientAcceptedVersions(Predicate)}
     * @param serverAcceptedVersions Called on the server with the networkProtocolVersion string from the client {@link NetworkRegistry.ChannelBuilder#serverAcceptedVersions(Predicate)}
     * @return A new {@link SimpleChannel}
     * @see NetworkRegistry.ChannelBuilder#newSimpleChannel(ResourceLocation, Supplier, Predicate, Predicate)
     */
    public static SimpleChannel newChannel(String modid, String name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(modid, name), networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
    }

    /**
     * Registers new packet.
     */
    public <T extends ITimePacket> void regPacket(SimpleChannel channel, Class<T> packetClass, ITimePacket.ITimePacketHandler<T> packetHandler) {
        Preconditions.checkNotNull(runnables, "You attempted to call this method after FMLCommonSetupEvent has been fired.");

        runnables.add(() -> channel.messageBuilder(packetClass, getAndIncreaseIndex(channel))
                .encoder(packetHandler::encode)
                .decoder(packetHandler::decode)
                .consumer(packetHandler::handle)
                .add());
    }

    @Override
    public void regToBus(IEventBus bus) {
        bus.addListener(this::onInit);
    }

    private void onInit(FMLCommonSetupEvent event) {
        withErrorCatching("common setup event", () -> runnables.forEach(Runnable::run));

        runnables = null;
        lastIndexes = null;
    }

    private int getAndIncreaseIndex(SimpleChannel channel) {
        if (lastIndexes.get(channel) == null) {
            lastIndexes.put(channel, 1);
            return 0;
        } else {
            int lastIndex = lastIndexes.get(channel);
            lastIndexes.put(channel, lastIndex + 1);
            return lastIndex;
        }
    }
}
