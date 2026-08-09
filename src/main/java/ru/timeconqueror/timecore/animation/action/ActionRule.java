package ru.timeconqueror.timecore.animation.action;

public interface ActionRule {
    boolean canBeStartedNow(String actionId, ActionManager actionManager);
}
