package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

public class ServerAnimationManager<T extends Entity> extends BaseAnimationManager {
    private StateMachineImpl<T> stateMachine;

    public ServerAnimationManager() {
    }

    void setStateMachine(StateMachineImpl<T> stateMachine) {
        this.stateMachine = stateMachine;
    }

    @Override
    protected void applyAnimation(TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime) {
        proceedActions(watcher);
    }

    @Override
    protected void onAnimationEnd(TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime) {
        proceedActions(watcher);
    }

    private void proceedActions(AnimationWatcher watcher) {
        for (StateMachineImpl.ActionWatcher<T> actionWatcher : stateMachine.getActionWatchers()) {
            if (actionWatcher.getAction().getAnimationStarter().getData().prototype.equals(watcher.getAnimation())) {
                Action<T> action = actionWatcher.getAction();

                if (action.getActionDelayPredicate().test(watcher)) {
                    action.getAction().accept(stateMachine.getEntity());
                }
            }
        }
    }

    @Override
    public boolean isGamePaused() {
        return FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().isGamePaused();
    }
}
