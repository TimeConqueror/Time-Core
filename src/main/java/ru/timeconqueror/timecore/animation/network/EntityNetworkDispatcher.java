package ru.timeconqueror.timecore.animation.network;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.common.registry.LevelObjectCodecs;

public class EntityNetworkDispatcher<T extends Entity & AnimatedObject<T>> extends NetworkDispatcher<T> {
    @Override
    public PacketDistributor.PacketTarget getPacketTarget(T boundObject) {
        return PacketDistributor.TRACKING_ENTITY.with(() -> boundObject);
    }

    @Override
    public LevelObjectCodec<Entity> getCodec(T boundObject) {
        return LevelObjectCodecs.ENTITY.create(boundObject);
    }
}
