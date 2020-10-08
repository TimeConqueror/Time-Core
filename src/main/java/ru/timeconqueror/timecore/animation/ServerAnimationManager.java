package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.client.render.model.TimeModel;

public class ServerAnimationManager<T extends AnimatedObject<T>> extends BaseAnimationManager {
    private ActionManagerImpl<T> actionManager;
    private final NetworkDispatcher<T> networkDispatcher;

    public ServerAnimationManager(NetworkDispatcher<T> networkDispatcher) {
        this.networkDispatcher = networkDispatcher;
    }

    public void setActionManager(ActionManagerImpl<T> actionManager) {
        this.actionManager = actionManager;
    }

    @Override
    protected void applyAnimation(TimeModel model, Layer layer, AnimationWatcher watcher, long currentTime) {
        proceedActions(watcher);
    }

    @Override
    protected void onAnimationSet(AnimationStarter.AnimationData data, Layer layer) {
        super.onAnimationSet(data, layer);

        networkDispatcher.sendSetAnimationPacket(actionManager, data, layer);
    }

    @Override
    protected void onAnimationEnd(@Nullable TimeModel model, Layer layer, AnimationWatcher watcher) {
        proceedActions(watcher);

        actionManager.getActionWatchers().removeIf(actionWatcher -> actionWatcher.isBound(watcher.getAnimation()));
    }

    private void proceedActions(AnimationWatcher watcher) {
        for (ActionManagerImpl.ActionWatcher<T, ?> actionWatcher : actionManager.getActionWatchers()) {
            if (actionWatcher.isBound(watcher.getAnimation())) {
                if (actionWatcher.shouldBeExecuted(watcher)) {
                    actionWatcher.runAction(actionManager.getBoundObject());
                }
            }
        }
    }

    @Override
    public void removeAnimation(String layerName, int transitionTime) {
        super.removeAnimation(layerName, transitionTime);

        networkDispatcher.sendRemoveAnimationPacket(actionManager, layerName, transitionTime);
    }

    @Override
    public boolean isGamePaused() {
        return FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().isGamePaused();
    }
}
