package ru.timeconqueror.timecore.api.animation;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

import java.util.function.Consumer;

public abstract class Animation {
	@SuppressWarnings("StaticInitializerReferencesSubClass")
	public static final Animation NULL = new BasicAnimation(LoopMode.DO_NOT_LOOP, TimeCore.rl("internal/null"), "null", 0, null);

	public abstract void apply(ITimeModel model, AnimationLayer layer, int existingTime);

	/**
	 * Name of the animation, that is indicated in animation file.
	 */
	public abstract String getName();

	/**
     * By default, contains the path to the file, from which this animation was parsed,
     * merged with the animation location from the file.
     */
	public abstract ResourceLocation getId();

	/**
	 * Length in ms
	 */
	public abstract int getLength();

	public abstract LoopMode getLoopMode();

	/**
	 * Should return the factory, that can handle your IAnimation implementation class
	 */
	@NotNull
	public abstract TransitionFactory getTransitionFactory();

	/**
	 * Proceeds some action for each bone.
	 *
	 * @param action action to call for every bone. Consumes bone location.
	 */
	public abstract void forEachBone(Consumer<String> action);

	/**
	 * Returns the reversed version of this animation.
	 * It is slow, so you need to call this once.
	 * Don't forget about registering returned animation.
	 */
	public abstract Animation reverse();

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Animation)) return false;
		Animation animation = (Animation) obj;
		return getId().equals(animation.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}