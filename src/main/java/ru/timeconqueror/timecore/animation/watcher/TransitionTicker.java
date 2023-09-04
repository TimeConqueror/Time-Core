package ru.timeconqueror.timecore.animation.watcher;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.LayerImpl;
import ru.timeconqueror.timecore.animation.util.AnimationTickerSerializer;
import ru.timeconqueror.timecore.animation.util.TickerSerializers;
import ru.timeconqueror.timecore.api.animation.BlendType;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.MathUtils;

public class TransitionTicker extends AnimationTicker {
    @Getter
    private final AnimationTicker source;
    @Getter
    private final AnimationTicker destination;

    public TransitionTicker(AnimationTicker source, AnimationTicker destination, int transitionTime) {
        super(new Timeline(transitionTime, 1.0F, false, System.currentTimeMillis(), 0));
        this.source = source;
        this.destination = destination;
        this.source.freeze(FreezableTime.FreezeCause.IN_TRANSITION);
        this.destination.freeze(FreezableTime.FreezeCause.IN_TRANSITION);
    }

    @Override
    public void freeze(FreezableTime.FreezeCause cause) {
        super.freeze(cause);
        source.freeze(cause);
        destination.freeze(cause);
    }

    @Override
    public void unfreeze(FreezableTime.FreezeCause cause) {
        super.unfreeze(cause);
        source.unfreeze(cause);
        destination.unfreeze(cause);
    }

    @Override
    public void init() {
        source.init();
        destination.init();
    }

    @Override
    public void apply(ITimeModel model, BlendType blendType, float outerWeight, MolangEnvironment environment, long systemTime) {
        Timeline timeline = getTimeline();

        int elapsedLength = timeline.getElapsedLength();
        float transitionWeight = elapsedLength != 0 ? ((float) timeline.getElapsedTime(systemTime)) / elapsedLength : 1F;
        transitionWeight = MathUtils.coerceInRange(transitionWeight, 0, 1);

        source.apply(model, blendType, outerWeight * (1 - transitionWeight), environment, systemTime);
        destination.apply(model, blendType, outerWeight * transitionWeight, environment, systemTime);
    }

    @Override
    public void handleEndOnLayer(LayerImpl layer) {
        destination.unfreeze(FreezableTime.FreezeCause.IN_TRANSITION);
        layer.setCurrentTicker(destination);
    }

    @Override
    public boolean canIgnore(AnimationStarter.AnimationData data) {
        return source.canIgnore(data) || destination.canIgnore(data);
    }

    @Override
    public boolean isTransition() {
        return true;
    }

    @Override
    public AnimationStarter.AnimationData getAnimationData() {
        return destination.getAnimationData();
    }

    public static class Serializer implements AnimationTickerSerializer<TransitionTicker> {
        @Override
        public void serialize(TransitionTicker ticker, FriendlyByteBuf buffer) {
            TickerSerializers.serializeTicker(ticker.source, buffer);
            TickerSerializers.serializeTicker(ticker.destination, buffer);
            Timeline timeline = ticker.getTimeline();
            buffer.writeVarInt(timeline.getLength());
            buffer.writeVarInt(timeline.getElapsedTime(System.currentTimeMillis()));
        }

        @Override
        public TransitionTicker deserialize(FriendlyByteBuf buffer) {
            AnimationTicker source = TickerSerializers.deserializeTicker(buffer);
            AnimationTicker destination = TickerSerializers.deserializeTicker(buffer);
            int transitionTime = buffer.readVarInt();
            int elapsedTime = buffer.readVarInt();

            TransitionTicker ticker = new TransitionTicker(source, destination, transitionTime);
            ticker.getTimeline().setFromElapsed(elapsedTime);
            return ticker;
        }
    }
}
