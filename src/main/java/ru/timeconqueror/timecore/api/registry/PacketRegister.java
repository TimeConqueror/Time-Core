package ru.timeconqueror.timecore.api.registry;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import ru.timeconqueror.timecore.api.TimeCoreAPI;
import ru.timeconqueror.timecore.api.common.packet.ITimePacketHandler;
import ru.timeconqueror.timecore.api.registry.base.RunnableStoringRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.internal.common.packet.InternalPacketManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * All {@link TimeRegister}s are used to simplify stuff registering.
 * <p>
 * To use it you need to:
 * <ol>
 *     <li>Create its instance and declare it static. Access modifier can be any.</li>
 *     <li>Attach {@link AutoRegistrable} annotation to it to register it as an event listener.</li>
 *     <li>Call {@link TimeCoreAPI#setup(Object)} from your mod constructor to enable TimeCore's annotations.</li>
 * </ol>
 *
 * <b>Features:</b>
 * If you need to register stuff, your first step will be to call method #register.
 * If the register system has any extra available registering stuff, then this method will return Register Chain,
 * which will have extra methods to apply.
 * <br>
 * <br>
 * <b>Field-only style:</b>
 * <br>
 * <blockquote>
 *     <pre>
 *      // it should be higher than the channel, which we want to create, because, otherwise you'll face NullPointerException
 *     {@literal @}AutoRegistrable
 *      private static final PacketRegister REGISTER = new PacketRegister(TimeCore.MODID);
 *
 *      private static final String PROTOCOL_STRING = "1";
 *
 *      public static final SimpleChannel INSTANCE = REGISTER.createChannel("main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals)
 *              .regPacket(S2CSRSendSinglePieceMsg.class, new S2CSRSendSinglePieceMsg.Handler())
 *              .regPacket(S2CSRClearPiecesMsg.class, new S2CSRClearPiecesMsg.Handler())
 *              .regPacket(S2CStartAnimationMsg.class, new S2CStartAnimationMsg.Handler())
 *              .regPacket(S2CEndAnimationMsg.class, new S2CEndAnimationMsg.Handler())
 *              .regPacket(S2CSyncAnimationsMsg.class, new S2CSyncAnimationsMsg.Handler())
 *              .asChannel();
 *     </pre>
 * </blockquote>
 * <br>
 * <b>Common style:</b>
 * <br>
 * We still add {@link TimeRegister} field to the class as stated above. (with AutoRegistrable annotation, etc.)]
 * <p>
 * One more thing: we should add is a <b>static</b> register method and annotate with {@link AutoRegistrable.Init}. Method can have any access modifier.
 * There we will register all needed stuff, using {@link TimeRegister} field.
 * Method annotated with {@link AutoRegistrable.Init} can have zero parameters or one {@link FMLConstructModEvent} parameter.
 * It will be called before Registry events to prepare all the stuff.
 *
 * <br>
 * <blockquote>
 *     <pre>
 *      public class PacketRegistryExample {
 *          private static final String PROTOCOL_STRING = "1";
 *          public static final SimpleChannel INSTANCE = PacketRegister.createChannel(TimeCore.MODID, "main", () -> PROTOCOL_STRING, PROTOCOL_STRING::equals, PROTOCOL_STRING::equals);
 *
 *         {@literal @}AutoRegistrable
 *          private static final PacketRegister REGISTER = new PacketRegister(TimeCore.MODID);
 *
 *         {@literal @}AutoRegistrable.InitMethod
 *           private static void register() {
 *              REGISTER.regPacket(INSTANCE, S2CSRSendSinglePieceMsg.class, new S2CSRSendSinglePieceMsg.Handler());
 *              REGISTER.regPacket(INSTANCE, S2CSRClearPiecesMsg.class, new S2CSRClearPiecesMsg.Handler());
 *          }
 *      }
 *     </pre>
 * </blockquote>
 * <p>
 * <p>
 * Examples can be seen at {@link InternalPacketManager}
 */
public class PacketRegister extends RunnableStoringRegister {
    private HashMap<SimpleChannel, Integer> lastIndexes = new HashMap<>();

    public PacketRegister(String modid) {
        super(modid);
    }

    /**
     * Creates a new {@link SimpleChannel}.
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
    public static SimpleChannel createChannel(String modid, String name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        return NetworkRegistry.newSimpleChannel(new ResourceLocation(modid, name), networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
    }

    /**
     * Creates a new {@link SimpleChannel}.
     *
     * @param name                   The location for this channel. Must be unique.
     *                               It will be used as a part of registry key. Should NOT contain mod ID, because it will be bound automatically.
     * @param networkProtocolVersion The network protocol version string that will be offered to the remote side {@link NetworkRegistry.ChannelBuilder#networkProtocolVersion(Supplier)}
     * @param clientAcceptedVersions Called on the client with the networkProtocolVersion string from the server {@link NetworkRegistry.ChannelBuilder#clientAcceptedVersions(Predicate)}
     * @param serverAcceptedVersions Called on the server with the networkProtocolVersion string from the client {@link NetworkRegistry.ChannelBuilder#serverAcceptedVersions(Predicate)}
     * @return {@link PacketRegisterChain} to register packets for this channels
     * @see NetworkRegistry.ChannelBuilder#newSimpleChannel(ResourceLocation, Supplier, Predicate, Predicate)
     */
    public PacketRegisterChain createChannel(String name, Supplier<String> networkProtocolVersion, Predicate<String> clientAcceptedVersions, Predicate<String> serverAcceptedVersions) {
        SimpleChannel channel = createChannel(getModId(), name, networkProtocolVersion, clientAcceptedVersions, serverAcceptedVersions);
        return new PacketRegisterChain(channel);
    }

    /**
     * Registers new packet.
     * All packets will be handled on main thread.
     */
    public <T> void regPacket(SimpleChannel channel, Class<T> packetClass, ITimePacketHandler<T> packetHandler, NetworkDirection direction) {
        add(() -> channel.messageBuilder(packetClass, getAndIncreaseIndex(channel), direction)
                .encoder((msg, buffer) -> {
                    try {
                        packetHandler.encode(msg, buffer);
                    } catch (IOException e) {
                        throw new RuntimeException("Can't encode packet: " + e.getMessage(), e);
                    }
                })
                .decoder(buffer -> {
                    try {
                        return packetHandler.decode(buffer);
                    } catch (IOException e) {
                        throw new RuntimeException("Can't decode packet: " + e.getMessage(), e);
                    }
                })
                .consumerMainThread((msg, contextSupplier) -> {
                    NetworkEvent.Context ctx = contextSupplier.get();
                    packetHandler.handle(msg, ctx);
                })
                .add());
    }

    @Override
    public void regToBus(IEventBus modEventBus) {
        super.regToBus(modEventBus);
        modEventBus.addListener(this::onInit);
    }

    private void onInit(FMLCommonSetupEvent event) {
        catchErrors(event, this::runAll);

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

    public class PacketRegisterChain {
        private final SimpleChannel channel;

        private PacketRegisterChain(SimpleChannel channel) {
            this.channel = channel;
        }

        /**
         * Registers the packet to the bound channel.
         */
        public <T> PacketRegisterChain regPacket(Class<T> packetClass, ITimePacketHandler<T> packetHandler, NetworkDirection direction) {
            PacketRegister.this.regPacket(channel, packetClass, packetHandler, direction);
            return this;
        }

        public SimpleChannel asChannel() {
            return channel;
        }
    }
}
