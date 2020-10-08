package ru.timeconqueror.timecore.animation;

import net.minecraft.network.PacketBuffer;
import ru.timeconqueror.timecore.mod.common.packet.animation.CodecSupplier;
import ru.timeconqueror.timecore.mod.common.packet.animation.CodecSupplier.EntityCodecSupplier;
import ru.timeconqueror.timecore.mod.common.packet.animation.CodecSupplier.TileEntityCodecSupplier;

import java.util.function.Function;

public enum EnumAnimatedObjectType {
    TILE_ENTITY(TileEntityCodecSupplier::new, NetworkDispatcher.forTileEntity()),
    ENTITY(EntityCodecSupplier::new, NetworkDispatcher.forEntity());

    private final Function<PacketBuffer, CodecSupplier> codecFactory;
    private final NetworkDispatcher<?> networkDispatcher;

    EnumAnimatedObjectType(Function<PacketBuffer, CodecSupplier> codecFactory, NetworkDispatcher<?> networkDispatcher) {
        this.codecFactory = codecFactory;
        this.networkDispatcher = networkDispatcher;
    }

    public CodecSupplier getCodec(PacketBuffer buffer) {
        return codecFactory.apply(buffer);
    }

    public NetworkDispatcher<?> getNetworkDispatcher() {
        return networkDispatcher;
    }
}
