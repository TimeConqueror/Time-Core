package ru.timeconqueror.timecore.animation.internal;

import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.animation.IAnimation;

import java.util.HashMap;
import java.util.Map;

public class AnimationRegistry {
    private static final Map<ResourceLocation, IAnimation> animationMap = new HashMap<>();

    public static IAnimation registerAnimation(IAnimation animation) {
        if (animationMap.put(animation.getId(), animation) != null) {
            throw new IllegalArgumentException("Animation with id " + animation.getId() + " is already registered.");
        }

        return animation;
    }

    @Nullable
    public static IAnimation getAnimation(ResourceLocation id) {
        return animationMap.get(id);
    }
}
