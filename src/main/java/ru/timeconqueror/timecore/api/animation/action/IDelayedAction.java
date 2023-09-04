package ru.timeconqueror.timecore.api.animation.action;

import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.watcher.AnimationTicker;
import ru.timeconqueror.timecore.api.animation.Animation;

public interface IDelayedAction<T, EXTRA_DATA> {
    boolean isBound(Animation animation);

    Handler<? super T, ? super EXTRA_DATA> getHandler();

    boolean isRepeatedOnLoop();

    String getId();

    String getLayerName();

    AnimationStarter getStarter();

    interface Handler<T, EXTRA_DATA> {
        /**
         * Called on server every tick and one extra time upon animation end until this method returns true.
         * Here you can fully control the behavior of attached animated object.
         *
         * @param object    bound animated object (entity, tile entity, etc.) for which action is currently active.
         * @param extraData user data to be read or write
         * @return true if the action is done
         */
        boolean onUpdate(AnimationTicker watcher, T object, EXTRA_DATA extraData);
    }

//    /**
//     * Creates builder for Delayed actions.
//     *
//     * @param id      ID of action. By this ID they will be compared for deletion and addition.
//     * @param starter animation starter, which will be played when action is started.
//     * @param layer   layer, where animation will be played.
//     */
//    static <T, EXTRA_DATA> Builder<T, EXTRA_DATA> builder(String id, String layer, AnimationStarter starter) {
//        return new Builder<>(id, layer, starter);
//    }
}
