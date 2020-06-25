//package ru.timeconqueror.timecore.animation.internal;
//
//import net.minecraft.network.PacketBuffer;
//import net.minecraftforge.fml.LogicalSide;
//import net.minecraftforge.fml.network.NetworkEvent;
//import org.jetbrains.annotations.NotNull;
//import ru.timeconqueror.timecore.api.common.packet.ITimePacket;
//
//import java.util.function.Supplier;
//
//public class S2CActionMsg implements ITimePacket {
//    public S2CActionMsg() {
//    }
//
//    @Override
//    public LogicalSide getReceptionSide() {
//        return LogicalSide.CLIENT;
//    }
//
//    public static class Handler implements ITimePacketHandler<S2CActionMsg> {
//        @Override
//        public void encode(S2CActionMsg packet, PacketBuffer buffer) {
//
//        }
//
//        @NotNull
//        @Override
//        public S2CActionMsg decode(PacketBuffer buffer) {
//            return null;
//        }
//
//        @Override
//        public void onPacketReceived(S2CActionMsg packet, Supplier<NetworkEvent.Context> contextSupplier) {
//
//        }
//    }
//}
