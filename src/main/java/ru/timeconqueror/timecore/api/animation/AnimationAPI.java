package ru.timeconqueror.timecore.api.animation;

import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.timecore.animation.AnimationRegistry;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.loading.AnimationLoader;

import java.util.Map;
//ToDO make allbuilders with Entity generic type
public class AnimationAPI {
    /**
     * Only for animation files with the single animation!!!
     * <p>
     * Creates and registers animation, which is a single one in the file with provided path.
     *
     * @param animationFileLocation path to the file, should contain path to the file location under the data/ folder.
	 *                              Example:
	 *                              {@code loadAndRegisterAnimation(new ResourceLocation(TimeCore.MODID, "animations/zombie_hit.json"))}
	 *                              results in {@code data/timecore/animations/zombie_hit.json}.
     */
    public static Animation loadAndRegisterAnimation(ResourceLocation animationFileLocation) {
        return AnimationRegistry.registerAnimation(AnimationLoader.loadAnimation(animationFileLocation));
    }

    /**
     * Creates a map of all animations in the file with provided path and registers all retrieved animations.
     * Key in this map is the location of the animation, that is indicated in the file.
     *
     * @param animationFileLocation path to the file, should contain path to the file location under the data/ folder.
	 *                              Example:
	 *                              {@code loadAndRegisterAnimation(new ResourceLocation(TimeCore.MODID, "animations/zombie_hit.json"))}
	 *                              results in {@code data/timecore/animations/zombie_hit.json}.
     */
    public static Map<String, Animation> loadAndRegisterAnimations(ResourceLocation animationFileLocation) {
	    Map<String, Animation> animations = AnimationLoader.loadAnimations(animationFileLocation);

	    for (Animation animation : animations.values()) {
		    AnimationRegistry.registerAnimation(animation);
	    }

	    return animations;
    }

	/**
	 * Registers the provided animation.
	 */
	public static Animation register(Animation animation) {
		return AnimationRegistry.registerAnimation(animation);
	}

	/**
     * Returns the reversed version of this animation.
     * This method is resource-intensive, so it's better to call this once and store the result somewhere.
     * Don't forget about registering returned animation.
     */
    // FIXME doesn't work for now
    public static Animation reverse(Animation animation) {
        return animation.reverse();
    }

	public static AnimationStarter createStarter(Animation animation) {
		return new AnimationStarter(animation);
	}

	public static void startAnimation(AnimationStarter animationStarter, AnimationManager animationManager, String layerName) {
		animationStarter.startAt(animationManager, layerName);
	}

	public static void removeAnimation(AnimationManager animationManager, String layerName) {
		animationManager.removeAnimation(layerName);
	}
}
