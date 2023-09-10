package ru.timeconqueror.timecore.animation.util;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.timecore.animation.watcher.AbstractAnimationTicker;

public interface AnimationTickerSerializer<T extends AbstractAnimationTicker> {
    void serialize(T ticker, FriendlyByteBuf buffer);

    T deserialize(FriendlyByteBuf buffer);
}