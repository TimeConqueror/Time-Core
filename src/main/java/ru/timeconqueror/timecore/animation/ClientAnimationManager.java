package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

public class ClientAnimationManager extends BaseAnimationManager {
    @Override
    protected void applyAnimation(ITimeModel model, Layer layer, AnimationWatcher watcher, long currentTime) {
        Animation animation = watcher.getAnimation();
        animation.apply(model, layer, watcher.getCurrentAnimationTime(currentTime));
    }

    @Override
    public boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }
}
