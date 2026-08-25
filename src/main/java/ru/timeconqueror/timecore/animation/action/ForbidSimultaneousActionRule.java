package ru.timeconqueror.timecore.animation.action;

import java.util.Set;

/**
 * Forbids simultaneous playing of specific actions.
 * Does not check if next animation scripts in sequence has that actions.
 */
public class ForbidSimultaneousActionRule implements ActionRule {
    private final Set<String> simultaneousForbiddenList;

    public ForbidSimultaneousActionRule(Set<String> simultaneousForbiddenList) {
        this.simultaneousForbiddenList = simultaneousForbiddenList;
    }

    @Override
    public boolean canBeStartedNow(String actionId, ActionManager actionManager) {
        if (!simultaneousForbiddenList.contains(actionId)) {
            return true;
        }

        for (String forbidden : simultaneousForbiddenList) {
            if (actionManager.isActive(forbidden)) {
                return false;
            }
        }

        return true;
    }
}
