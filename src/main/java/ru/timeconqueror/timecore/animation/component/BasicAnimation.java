package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.Map;
import java.util.function.Consumer;

public class BasicAnimation extends Animation {
    @Getter
    private final LoopMode loopMode;
    @Getter
    private final String name;
    @Getter
    private final ResourceLocation id;
    /**
     * animation length in ms
     */
    @Getter
    private final int length;

    /**
     * Unmodifiable map of bone options.
     * Key - bone location.
     */
    @Nullable
    @Getter
    private final Map<String, AnimationBone> options;

    public BasicAnimation(LoopMode loopMode, ResourceLocation id, String name, int length, @Nullable Map<String, AnimationBone> options) {
        this.loopMode = loopMode;
        this.name = name;
        this.id = id;
        this.length = length;
        this.options = options;
    }

    @Override
    public void apply(ITimeModel model, BlendType blendType, float weight, MolangEnvironment env, int animationTime) {
        if (options != null) {
            if (animationTime <= length) {
                for (AnimationBone animationBone : options.values()) {
                    TimeModelPart piece = model.tryGetPart(animationBone.getName());

                    if (piece != null) {
                        animationBone.apply(this, blendType, weight, piece, env, animationTime);
                    }
                }
            }
        }
    }

    @Override
    public void forEachBone(Consumer<String> action) {
        if (getOptions() != null) {
            getOptions().forEach((s, option) -> action.accept(s));
        }
    }

    @Override
    public String toString() {
        return "BasicAnimation{" +
                "location=" + name +
                ", id=" + id +
                ", loopMode=" + loopMode +
                ", length=" + length +
                '}';
    }
}
