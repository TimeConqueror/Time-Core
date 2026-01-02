package ru.timeconqueror.timecore.common.packet;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.neoforged.fml.LogicalSide;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.util.SimpleName;
import ru.timeconqueror.timecore.api.util.client.ClientProxy;

import java.util.function.BiConsumer;
import java.util.function.Function;

public class PayloadHelper {
    @NotNull
    public static Level getLevel(IPayloadContext ctx) {
        return ctx.flow().getReceptionSide() == LogicalSide.CLIENT ? ClientProxy.level() : ctx.player().level();
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> makeType(String modId, String id) {
        return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(modId, id));
    }

    public static <T extends CustomPacketPayload> CustomPacketPayload.Type<T> makeType(String modId, Class<T> clazz) {
        return new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(modId, SimpleName.of(clazz)));
    }

    public static <BUF, PACKET> StreamCodec<BUF, PACKET> simpleStreamCodec(BiConsumer<PACKET, BUF> encoder, Function<BUF, PACKET> decoder) {
        return new StreamCodec<>() {
            @Override
            public PACKET decode(BUF buffer) {
                return decoder.apply(buffer);
            }

            @Override
            public void encode(BUF buffer, PACKET value) {
                encoder.accept(value, buffer);
            }
        };
    }
}
