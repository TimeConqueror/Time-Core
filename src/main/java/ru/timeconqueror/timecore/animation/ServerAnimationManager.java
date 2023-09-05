package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;
import ru.timeconqueror.timecore.api.animation.AnimatedObject;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

public class ServerAnimationManager<T extends AnimatedObject<T>> extends BaseAnimationManager {
    private final NetworkDispatcherInstance<T> networkDispatcher;

    public ServerAnimationManager(SharedMolangObject sharedMolangObject, NetworkDispatcherInstance<T> networkDispatcher) {
        super(sharedMolangObject);
        this.networkDispatcher = networkDispatcher;
    }

    @Override
    public boolean setAnimation(AnimationStarter animationStarter, String layerName) {
        var set = super.setAnimation(animationStarter, layerName);
        if (set) {
            networkDispatcher.sendSetAnimationPacket(animationStarter.getData(), layerName);
        }
        return set;
    }

    @Override
    public void removeAnimation(String layerName, int transitionTime) {
        super.removeAnimation(layerName, transitionTime);

        networkDispatcher.sendRemoveAnimationPacket(layerName, transitionTime);
    }

    @Override
    public boolean isGamePaused() {
        return FMLEnvironment.dist == Dist.CLIENT && Minecraft.getInstance().isPaused();
    }

    @Override
    protected void applyAnimation(ITimeModel model, LayerImpl layer, long systemTime) {

    }
}
