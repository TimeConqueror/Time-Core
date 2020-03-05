package ru.timeconqueror.timecore.api.registry;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.api.TimeMod;

import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Used for simplifying packet adding.<br>
 * <p>
 * Any your registry that extends it should be annotated with {@link TimeAutoRegistry}
 * to create its instance automatically and provide register features.<br>
 *
 * <b><font color="yellow">WARNING: Any annotated registry class must contain constructor without params or exception will be thrown.</b><br>
 * Examples can be seen at test module.//TODO add examples :)
 */
public abstract class PacketTimeRegistry extends TimeRegistry {
    private static HashMap<SimpleChannel, Integer> lastIndexes = new HashMap<>();

    public static SimpleChannel newChannel(TimeMod instance, String name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(instance.getModID(), name), networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
    }

//    public <T> void regPacket(SimpleChannel channel, Class<T> packetClass, ITimePacketHandler<T> packetHandler) {
////        channel.registerMessage()
//    }
}
