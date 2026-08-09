package ru.timeconqueror.timecore.animation.action;

import ru.timeconqueror.timecore.api.animation.Layer;

import java.util.*;
import java.util.function.Supplier;

public class ActionManagerImpl implements ActionManager {
    private final Map<String, LayerActionManager> layerActionManagers;
    private final List<ActionRule> actionRules = new ArrayList<>(0);

    public static ActionManagerImpl create(Collection<? extends Layer> layers, Supplier<LayerActionManager> layerActionManagerFactory) {
        Map<String, LayerActionManager> layerActionManagers = new HashMap<>();

        for (Layer layer : layers) {
            LayerActionManager layerActionManager = layerActionManagerFactory.get();
            layer.addAnimationEventListener(layerActionManager);
            if (layerActionManagers.put(layer.getName(), layerActionManager) != null) {
                throw new IllegalArgumentException("Duplicate layer name detected: " + layer.getName());
            }
        }

        return new ActionManagerImpl(layerActionManagers);
    }

    private ActionManagerImpl(Map<String, LayerActionManager> layerActionManagers) {
        this.layerActionManagers = layerActionManagers;
    }

    @Override
    public boolean isActive(String actionId) {
        boolean active = false;
        for (LayerActionManager manager : layerActionManagers.values()) {
            if (manager.getActiveActions().getIds().contains(actionId)) {
                active = true;
            }
        }

        return active;
    }

    @Override
    public boolean isActiveOnLayer(String actionId, String layerName) {
        return layerActionManagers.get(layerName).getActiveActions().getIds().contains(actionId);
    }

    @Override
    public boolean isAnyActive() {
        boolean isAnyActive = false;
        for (LayerActionManager manager : layerActionManagers.values()) {
            if (!manager.getActiveActions().isEmpty()) {
                isAnyActive = true;
            }
        }

        return isAnyActive;
    }

    @Override
    public boolean isAnyActiveOnLayer(String layerName) {
        return !layerActionManagers.get(layerName).getActiveActions().isEmpty();
    }

    @Override
    public void addStartRule(ActionRule rule) {
        actionRules.add(rule);
    }

    @Override
    public boolean canBeStartedNow(String actionId) {
        for (ActionRule rule : actionRules) {
            if (!rule.canBeStartedNow(actionId, this)) {
                return false;
            }
        }
        return true;
    }
}
