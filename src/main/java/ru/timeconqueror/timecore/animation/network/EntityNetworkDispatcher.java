package ru.timeconqueror.timecore.animation.network;

import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.common.registry.LevelObjectCodecs;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CAnimationPacket;

public class EntityNetworkDispatcher<T extends Entity & AnimatedObject<T>> extends NetworkDispatcher<T> {

    @Override
    public void send(T boundObject, S2CAnimationPacket packet) {
        PacketDistributor.sendToPlayersTrackingEntity(boundObject, packet);
    }

    @Override
    public LevelObjectCodec<Entity> getCodec(T boundObject) {
        return LevelObjectCodecs.ENTITY.create(boundObject);
    }
}
