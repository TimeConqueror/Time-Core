package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

public class ClientAnimationManager extends BaseAnimationManager {
    @Override
    protected void applyAnimation(TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime) {
        IAnimation animation = watcher.getAnimation();
        animation.apply(model, layer, watcher.getExistingTime(currentTime));
    }

    @Override
    public boolean isGamePaused() {
        return Minecraft.getInstance().isGamePaused();
    }
}
