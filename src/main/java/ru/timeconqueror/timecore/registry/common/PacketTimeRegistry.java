package ru.timeconqueror.timecore.registry.common;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.registry.TimeAutoRegistrable;
import ru.timeconqueror.timecore.registry.common.base.TimeRegistry;

import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Used for simplifying packet adding. You need to extend it and do your stuff in {@link #register()} method<br>
 * <p>
 * Any your registry that extends it should be annotated by {@link TimeAutoRegistrable} with {@link TimeAutoRegistrable.Target#INSTANCE} target
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 * Example can be seen here: {@link InternalPacketManager}
 */
public abstract class PacketTimeRegistry extends TimeRegistry {
    private static HashMap<SimpleChannel, Integer> lastIndexes = new HashMap<>();

    /**
     * Create a new {@link SimpleChannel}.
     *
     * @param name                   The location for this channel. Must be unique.
     *                               It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     * @param networkProtocolVersion The network protocol version string that will be offered to the remote side {@link NetworkRegistry.ChannelBuilder#networkProtocolVersion(Supplier)}
     * @param clientAcceptedVersions Called on the client with the networkProtocolVersion string from the server {@link NetworkRegistry.ChannelBuilder#clientAcceptedVersions(Predicate)}
     * @param serverAcceptedVersions Called on the server with the networkProtocolVersion string from the client {@link NetworkRegistry.ChannelBuilder#serverAcceptedVersions(Predicate)}
     * @return A new {@link SimpleChannel}
     * @see NetworkRegistry.ChannelBuilder#newSimpleChannel(ResourceLocation, Supplier, Predicate, Predicate)
     */
    public static SimpleChannel newChannel(String name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(ModLoadingContext.get().getActiveNamespace(), name), networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
    }

    private static int getAndIncreaseIndex(SimpleChannel channel) {
        if (lastIndexes.get(channel) == null) {
            lastIndexes.put(channel, 1);
            return 0;
        } else {
            int lastIndex = lastIndexes.get(channel);
            lastIndexes.put(channel, lastIndex + 1);
            return lastIndex;
        }
    }

    @SubscribeEvent
    public final void onInit(FMLCommonSetupEvent event) {
        register();

        lastIndexes.clear();
        lastIndexes = null;
    }

    /**
     * Registers new packet.
     * <p>
     * Should be called in {@link #register()} method.
     */
    public <T extends ITimePacket> void regPacket(SimpleChannel channel, Class<T> packetClass, ITimePacket.ITimePacketHandler<T> packetHandler) {
        channel.messageBuilder(packetClass, getAndIncreaseIndex(channel))
                .encoder(packetHandler::encode)
                .decoder(packetHandler::decode)
                .consumer(packetHandler::handle)
                .add();
    }
}
