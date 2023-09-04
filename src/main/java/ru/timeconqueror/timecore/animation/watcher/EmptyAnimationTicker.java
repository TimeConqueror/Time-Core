package ru.timeconqueror.timecore.animation.watcher;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.LayerImpl;
import ru.timeconqueror.timecore.animation.util.AnimationTickerSerializer;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;

public class EmptyAnimationTicker extends AnimationTicker {
    public static final EmptyAnimationTicker INSTANCE = new EmptyAnimationTicker();
    private static final AnimationStarter.AnimationData EMPTY = new AnimationStarter(Animation.NULL).getData();

    private EmptyAnimationTicker() {
        super(new Timeline(0, 1, false, 0L, 0));
    }

    @Override
    public void apply(ITimeModel model, BlendType blendType, float outerWeight, MolangEnvironment environment, long systemTime) {

    }

    @Override
    public void handleEndOnLayer(LayerImpl layer) {

    }

    @Override
    public boolean canIgnore(AnimationStarter.AnimationData data) {
        return data == EMPTY;
    }

    @Override
    public boolean isTransition() {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public AnimationStarter.AnimationData getAnimationData() {
        return EMPTY;
    }

    public static class Serializer implements AnimationTickerSerializer<EmptyAnimationTicker> {

        @Override
        public void serialize(EmptyAnimationTicker ticker, FriendlyByteBuf buffer) {

        }

        @Override
        public EmptyAnimationTicker deserialize(FriendlyByteBuf buffer) {
            return INSTANCE;
        }
    }
}
