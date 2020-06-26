package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

public class ClientAnimationManager extends BaseAnimationManager {
    public ClientAnimationManager(@Nullable AnimationStarter walkingAnimationStarter) {
        super(walkingAnimationStarter);
    }

    @Override
    protected void applyAnimation(TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime) {
        IAnimation animation = watcher.getAnimation();
//        System.out.println("model = " + model + ", layer = " + layer + ", watcher = " + watcher + ", currentTime = " + currentTime);
//        System.out.println(watcher);
//        System.out.println(animation);
        animation.apply(model, layer, watcher.getExistingTime(currentTime));
    }

    @Override
    public boolean isGamePaused() {
        return Minecraft.getInstance().isGamePaused();
    }
}
