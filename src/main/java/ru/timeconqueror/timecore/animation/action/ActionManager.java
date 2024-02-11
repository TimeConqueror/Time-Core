package ru.timeconqueror.timecore.animation.action;

import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.watcher.AnimationTickerImpl;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationTicker;
import ru.timeconqueror.timecore.api.animation.action.ActionContext;
import ru.timeconqueror.timecore.api.animation.action.ActionInstance;
import ru.timeconqueror.timecore.api.animation.action.AnimationUpdateListener;
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
    public void onAnimationStarted(AnimationTicker ticker) {
        if (ticker instanceof AnimationTickerImpl impl) {
            var companionData = impl.getCompanionData();
            if (companionData != AnimationCompanionData.EMPTY && !companionData.getActionList().isEmpty()) {
                currentActions = CollectionUtils.mapList(companionData.getActionList(), ActionTicker::new);
            }
        }
    }

    @Override
    public void onAnimationStopped(AnimationTicker ticker) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            currentActions = null;
        }
    }

    @Override
    public void onAnimationUpdate(AnimationTicker ticker, long clockTime) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            for (ActionTicker currentAction : currentActions) {
                currentAction.onUpdate(ticker, owner, clockTime);
            }
        }
    }

    public static class ActionTicker {
        private final ActionInstance<?, ?> actionInstance;
        private int lastAnimationCycleIndex = 0;

        public ActionTicker(ActionInstance<?, ?> actionInstance) {
            this.actionInstance = actionInstance;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public void onUpdate(AnimationTicker ticker, AnimatedObject<?> owner, long clockTime) {
            AnimationUpdateListener listener = actionInstance.getUpdateListener();
            ActionContext ctx = new ActionContext(ticker, owner, actionInstance.getData(), clockTime, lastAnimationCycleIndex);
            lastAnimationCycleIndex = listener.onUpdate(ctx);
        }
    }
}
