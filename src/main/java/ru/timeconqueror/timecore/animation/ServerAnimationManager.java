package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import ru.timeconqueror.timecore.animation.action.ActionManagerImpl;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

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
    protected void applyAnimation(ITimeModel model, Layer layer, AnimationWatcher watcher, long currentTime) {
        actionManager.updateActions(watcher);
    }

    @Override
    protected void onAnimationStart(Layer layer, AnimationStarter.AnimationData data, AnimationWatcher watcher) {
        super.onAnimationStart(layer, data, watcher);

        networkDispatcher.sendSetAnimationPacket(actionManager, data, layer);
    }

    @Override
    protected void onAnimationStop(AnimationWatcher watcher) {
        actionManager.updateActions(watcher);
        actionManager.onAnimationStop(watcher);
    }

    @Override
    public void onLoopedAnimationRestart(AnimationWatcher watcher) {
        super.onLoopedAnimationRestart(watcher);

        actionManager.onLoopedAnimationRestart(watcher);
    }

    @Override
    public void removeAnimation(String layerName, int transitionTime) {
        super.removeAnimation(layerName, transitionTime);

        networkDispatcher.sendRemoveAnimationPacket(actionManager, layerName, transitionTime);
    }

    @Override
    public boolean isGamePaused() {
        return FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().isPaused();
    }
}
