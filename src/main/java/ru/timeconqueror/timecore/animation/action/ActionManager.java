package ru.timeconqueror.timecore.animation.action;

import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.watcher.AnimationTickerImpl;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationTicker;
import ru.timeconqueror.timecore.api.animation.action.ActionContext;
import ru.timeconqueror.timecore.api.animation.action.ActionInstance;
import ru.timeconqueror.timecore.api.animation.action.AnimationUpdateListener;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.Empty;

import java.util.List;

@Log4j2
public class ActionManager implements AnimationEventListener {
    public static boolean loggerEnabled = true;
    private final AnimatedObject<?> owner;
    @Nullable
    private List<ActionManager.ActionTicker> currentActions = null;

    public ActionManager(AnimatedObject<?> owner) {
        this.owner = owner;
    }

    @Override
    public void onAnimationStarted(String layerName, AnimationTicker ticker) {
        if (ticker instanceof AnimationTickerImpl impl) {
            var companionData = impl.getCompanionData();
            if (companionData != AnimationCompanionData.EMPTY && !companionData.getActionList().isEmpty()) {
                currentActions = CollectionUtils.mapList(companionData.getActionList(), ActionTicker::new);
                if (loggerEnabled) {
                    log.debug("Added actions on layer '{}': {}", layerName, getActionIds());
                }
            }
        }
    }

    @Override
    public void onAnimationStopped(String layerName, AnimationTicker ticker) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            if (loggerEnabled) {
                log.debug("Stopped actions on layer '{}': {}", layerName, getActionIds());
            }
            currentActions = null;
        }
    }

    @Override
    public void onAnimationUpdate(String layerName, AnimationTicker ticker, long clockTime) {
        if (ticker instanceof AnimationTickerImpl && currentActions != null) {
            for (ActionTicker currentAction : currentActions) {
                currentAction.onUpdate(ticker, owner, clockTime);
            }
        }
    }

    private List<String> getActionIds() {
        return currentActions != null ? CollectionUtils.mapList(currentActions, actionTicker -> actionTicker.actionInstance.getId()) : Empty.list();
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
