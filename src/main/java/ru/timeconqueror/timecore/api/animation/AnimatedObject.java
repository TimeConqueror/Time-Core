package ru.timeconqueror.timecore.api.animation;

import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.animation.AnimationSystem;
import ru.timeconqueror.timecore.animation.renderer.AnimatedLivingEntityRenderer;

/**
 * An interface for entities to provide animation stuff.
 * You also need to use {@link AnimatedLivingEntityRenderer} for animations to work.
 * <br>
 * Example of implementing:
 * <blockquote><pre>
 *  public class EntityFloro extends MonsterEntity implements AnimatedObject<EntityFloro> {
 *     private final ActionController<EntityFloro> actionController;
 *
 *     public EntityFloro(EntityType<? extends MonsterEntity> type, World worldIn) {
 *         super(type, worldIn);
 *
 *         actionController = new ActionControllerBuilder<EntityFloro>(
 *                 new AnimationManagerBuilder(true)
 *                         .addWalkingAnimationHandling(new AnimationStarter(TEntities.FLORO_WALK).setSpeed(2.0F), "walking")
 *                         .addLayer("attack", 1, BlendType.ADDING, 0.9F)
 *         ).forEntity(this, world);
 *     }
 *
 *     public @NotNull ActionController<EntityFloro> getActionController() {
 *         return actionController;
 *     }
 * }
 * </pre></blockquote>
 *
 * @see AnimatedLivingEntityRenderer
 */
public interface AnimatedObject<T extends AnimatedObject<T>> {
    /**
     * The entry point for accessing animation stuff.
     */
    @NotNull
    AnimationSystem<T> getSystem();

    default ActionManager<T> getActionManager() {
        return getSystem().getActionManager();
    }

    default AnimationManager getAnimationManager() {
        return getSystem().getAnimationManager();
    }
}
