package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.S2CEndAnimationMsg;
import ru.timeconqueror.timecore.mod.common.packet.S2CStartAnimationMsg;
import ru.timeconqueror.timecore.mod.common.packet.animation.CodecSupplier;

public abstract class NetworkDispatcher<T extends AnimatedObject<T>> {
    public static <T extends Entity & AnimatedObject<T>> NetworkDispatcher<T> forEntity() {
        return new NetworkDispatcher<T>() {
            @Override
            protected PacketDistributor.PacketTarget getPacketTarget(T boundObject) {
                return PacketDistributor.TRACKING_ENTITY.with(() -> boundObject);
            }

            @Override
            protected CodecSupplier getCodec(T boundObject) {
                return new CodecSupplier.EntityCodecSupplier(boundObject);
            }
        };
    }

    public static <T extends TileEntity & AnimatedObject<T>> NetworkDispatcher<T> forTileEntity() {
        return new NetworkDispatcher<T>() {
            @Override
            protected PacketDistributor.PacketTarget getPacketTarget(T boundObject) {
                return PacketDistributor.TRACKING_CHUNK.with(() -> {
                    World world = boundObject.getWorld();
                    return world.getChunkAt(boundObject.getPos());
                });
            }

            @Override
            protected CodecSupplier getCodec(T boundObject) {
                return new CodecSupplier.TileEntityCodecSupplier(boundObject);
            }
        };
    }

    protected abstract PacketDistributor.PacketTarget getPacketTarget(T boundObject);

    protected abstract CodecSupplier getCodec(T boundObject);

    public void sendSetAnimationPacket(ActionManagerImpl<T> actionManager, AnimationStarter.AnimationData data, Layer layer) {
        T boundObject = actionManager.getBoundObject();
        InternalPacketManager.INSTANCE.send(getPacketTarget(boundObject), new S2CStartAnimationMsg(getCodec(boundObject), layer.getName(), data));
    }

    public void sendRemoveAnimationPacket(ActionManagerImpl<T> actionManager, String layerName, int transitionTime) {
        T boundObject = actionManager.getBoundObject();
        InternalPacketManager.INSTANCE.send(getPacketTarget(boundObject), new S2CEndAnimationMsg(getCodec(boundObject), layerName, transitionTime));
    }
}