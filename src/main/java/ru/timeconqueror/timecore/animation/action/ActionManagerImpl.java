//package ru.timeconqueror.timecore.animation.action;
//
//import ru.timeconqueror.timecore.animation.BaseAnimationManager;
//import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
//import ru.timeconqueror.timecore.api.animation.ActionManager;
//import ru.timeconqueror.timecore.api.animation.Animation;
//import ru.timeconqueror.timecore.api.animation.action.IDelayedAction;
//
//import java.util.HashSet;
//import java.util.Objects;
//import java.util.Set;
////FIXME TO MAKE IT NOT RUN ON ANY ANIMATION WITH SAME NAME
//public class ActionManagerImpl<T> implements ActionManager<T> {
//    private final Set<ActionWatcher<T, ?>> actionWatchers = new HashSet<>();
//    private final BaseAnimationManager animationManager;
//    private final T boundObject;
//
//    public ActionManagerImpl(BaseAnimationManager animationManager, T boundObject) {
//        this.animationManager = animationManager;
//        this.boundObject = boundObject;
//    }
//
//    @Override
//    public <EXTRA_DATA> void enableAction(IDelayedAction<T, EXTRA_DATA> action, EXTRA_DATA actionData) {
//        if (actionWatchers.add(new ActionWatcher<>(action, actionData))) {
//            action.getStarter().startAt(animationManager, action.getLayerName());
//        }
//    }
//
//    @Override
//    public boolean isActionEnabled(IDelayedAction<T, ?> action) {
//        for (ActionWatcher<T, ?> actionWatcher : actionWatchers) {
//            if (actionWatcher.stores(action.getId())) {
//                return true;
//            }
//        }
//
//        return false;
//    }
//
//    //TODo add check for if it's transitTo/anim or transitFrom
//    @Override
//    public <EXTRA_DATA> void disableAction(IDelayedAction<T, EXTRA_DATA> action) {
//        if (actionWatchers.removeIf(watcher -> watcher.action.equals(action))) {
//            animationManager.removeAnimation(action.getLayerName());
//        }
//    }
//
//    @SuppressWarnings("unchecked")
//    public <EXTRA_DATA> void updateActionData(IDelayedAction<T, EXTRA_DATA> action, EXTRA_DATA newData) {
//        for (ActionWatcher<T, ?> w : actionWatchers) {
//            if (w.stores(action.getId())) {
//                ((ActionWatcher<T, EXTRA_DATA>) w).updateData(newData);
//            }
//        }
//    }
//
//    @Override
//    public BaseAnimationManager getAnimationManager() {
//        return animationManager;
//    }
//
//    public T getBoundObject() {
//        return boundObject;
//    }
//
//    public void updateActions(AnimationWatcher animWatcher) {
//        for (ActionWatcher<T, ?> watcher : actionWatchers) {
//            if (watcher.isBound(animWatcher.getAnimation())) {
//                watcher.onTick(animWatcher, boundObject);
//            }
//        }
//    }
//
//    //TODO also add check if transitTo==animation
//    public void onAnimationStop(AnimationWatcher watcher) {
//        actionWatchers.removeIf(actionWatcher -> actionWatcher.isBound(watcher.getAnimation()));
//    }
//
//    public void onLoopedAnimationRestart(AnimationWatcher watcher) {
//        for (ActionWatcher<T, ?> w : actionWatchers) {
//            if (w.isBound(watcher.getAnimation())) {
//                w.reset();
//            }
//        }
//    }
//
//    public static class ActionWatcher<T, EXTRA_DATA> {
//        private final IDelayedAction<T, EXTRA_DATA> action;
//        private EXTRA_DATA actionData;
//        private boolean done;
//
//        public ActionWatcher(IDelayedAction<T, EXTRA_DATA> action, EXTRA_DATA actionData) {
//            this.action = action;
//            this.actionData = actionData;
//        }
//
//        public boolean isBound(Animation animation) {
//            return action.isBound(animation);
//        }
//
//        public boolean stores(String id) {
//            return this.action.getId().equals(id);
//        }
//
//        public void updateData(EXTRA_DATA data) {
//            this.actionData = data;
//        }
//
//        public void onTick(AnimationWatcher watcher, T obj) {
//            if (!done && action.getHandler().onUpdate(watcher, obj, actionData)) {
//                done = true;
//            }
//        }
//
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) return true;
//            if (!(o instanceof ActionWatcher<?, ?> that)) return false;
//            return action.getId().equals(that.action.getId());
//        }
//
//        @Override
//        public int hashCode() {
//            return Objects.hash(action.getId());
//        }
//
//        public void reset() {
//            if (action.isRepeatedOnLoop()) {
//                done = false;
//            }
//        }
//    }
//}
