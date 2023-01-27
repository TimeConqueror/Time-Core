//package ru.timeconqueror.timecore.internal.common.packet;
// FIXME port?
//import net.minecraft.network.FriendlyByteBuf;
//import net.minecraftforge.network.NetworkEvent;
//import org.jetbrains.annotations.NotNull;
//import ru.timeconqueror.timecore.TimeCore;
//import ru.timeconqueror.timecore.api.common.packet.SimplePacketHandler;
//import ru.timeconqueror.timecore.internal.devtools.StructureRevealer;
//
//import java.util.Optional;
//
//public class S2CSRClearPiecesPacket {
//    public static class Handler extends SimplePacketHandler<S2CSRClearPiecesPacket> {
//        @Override
//        public void encode(S2CSRClearPiecesPacket packet, FriendlyByteBuf buffer) {
//
//        }
//
//        @NotNull
//        @Override
//        public S2CSRClearPiecesPacket decode(FriendlyByteBuf buffer) {
//            return new S2CSRClearPiecesPacket();
//        }
//
//        @Override
//        public void handleOnMainThread(S2CSRClearPiecesPacket packet, NetworkEvent.Context ctx) {
//            Optional<StructureRevealer> instance = StructureRevealer.getInstance();
//            if (instance.isPresent()) {
//                instance.get().structureRenderer.getTrackedStructurePieces().clear();
//            } else {
//                TimeCore.LOGGER.warn("Server has sent you a structure revealing packet, but structure revealer is turned off on client!");
//            }
//        }
//    }
//}
