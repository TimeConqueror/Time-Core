package ru.timeconqueror.timecore.animation.action;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.AnimationCompanionData;
import ru.timeconqueror.timecore.animation.watcher.AnimationTickerImpl;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.animation.AnimationScript;
import ru.timeconqueror.timecore.api.animation.AnimationTicker;
import ru.timeconqueror.timecore.api.animation.action.ActionContext;
import ru.timeconqueror.timecore.api.animation.action.BakedAction;
import ru.timeconqueror.timecore.api.util.CollectionUtils;
import ru.timeconqueror.timecore.api.util.Empty;

import java.util.*;
import java.util.stream.Collectors;

@Log4j2
public class LayerActionManager implements AnimationEventListener {
    private final AnimatedObject<?> owner;
    private final PredefinedActionManagerImpl<?> predefinedActionManagerImpl;
    @Getter
    private final ActiveActions activeActions = new ActiveActions();

    public LayerActionManager(AnimatedObject<?> owner, PredefinedActionManagerImpl<?> predefinedActionManagerImpl) {
        this.owner = owner;
        this.predefinedActionManagerImpl = predefinedActionManagerImpl;
    }

    @Override
    public void onAnimationStarted(String layerName, AnimationTicker ticker) {
        if (ticker instanceof AnimationTickerImpl impl) {
            AnimationScript animationScript = impl.getAnimationScript();
            var companionData = animationScript.getCompanionData();
            if (companionData != AnimationCompanionData.EMPTY) {
                List<ActionTicker> currentActions = new ArrayList<>();

                if (!companionData.getInplaceActions().isEmpty()) {
                    currentActions.addAll(CollectionUtils.mapList(companionData.getInplaceActions(), ActionTicker::new));
                }

                if (!companionData.getPredefinedActionsToPlay().isEmpty()) {
                    companionData.getPredefinedActionsToPlay().stream()
                            .map(predefinedActionManagerImpl::tryCreateAction)
                            .filter(Objects::nonNull)
                            .map(ActionTicker::new)
                            .forEach(currentActions::add);
                }

                activeActions.setCurrentActions(currentActions);

                if (ActionManager.loggerEnabled) {
                    log.debug("Added actions on layer '{}': {}", layerName, activeActions.getIds());
                }
            }
        }
    }

    @Override
    public void onAnimationStopped(String layerName, AnimationTicker ticker, long clockTime) {
        onAnimationTick(layerName, ticker, clockTime);
        if (ticker instanceof AnimationTickerImpl && !activeActions.isEmpty()) {
            if (ActionManager.loggerEnabled) {
                log.debug("Stopped actions on layer '{}': {}", layerName, activeActions.getIds());
            }
            activeActions.clear();
        }
    }

    @Override
    public void onAnimationTick(String layerName, AnimationTicker ticker, long clockTime) {
        if (ticker instanceof AnimationTickerImpl && !activeActions.isEmpty()) {
            for (ActionTicker currentAction : activeActions.get()) {
                currentAction.onUpdate(ticker, owner, clockTime);
            }
        }
    }

    public static class ActionTicker {
        private final BakedAction<?> bakedAction;
        private long lastAnimationCycleIndex = 0;

        public ActionTicker(BakedAction<?> bakedAction) {
            this.bakedAction = bakedAction;
        }

        @SuppressWarnings({"rawtypes", "unchecked"})
        public void onUpdate(AnimationTicker ticker, AnimatedObject<?> owner, long clockTime) {
            ActionContext ctx = new ActionContext(ticker, owner, clockTime, lastAnimationCycleIndex);
            lastAnimationCycleIndex = bakedAction.onUpdate(ctx);
        }
    }

    public static class ActiveActions {
        @Nullable
        private List<ActionTicker> actions = null;
        private Set<String> ids = null;

        public void setCurrentActions(List<ActionTicker> actions) {
            this.actions = actions;
            this.ids = actions.stream()
                    .map(actionTicker -> actionTicker.bakedAction.getId())
                    .collect(Collectors.toSet());
        }

        public void clear() {
            this.actions = null;
            this.ids = null;
        }

        public List<ActionTicker> get() {
            return !isEmpty() ? actions : Empty.list();
        }

        public Set<String> getIds() {
            return !isEmpty() ? ids : Empty.set();
        }

        public boolean isEmpty() {
            return actions == null;
        }
    }
}
