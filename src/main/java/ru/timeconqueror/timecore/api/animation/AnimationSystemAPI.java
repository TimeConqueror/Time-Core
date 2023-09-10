package ru.timeconqueror.timecore.api.animation;

import lombok.Setter;
import org.jetbrains.annotations.ApiStatus;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.api.animation.action.ActionInstance;

import java.util.List;

public class AnimationSystemAPI<T extends AnimatedObject<T>> {
    @Setter
    @ApiStatus.Internal
    private AnimationSystem<T> system;

    public <DATA> boolean startAnimation(AnimationStarter animationStarter, String layerName, List<ActionInstance<? super T, ? super DATA>> actionList) {
        @SuppressWarnings("unchecked")
        var companion = new AnimationCompanionData((List) actionList);
        return system.getAnimationManager().startAnimation(animationStarter.getData(), layerName, companion);
    }

    public boolean startAnimation(AnimationStarter animationStarter, String layerName) {
        return system.getAnimationManager().startAnimation(animationStarter.getData(), layerName, AnimationCompanionData.EMPTY);
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
        system.getAnimationManager().stopAnimation(layerName, transitionTime);
    }
}
