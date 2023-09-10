package ru.timeconqueror.timecore.animation.action;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.watcher.AnimationTickerImpl;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationTickerInfo;
import ru.timeconqueror.timecore.api.animation.action.Action;
import ru.timeconqueror.timecore.api.animation.action.ActionInstance;
import ru.timeconqueror.timecore.api.util.CollectionUtils;

import java.util.List;

public class ActionManager implements AnimationEventListener {
    private final AnimatedObject<?> owner;
    @Nullable
    private List<ActionManager.ActionTicker> currentActions = null;

    public ActionManager(AnimatedObject<?> owner) {
        this.owner = owner;
    }

    @Override
    public void onAnimationStarted(AnimationTickerInfo ticker) {
        if (ticker instanceof AnimationTickerImpl impl) {
            var companionData = impl.getCompanionData();
            if (companionData != AnimationCompanionData.EMPTY && !companionData.getActionList().isEmpty()) {
                currentActions = CollectionUtils.mapList(companionData.getActionList(), ActionTicker::new);
            }
        }
    }

    @Override
    public void onAnimationUpdate(AnimationTickerInfo ticker) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            for (ActionTicker currentAction : currentActions) {
                currentAction.onTick(ticker, owner);
            }
        }
    }

    @Override
    public void onAnimationEnded(AnimationTickerInfo ticker) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            for (ActionTicker currentAction : currentActions) {
                currentAction.onTick(ticker, owner);
            }
        }
    }

    @Override
    public void onAnimationStopped(AnimationTickerInfo ticker) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            currentActions = null;
        }
    }

    @Override
    public void onAnimationRestarted(AnimationTickerInfo ticker) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            for (ActionTicker currentAction : currentActions) {
                currentAction.reset();
            }
        }
    }

    public static class ActionTicker {
        private final ActionInstance<?, ?> actionInstance;
        private boolean done;

        public ActionTicker(ActionInstance<?, ?> actionInstance) {
            this.actionInstance = actionInstance;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public void onTick(AnimationTickerInfo ticker, AnimatedObject<?> owner) {
            Action.AnimationTickListener listener = actionInstance.getAction().getListener();
            if (!done && listener.onTick(ticker, owner, actionInstance.getData())) {
                done = true;
            }
        }

        public void reset() {
            if (actionInstance.getAction().isRepeatedOnLoop()) {
                done = false;
            }
        }
    }
}
