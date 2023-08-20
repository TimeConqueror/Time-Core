package ru.timeconqueror.timecore.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.animation.watcher.FreezableTime;
import ru.timeconqueror.timecore.animation.watcher.TransitionWatcher;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.IAnimationWatcherInfo;
import ru.timeconqueror.timecore.api.animation.ILayer;
import ru.timeconqueror.timecore.api.animation.builders.LayerDefinition;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.molang.MolangFillers;

public class Layer implements ILayer {
    private final BaseAnimationManager manager;
    private final String name;

    @Nullable
    private AnimationWatcher animationWatcher;
    private BlendType blendType;
    private float weight;

    @Getter
    private final MolangEnvironment environment;

    public Layer(BaseAnimationManager manager, LayerDefinition layerDefinition) {
        this.manager = manager;
        this.name = layerDefinition.name();
        this.blendType = layerDefinition.blendType();
        this.weight = layerDefinition.weight();

        environment = createMolangEnvironment();
    }

    private MolangEnvironment createMolangEnvironment() {
        MolangRuntime.Builder builder = new MolangRuntime.Builder();
        manager.getMolangSharedObjects().forEach(builder::loadLibrary);

        MolangFillers.addAnimationBasedQueries(this, builder.getQuery());

        return builder.create();
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
                watcher.freeze(FreezableTime.FreezeCause.BY_ESC);
            } else {
                watcher.unfreeze(FreezableTime.FreezeCause.BY_ESC);

                if (watcher.requiresInit()) {
                    watcher.init(this, model);
                }

                if (watcher.isAnimationEnded(currentTime)) {
                    if (watcher.getNextAnimation() == null) {
                        if (watcher.getLoopMode() == LoopMode.LOOP) {
                            watcher.resetTimer();
                            manager.onLoopedAnimationRestart(watcher);
                            return;
                        } else if (watcher.getLoopMode() == LoopMode.HOLD_ON_LAST_FRAME) {
                            return;
                        }
                    }

                    manager.onAnimationEnd(model, this, watcher);

                    watcher = watcher.next();

                    if (watcher != null && watcher.requiresInit()) {
                        watcher.init(this, model);
                    }

                    setAnimationWatcher(watcher);//here we update current watcher
                }
            }
        }
    }

    public void apply(ITimeModel model, long currentTime) {
        AnimationWatcher watcher = getAnimationWatcher();
        if (watcher != null) {
            Animation animation = watcher.getAnimation();
            animation.apply(model, this, environment, watcher.getAnimationTime(currentTime));
        }
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
}