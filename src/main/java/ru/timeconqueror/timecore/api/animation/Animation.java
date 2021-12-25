package ru.timeconqueror.timecore.api.animation;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.KeyFrame;
import ru.timeconqueror.timecore.animation.component.Transition;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.List;
import java.util.function.Consumer;

public abstract class Animation {
	@SuppressWarnings("StaticInitializerReferencesSubClass")
	public static final Animation NULL = new BasicAnimation(false, TimeCore.rl("internal/null"), "null", 0, null);

	public abstract void apply(ITimeModel model, AnimationLayer layer, int existingTime);

	/**
	 * Name of the animation, that is indicated in animation file.
	 */
	public abstract String getName();

	/**
	 * By default contains the path to the file, from which this animation was parsed,
	 * merged with the animation location from the file.
	 */
	public abstract ResourceLocation getId();

	/**
	 * Length in ms
	 */
	public abstract int getLength();

	public abstract boolean isLooped();

	/**
	 * Should return the factory, that can handle your IAnimation implementation class
	 */
	@NotNull
	public abstract Animation.TransitionFactory getTransitionFactory();

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

	public enum OptionType {
		ROTATION,
		POSITION,
		SCALE
	}

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

	public abstract static class TransitionFactory {
		/**
		 * Animation, from which transition will start.
		 */
		protected Animation source;

		public TransitionFactory(Animation source) {
			this.source = source;
		}

		@SuppressWarnings("unchecked")
		public <T> T getSourceTyped() {
			return ((T) source);
		}

		/**
		 * Returns list of bones with calculated change vectors.
		 * Returns null if list can't be created due to, for example, lack of bone option list.
		 * In this case transition will be created with more strong dependence to destination animation.
		 * <p>
		 * Will be called only when {@link #source} is a start animation.
		 *
		 * @param dest           animation, to which transition will lead.
		 * @param model          model, for which we apply animation.
		 * @param existingTime   source animation existing time
		 * @param transitionTime time of transition between source and destination animations.
		 */
		@Nullable
		public abstract List<Transition.BoneOption> createBoneOptions(Animation dest, ITimeModel model, int existingTime, int transitionTime);

		/**
		 * Returns destination keyframe of provided type for transition animation.
		 * <p>
		 * Will be called only when {@link #source} is a destination animation.
		 *
		 * @param piece          piece for which destination keyframe should be calculated.
		 * @param boneName       location of bone/piece for which destination keyframe should be calculated.
		 * @param optionType     type of keyframe which should be calculated.
		 * @param transitionTime time of transition between source and destination animations.
		 */
		@NotNull
        public abstract KeyFrame getDestKeyFrame(TimeModelRenderer piece, String boneName, OptionType optionType, int transitionTime);
    }
}