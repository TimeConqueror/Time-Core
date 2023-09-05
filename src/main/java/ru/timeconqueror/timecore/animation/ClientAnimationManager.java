package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.molang.SharedMolangObject;

public class ClientAnimationManager extends BaseAnimationManager {

    public ClientAnimationManager(SharedMolangObject sharedMolangObject) {
        super(sharedMolangObject);
    }

    @Override
    protected void applyAnimation(ITimeModel model, LayerImpl layer, long systemTime) {
        layer.apply(model, systemTime);
    }

    @Override
    public boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }
}
