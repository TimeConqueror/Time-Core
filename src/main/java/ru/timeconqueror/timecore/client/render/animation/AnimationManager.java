package ru.timeconqueror.timecore.client.render.animation;

import net.minecraft.entity.LivingEntity;
import ru.timeconqueror.timecore.api.client.render.AnimationInsertType;
import ru.timeconqueror.timecore.api.client.render.TimeEntityModel;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AnimationManager {
    private List<AnimationWatcher> animations = new ArrayList<>();

    public void startAnimation(Animation animation, AnimationInsertType insertType) {
        if (insertType == AnimationInsertType.CLEAR) {
            animations.clear();
        } else {
            for (Iterator<AnimationWatcher> iterator = animations.iterator(); iterator.hasNext(); ) {
                AnimationWatcher animationWatcher = iterator.next();
                Animation loopAnimation = animationWatcher.getAnimation();
                if (loopAnimation == animation) {
                    if (insertType == AnimationInsertType.IGNORE) {
                        return;
                    } else if (insertType == AnimationInsertType.OVERWRITE) {
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
            if (watcher.isAnimationEnded()) {
                iterator.remove();
                continue;
            }

            Animation animation = watcher.getAnimation();
            animation.apply(model, watcher.getExistingTime());
        }
    }
}
