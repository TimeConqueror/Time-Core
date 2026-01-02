package ru.timeconqueror.timecore.animation.network;

import ru.timeconqueror.timecore.animation.network.codec.LevelObjectCodec;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.internal.common.packet.animation.S2CAnimationPacket;

public abstract class NetworkDispatcher<T extends AnimatedObject<T>> {

    public abstract void send(T boundObject, S2CAnimationPacket packet);

    public abstract LevelObjectCodec<?> getCodec(T boundObject);
}