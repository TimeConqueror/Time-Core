package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.util.MathUtils;

public class ClientAnimationManager extends BaseAnimationManager {
    @Override
    protected void applyAnimation(ITimeModel model, Layer layer, AnimationWatcher watcher, long currentTime) {
        Animation animation = watcher.getAnimation();
        animation.apply(model, layer, MathUtils.coerceInRange(watcher.getExistingTime(currentTime), 0, watcher.getAnimation().getLength()));
    }

    @Override
    public boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }
}
