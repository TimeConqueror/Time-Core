package ru.timeconqueror.timecore.animation;

import net.minecraft.client.Minecraft;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.molang.MolangSharedObjects;

public class ClientAnimationManager extends BaseAnimationManager {

    public ClientAnimationManager(MolangSharedObjects molangSharedObjects) {
        super(molangSharedObjects);
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
