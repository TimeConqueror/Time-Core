package ru.timeconqueror.timecore.api.animation;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationStarterImpl;
import ru.timeconqueror.timecore.animation.component.BasicAnimation;
import ru.timeconqueror.timecore.animation.component.LoopMode;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

import java.util.function.Consumer;
//TODo freeze layers (add the config system which disables some layers/frezzes them, etc)
public abstract class Animation {
	@SuppressWarnings("StaticInitializerReferencesSubClass")
	public static final Animation NULL = new BasicAnimation(LoopMode.LOOP, TimeCore.rl("internal/null"), "null", Integer.MAX_VALUE, null);

    public abstract void apply(ITimeModel model, BlendType blendType, float weight, MolangEnvironment env, int existingTime);

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
	 * Proceeds some action for each bone.
	 *
	 * @param action action to call for every bone. Consumes bone location.
	 */
	public abstract void forEachBone(Consumer<String> action);

    public AnimationStarter starter() {
        return new AnimationStarterImpl(this);
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!(obj instanceof Animation animation)) return false;
        return getId().equals(animation.getId());
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

}