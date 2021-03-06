package ru.timeconqueror.timecore.animation.action;

import net.minecraft.tileentity.TileEntity;
import ru.timeconqueror.timecore.animation.BaseAnimationManager;
import ru.timeconqueror.timecore.animation.builders.PredefinedAnimations;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;

public class TileEntityActionManager<T extends TileEntity & AnimatedObject<T>> extends ActionManagerImpl<T> {
    private final PredefinedAnimations predefinedAnimations;

    public TileEntityActionManager(BaseAnimationManager animationManager, T boundObject, PredefinedAnimations predefinedAnimations) {
        super(animationManager, boundObject);
        this.predefinedAnimations = predefinedAnimations;
    }
}
