package ru.timeconqueror.timecore.api.client.render.animation;

import org.jetbrains.annotations.NotNull;

public interface IAnimationProvider {
    @NotNull
    IAnimationManager getAnimationManager();
}
