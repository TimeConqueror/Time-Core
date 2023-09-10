package ru.timeconqueror.timecore.animation.network;

import net.minecraftforge.network.PacketDistributor;
import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

public abstract class NetworkDispatcher<T extends AnimatedObject<T>> {

    public abstract PacketDistributor.PacketTarget getPacketTarget(T boundObject);

    public abstract LevelObjectCodec<?> getCodec(T boundObject);
}