package ru.timeconqueror.timecore.client.render.animation;

import net.minecraft.entity.LivingEntity;
import ru.timeconqueror.timecore.api.client.render.InsertType;
import ru.timeconqueror.timecore.api.client.render.TimeEntityModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimationManager {
    private List<AnimationWatcher> animations = new ArrayList<>();

    public void startAnimation(Animation animation, InsertType insertType) {
        if (insertType == InsertType.CLEAR) {
            animations.clear();
        } else {
            for (Iterator<AnimationWatcher> iterator = animations.iterator(); iterator.hasNext(); ) {
                AnimationWatcher animationWatcher = iterator.next();
                Animation loopAnimation = animationWatcher.getAnimation();
                if (loopAnimation == animation) {
                    if (insertType == InsertType.IGNORE) {
                        return;
                    } else if (insertType == InsertType.OVERWRITE) {
                        iterator.remove();
                    }
                }
            }
        }

        animations.add(new AnimationWatcher(animation));
    }

    public void removeAnimation(Animation animation) {
        animations.removeIf(animationWatcher -> animationWatcher.getAnimation() == animation);
    }

    public <T extends LivingEntity> void processAnimations(TimeEntityModel<T> model) {
        for (Iterator<AnimationWatcher> iterator = animations.iterator(); iterator.hasNext(); ) {
            AnimationWatcher watcher = iterator.next();
            Animation animation = watcher.getAnimation();

            if (watcher.isAnimationEnded()) {
                if (watcher.getAnimation().isLooped()) {
                    watcher.resetTimer();
                } else {
                    iterator.remove();
                    continue;
                }
            }

            animation.apply(model, watcher.getExistingTime());
        }
    }
}
