package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.MobEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.mod.common.packet.InternalPacketManager;
import ru.timeconqueror.timecore.mod.common.packet.S2CEndAnimationMsg;
import ru.timeconqueror.timecore.mod.common.packet.S2CStartAnimationMsg;

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
    public void setAnimation(AnimationStarter.AnimationData animationData, String layerName) {
        super.setAnimation(animationData, layerName);

        if (containsLayer(layerName)) {
            InternalPacketManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> stateMachine.getEntity()), new S2CStartAnimationMsg(stateMachine.getEntity(), layerName, animationData));
        }
    }

    @Override
    protected void onAnimationEnd(TimeEntityModel<?> model, Layer layer, AnimationWatcher watcher, long currentTime) {
        proceedActions(watcher);

        stateMachine.getActionWatchers().removeIf(actionWatcher -> actionWatcher.isBound(watcher.getAnimation()));
    }

    private void proceedActions(AnimationWatcher watcher) {
        for (StateMachineImpl.ActionWatcher<T> actionWatcher : stateMachine.getActionWatchers()) {
            if (actionWatcher.isBound(watcher.getAnimation())) {
                if (actionWatcher.shouldBeExecuted(watcher)) {
                    actionWatcher.runAction(stateMachine.getEntity());
                }
            }
        }
    }

    @Override
    public void removeAnimation(String layerName, int transitionTime) {
        super.removeAnimation(layerName, transitionTime);

        InternalPacketManager.INSTANCE.send(PacketDistributor.TRACKING_ENTITY.with(() -> stateMachine.getEntity()), new S2CEndAnimationMsg(stateMachine.getEntity(), layerName, transitionTime));
    }

    @Override
    public boolean isGamePaused() {
        return FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().isGamePaused();
    }
}
