package ru.timeconqueror.timecore.animation.util;

import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;

public interface WatcherSerializer<T extends AnimationWatcher> {
    void serialize(T watcher, FriendlyByteBuf buffer);

    T deserialize(FriendlyByteBuf buffer);
}