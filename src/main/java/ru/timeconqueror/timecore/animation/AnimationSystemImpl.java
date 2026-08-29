package ru.timeconqueror.timecore.animation;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import net.minecraft.server.level.ServerPlayer;
import ru.timeconqueror.timecore.animation.action.*;
import ru.timeconqueror.timecore.animation.clock.TickBasedClock;
import ru.timeconqueror.timecore.animation.network.NetworkDispatcherInstance;
import ru.timeconqueror.timecore.api.animation.*;
import ru.timeconqueror.timecore.api.animation.action.BakedAction;

import java.util.List;

@Log4j2
@Getter
public class AnimationSystemImpl<T extends AnimatedObject<T>> implements AnimationSystem<T> {
    private final T owner;
    /**
     * Should sync with client or not
     */
    private final boolean clientSide;
    private final AnimationManager animationManager;
    private final NetworkDispatcherInstance<T> networkDispatcher;
    private final Clock clock;
    private final PredefinedAnimationManager<T> predefinedAnimationManager;
    private final PredefinedActionManagerImpl<T> predefinedActionManagerImpl;
    private final ActionManagerImpl actionManager;

    public AnimationSystemImpl(T owner,
                               boolean clientSide,
                               Clock clock,
                               AnimationManager animationManager,
                               NetworkDispatcherInstance<T> networkDispatcher,
                               PredefinedAnimationManager<T> predefinedAnimationManager,
                               PredefinedActionManagerImpl<T> predefinedActionManagerImpl,
                               ActionManagerImpl actionManager) {
        this.owner = owner;
        this.clientSide = clientSide;
        this.clock = clock;
        this.animationManager = animationManager;
        this.networkDispatcher = networkDispatcher;
        this.predefinedAnimationManager = predefinedAnimationManager;
        this.predefinedActionManagerImpl = predefinedActionManagerImpl;
        this.actionManager = actionManager;
    }

    @Override
    public <DATA> boolean startAnimation(AnimationBundle<T, DATA> animationBundle, DATA actionData) {
        AnimationScript.Builder scriptBuilder = animationBundle.toScriptBuilder(actionData);
        return startAnimationScript(scriptBuilder, animationBundle.getLayerName());
    }

    @Override
    public boolean startAnimation(AnimationStarter animationStarter, String layerName) {
        return startAnimationScript(AnimationScript.builder(animationStarter), layerName);
    }

    @Override
    public boolean startAnimationScript(AnimationScript.Builder animationScriptBuilder, String layerName) {
        List<BakedAction<?>> inplaceActions = animationScriptBuilder.getInplaceActions();
        if(inplaceActions != null) {
            for (BakedAction<?> inplaceAction : inplaceActions) {
                if (!actionManager.canBeStartedNow(inplaceAction.getId())) {
                    if(ActionManager.loggerEnabled) {
                        log.debug("Animation with inplace action {} was rejected to start on layer {}", inplaceAction.getId(), layerName);
                    }
                    return false;
                }
            }
        }

        List<String> predefinedActions = animationScriptBuilder.getPredefinedActions();
        if(predefinedActions != null) {
            for (String predefinedAction : predefinedActions) {
                if(!actionManager.canBeStartedNow(predefinedAction)) {
                    if(ActionManager.loggerEnabled) {
                        log.debug("Animation with predefined action {} was rejected to start on layer {}", predefinedAction, layerName);
                    }
                    return false;
                }
            }
        }

        return getAnimationManager().startAnimationScript(animationScriptBuilder.build(predefinedActionManagerImpl), layerName);
    }

    /**
     * Stops animation from the layer with provided name.
     * Default transition time: {@link AnimationConstants#BASIC_TRANSITION_TIME}
     *
     * @param layerName name of layer, where you need to stop animation.
     */
    public void stopAnimation(String layerName) {
        stopAnimation(layerName, AnimationConstants.BASIC_TRANSITION_TIME);//TODO move constant to special configuration object for each system
    }

    /**
     * Stops animation from the layer with provided name.
     *
     * @param layerName      name of layer, where you need to stop animation.
     * @param transitionTime time of transition to the idle state.
     *                       If this value is bigger than 0, then transition will be created, which will smoothly stop current animation.
     */
    public void stopAnimation(String layerName, int transitionTime) {
        transitionTime = Math.max(transitionTime, 0);
        getAnimationManager().stopAnimation(layerName, transitionTime);
    }

    public void addAnimationEventListener(String layerName, AnimationEventListener listener) {
        getAnimationManager().getLayer(layerName).addAnimationEventListener(listener);
    }

    public void removeAnimationEventListener(String layerName, AnimationEventListener listener) {
        getAnimationManager().getLayer(layerName).removeAnimationEventListener(listener);
    }

    public void registerPredefinedAction(String id, BakedActionFactory<T> bakedActionFactory) {
        getPredefinedActionManagerImpl().register(id, bakedActionFactory);
    }

    @Override
    public void onTick() {
        predefinedAnimationManager.onTick(this, owner);

        if (clock instanceof TickBasedClock tickBasedClock) {
            tickBasedClock.tick();
        }

        animationManager.tick();

        if (!isClientSide()) {
            // simulate ticking
            animationManager.applyAnimations(null, 0);
        }
    }

    @Override
    public PredefinedActionManagerImpl<T> getPredefinedActionManager() {
        return predefinedActionManagerImpl;
    }

    @Override
    public void sync(ServerPlayer player) {
        if (!isClientSide() && animationManager instanceof ServerAnimationManager<?>) {
            ((ServerAnimationManager<?>) animationManager).syncAnimations(player);
        }
    }
}
