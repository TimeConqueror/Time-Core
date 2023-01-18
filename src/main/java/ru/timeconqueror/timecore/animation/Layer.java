package ru.timeconqueror.timecore.animation;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.animation.watcher.TransitionWatcher;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.MathUtils;

//TODO 1.18 make LayerDefinition instead of using Layer in Builder
public class Layer implements AnimationLayer {
    private BaseAnimationManager manager;
    private final String name;

    @Nullable
    private AnimationWatcher animationWatcher;
    private BlendType blendType;
    private float weight;

    public Layer(String name, BlendType blendType, float weight) {
        this.name = name;
        this.weight = MathUtils.coerceInRange(weight, 0, 1);
        this.blendType = blendType;
    }

    @Override
    public float getWeight() {
        return weight;
    }

    @Override
    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public BlendType getBlendType() {
        return blendType;
    }

    @Override
    public void setBlendType(BlendType type) {
        blendType = type;
    }

    void setAnimation(AnimationStarter.AnimationData data) {
        if (animationWatcher != null) {
            manager.onAnimationStop(animationWatcher);
        }

        if (data.getTransitionTime() == 0) {
            animationWatcher = new AnimationWatcher(data);
        } else {
            if (animationWatcher == null) {
                animationWatcher = TransitionWatcher.fromNullSource(data);
            } else {
                animationWatcher = TransitionWatcher.from(animationWatcher, data);
            }
        }

        manager.onAnimationStart(this, data, animationWatcher);
    }

    void removeAnimation(int transitionTime) {
        if (animationWatcher != null) {
            manager.onAnimationEnd(null, this, animationWatcher);

            if (transitionTime == 0) {
                animationWatcher = null;
            } else {
                if (!(animationWatcher instanceof TransitionWatcher && ((TransitionWatcher) animationWatcher).getDestination() == Animation.NULL)) {
                    animationWatcher = TransitionWatcher.toNullDestination(animationWatcher, transitionTime);
                }
            }
        }
    }

    void update(BaseAnimationManager manager, ITimeModel model, long currentTime) {
//        boolean paused = manager.isGamePaused() || frozen;
        boolean paused = manager.isGamePaused();

        AnimationWatcher watcher = getAnimationWatcher();

        if (watcher != null) {
            if (paused) {
                watcher.freeze();
            } else {
                watcher.unfreeze();

                if (watcher.requiresInit()) {
                    watcher.init(model);
                }

                if (watcher.isAnimationEnded(currentTime)) {
                    Animation animation = watcher.getAnimation();

                    if (watcher.getNextAnimationData() == null) {
                        if (animation.getLoopMode() == LoopMode.LOOP) {
                            watcher.resetTimer();
                            manager.onLoopedAnimationRestart(watcher);
                            return;
                        } else if (animation.getLoopMode() == LoopMode.HOLD_ON_LAST_FRAME) {
                            return;
                        }
                    }

                    manager.onAnimationEnd(model, this, watcher);

                    watcher = watcher.next();

                    if (watcher != null && watcher.requiresInit()) {
                        watcher.init(model);
                    }

                    setAnimationWatcher(watcher);//here we update current watcher
                }
            }
        }
    }

    //TODO 1.18+ move to constructor
    void setManager(BaseAnimationManager manager) {
        this.manager = manager;
    }

    @Nullable
    public AnimationWatcher getAnimationWatcher() {
        return animationWatcher;
    }

    public void setAnimationWatcher(@Nullable AnimationWatcher animationWatcher) {
        this.animationWatcher = animationWatcher;
    }

    @Override
    public IAnimationWatcherInfo getWatcherInfo() {
        return animationWatcher != null ? animationWatcher : IAnimationWatcherInfo.EMPTY;
    }

    @Override
    public String getName() {
        return name;
    }

    public Layer copySettings() {
        return new Layer(name, blendType, weight);
    }
}