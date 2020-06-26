package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

public class ServerAnimationManager<T extends MobEntity> extends BaseAnimationManager {
    private StateMachineImpl<T> stateMachine;

    public ServerAnimationManager(@Nullable AnimationStarter walkingAnimationStarter) {
        super(walkingAnimationStarter);
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
                DelayedAction<T> action = actionWatcher.getAction();

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
