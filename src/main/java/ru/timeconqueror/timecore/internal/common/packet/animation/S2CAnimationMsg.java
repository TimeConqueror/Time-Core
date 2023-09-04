package ru.timeconqueror.timecore.internal.common.packet.animation;

import lombok.AllArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.common.packet.ITimePacketHandler;

@AllArgsConstructor
public abstract class S2CAnimationMsg {
    protected final CodecSupplier codecSupplier;

    public abstract static class Handler<T extends S2CAnimationMsg> implements ITimePacketHandler<T> {
        @Override
        public final void encode(T packet, FriendlyByteBuf buffer) {
            packet.codecSupplier.toBuffer(buffer);

            encodeExtra(packet, buffer);
        }

        @Override
        public @NotNull
        final T decode(FriendlyByteBuf buffer) {
            CodecSupplier codecSupplier = CodecSupplier.fromBuffer(buffer);

            return decodeWithExtraData(codecSupplier, buffer);
        }

        public abstract void encodeExtra(T packet, FriendlyByteBuf buffer);

        public abstract T decodeWithExtraData(CodecSupplier codecSupplier, FriendlyByteBuf buffer);

        public abstract void onPacket(T packet, AnimatedObject<?> provider, NetworkEvent.Context ctx);

        @Override
        public void handle(T packet, NetworkEvent.Context ctx) {
            AnimatedObject<?> animatedObject = packet.codecSupplier.construct(getWorld(ctx));

            onPacket(packet, animatedObject, ctx);
        }
    }

}
