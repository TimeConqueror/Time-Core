package ru.timeconqueror.timecore.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangRuntime;
import lombok.Getter;
import lombok.Setter;
import ru.timeconqueror.timecore.animation.watcher.AnimationTicker;
import ru.timeconqueror.timecore.animation.watcher.AnimationTickerImpl;
import ru.timeconqueror.timecore.animation.watcher.EmptyAnimationTicker;
import ru.timeconqueror.timecore.animation.watcher.FreezableTime.FreezeCause;
import ru.timeconqueror.timecore.animation.watcher.TransitionTicker;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.Layer;
import ru.timeconqueror.timecore.api.animation.builders.LayerDefinition;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.molang.MolangFillers;

public class LayerImpl implements Layer {
    @Getter
    private final String name;
    @Getter
    private final BlendType blendType;
    @Getter
    private final float weight;
    @Getter
    private final MolangEnvironment environment;
    @Getter
    @Setter
    private AnimationTicker currentTicker = EmptyAnimationTicker.INSTANCE;

    public LayerImpl(BaseAnimationManager animationManager, LayerDefinition layerDefinition) {
        this.name = layerDefinition.name();
        this.blendType = layerDefinition.blendType();
        this.weight = layerDefinition.weight();

        environment = createMolangEnvironment(animationManager);
    }

    private MolangEnvironment createMolangEnvironment(BaseAnimationManager animationManager) {
        MolangRuntime.Builder builder = new MolangRuntime.Builder();
        animationManager.getMolangSharedObjects().forEach(builder::loadLibrary);

        MolangFillers.addAnimationBasedQueries(this, builder.getQuery());

        return builder.create();
    }

    public void update(BaseAnimationManager manager, long systemTime) {
        boolean paused = manager.isGamePaused();
        var current = getCurrentTicker();

        if (paused) {
            current.freeze(FreezeCause.GAME_PAUSED);
            return;
        }

        current.unfreeze(FreezeCause.GAME_PAUSED);

        current.initIfNeedsTo();

        if (current.isAnimationEnded(systemTime)) {
            // may be called multiple times for one animation
            current.handleEndOnLayer(this);
        }

        current = getCurrentTicker();
        current.initIfNeedsTo();
    }

    public void apply(ITimeModel model, long systemTime) {
        getCurrentTicker().apply(model, getBlendType(), getWeight(), getEnvironment(), systemTime);
    }

    public boolean start(AnimationStarter.AnimationData data) {
        if (data.isIgnorable() && currentTicker.canIgnore(data)) {
            return false;
        }

        AnimationTickerImpl animationTicker = new AnimationTickerImpl(data);
        if (data.getTransitionTime() == 0) {
            currentTicker = animationTicker;
        } else {
            currentTicker = new TransitionTicker(currentTicker, animationTicker, data.getTransitionTime());
        }

        return true;
    }

    public void removeAnimation(int transitionTime) {
        if (currentTicker.isEmpty()) return;

        currentTicker = new TransitionTicker(currentTicker, EmptyAnimationTicker.INSTANCE, transitionTime);
    }
}
