package ru.timeconqueror.timecore.animation.util;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.timecore.animation.watcher.AnimationTicker;

public interface AnimationTickerSerializer<T extends AnimationTicker> {
    void serialize(T ticker, FriendlyByteBuf buffer);

    T deserialize(FriendlyByteBuf buffer);
}