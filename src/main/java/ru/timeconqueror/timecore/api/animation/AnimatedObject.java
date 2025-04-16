package ru.timeconqueror.timecore.api.animation;

import ru.timeconqueror.timecore.animation.AnimationSystem;

/**
 * An interface for objects to provide animation stuff.
 * TileEntity example: BlockEntityHeatCube class in test package
 * Entity example: FloroEntity class in test package
 */
public interface AnimatedObject<T extends AnimatedObject<T>> {
    /**
     * The entry point for accessing animation stuff.
     */
    AnimationSystem<T> animationSystem();

    default void populateMolangObjects(MolangObjectFiller molangObjectFiller) {

    }
}
