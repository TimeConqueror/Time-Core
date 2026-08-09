package ru.timeconqueror.timecore.animation.action;

public interface ActionManager {
    boolean loggerEnabled = true;

    boolean isActive(String actionId);
    boolean isActiveOnLayer(String actionId, String layerId);
    boolean isAnyActive();
    boolean isAnyActiveOnLayer(String layerId);

    void addStartRule(ActionRule rule);
    boolean canBeStartedNow(String actionId);
}
