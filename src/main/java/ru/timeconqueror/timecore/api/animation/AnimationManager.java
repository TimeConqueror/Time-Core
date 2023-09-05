package ru.timeconqueror.timecore.api.animation;

import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

import java.util.Set;

public interface AnimationManager {

	/**
	 * Returns true, if this animation manager contains the layer with provided location,
	 * otherwise returns false.
	 */
	boolean containsLayer(String name);

	/**
     * Returns layer object by its location.
     *
     * @throws RuntimeException if layer is not found,
     *                          so you should check existing of the layer in {@link #containsLayer(String)} firstly.
     */
    @NotNull
    Layer getLayer(String name);

	/**
	 * Returns all layer names.
	 * Can be used for iterating throw the layers by their location in {@link #getLayer(String)}
	 */
	Set<String> getLayerNames();

	/**
	 * On client: called on every frame for model from the renderer of entity, which contains this manager.
	 * On server: called on every tick and with null model as a param,
	 * since no operations with model shouldn't be done on server (because no model exists on server side).
	 *
	 * @param model model to perform calculations on it.
	 *              <p>
	 *              on client: model of the bound entity;<p>
	 *              on server: null
	 */
	void applyAnimations(ITimeModel model);

    /**
     * Sets animation data to start new animation in the layer with provided location.
     *
     * @see AnimationStarter#startAt(AnimationManager, String)
     */
    boolean setAnimation(AnimationStarter animationStarter, String layerName);

	/**
	 * Removes animation from the layer with provided location.
	 * Transition location is default here: {@link AnimationConstants#BASIC_TRANSITION_TIME}
	 *
	 * @param layerName location of the layer, where you need to remove animation.
	 */
    void removeAnimation(String layerName);

    /**
     * Removes animation from the layer with provided location.
     *
     * @param layerName      location of the layer, where you need to remove animation.
     * @param transitionTime time of transition to the idle state.
     *                       If this value is bigger than 0, then transition will be created, which will smoothly end current animation.
     */
    void removeAnimation(String layerName, int transitionTime);

	SharedMolangObject getSharedMolangObjects();
}
