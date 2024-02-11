package ru.timeconqueror.timecore.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.Getter;
import ru.timeconqueror.timecore.animation.action.AnimationEventListener;
import ru.timeconqueror.timecore.animation.network.AnimationState;
import ru.timeconqueror.timecore.animation.watcher.AbstractAnimationTicker;
import ru.timeconqueror.timecore.animation.watcher.AnimationTickerImpl;
import ru.timeconqueror.timecore.animation.watcher.EmptyAnimationTicker;
import ru.timeconqueror.timecore.animation.watcher.TransitionTicker;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.animation.Layer;
import ru.timeconqueror.timecore.api.animation.builders.LayerDefinition;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.molang.CustomMolangRuntime;

import java.util.ArrayList;
import java.util.List;

public class LayerImpl implements Layer, AnimationController {
    @Getter
    private final String name;
    @Getter
    private final BlendType blendType;
    @Getter
    private final float weight;
    @Getter
    private final MolangEnvironment environment;
    private final List<AnimationEventListener> eventListeners;
    @Getter
    private AbstractAnimationTicker currentTicker = EmptyAnimationTicker.INSTANCE;

    public LayerImpl(BaseAnimationManager animationManager, LayerDefinition layerDefinition) {
        this.name = layerDefinition.name();
        this.blendType = layerDefinition.blendType();
        this.weight = layerDefinition.weight();

        environment = createMolangEnvironment(animationManager);
        this.eventListeners = new ArrayList<>();
    }

    private CustomMolangRuntime createMolangEnvironment(BaseAnimationManager animationManager) {
        CustomMolangRuntime runtime = new CustomMolangRuntime();
        animationManager.getSharedMolangObjects().forEach(runtime::loadLibrary);
        return runtime;
    }

    public void update(long clockTime) {
        while (true) {
            AbstractAnimationTicker currentTicker = getCurrentTicker();

            eventListeners.forEach(listener -> listener.onAnimationUpdate(this.name, currentTicker, clockTime));
            currentTicker.update(this, clockTime);

            AbstractAnimationTicker newTicker = getCurrentTicker();
            if (newTicker == currentTicker) {
                break;
            }
        }
    }

    public void apply(ITimeModel model, long clockTime) {
        getCurrentTicker().apply(model, getBlendType(), getWeight(), getEnvironment(), clockTime);
    }

    @Override
    public boolean startAnimation(AnimationData data, long clockTime, AnimationCompanionData companionData) {
        if (data.isIgnorable() && getCurrentTicker().canIgnore(data)) {
            return false;
        }

        AnimationTickerImpl animationTicker = new AnimationTickerImpl(data, clockTime, companionData);
        if (data.getTransitionTime() == 0) {
            setCurrentTicker(animationTicker);
        } else {
            setCurrentTicker(new TransitionTicker(getCurrentTicker(), data, companionData, clockTime, data.getTransitionTime()));
        }

        return true;
    }

    @Override
    public void removeAnimation(long clockTime, int transitionTime) {
        if (getName().isEmpty()) return;

        if (transitionTime == 0) {
            setCurrentTicker(EmptyAnimationTicker.INSTANCE);
            return;
        }

        setCurrentTicker(new TransitionTicker(getCurrentTicker(), null, AnimationCompanionData.EMPTY, clockTime, transitionTime));
    }

    public void setCurrentTicker(AbstractAnimationTicker ticker) {
        eventListeners.forEach(listener -> listener.onAnimationStopped(this.name, getCurrentTicker()));

        this.currentTicker = ticker;

        eventListeners.forEach(listener -> listener.onAnimationStarted(this.name, getCurrentTicker()));
    }

    @Override
    public void addAnimationEventListener(AnimationEventListener listener) {
        this.eventListeners.add(listener);
    }

    public AnimationState getAnimationState(long clockTime) {
        return getCurrentTicker().getState(clockTime);
    }

    public void setAnimationState(AnimationState state, long clockTime) {
        setCurrentTicker(AbstractAnimationTicker.fromState(state, clockTime));
    }
}
