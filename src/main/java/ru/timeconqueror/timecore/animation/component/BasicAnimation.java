package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.Empty;
import ru.timeconqueror.timecore.animation.watcher.TimelineSnapshot;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.Channel;
import ru.timeconqueror.timecore.api.animation.ILayer;
import ru.timeconqueror.timecore.api.animation.TransitionFactoryWithDestination;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.holder.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class BasicAnimation extends Animation {
    @Getter
    private final LoopMode loopMode;
    @Getter
    private final String name;
    @Getter
    private final ResourceLocation id;
    /**
     * animation length in ms
     */
    @Getter
    private final int length;

    /**
     * Unmodifiable map of bone options.
     * Key - bone location.
     */
    @Nullable
    @Getter
    private final Map<String, AnimationBone> options;

    public BasicAnimation(LoopMode loopMode, ResourceLocation id, String name, int length, @Nullable Map<String, AnimationBone> options) {
        this.loopMode = loopMode;
        this.name = name;
        this.id = id;
        this.length = length;
        this.options = options;
    }

    public void apply(ITimeModel model, ILayer layer, MolangEnvironment env, int existingTime) {
        if (options != null) {
            if (existingTime <= length) {
                for (AnimationBone animationBone : options.values()) {
                    TimeModelPart piece = model.tryGetPart(animationBone.getName());

                    if (piece != null) {
                        animationBone.apply(this, layer, piece, env, existingTime);
                    }
                }
            }
        }
    }

    @Override
    public @NotNull ru.timeconqueror.timecore.api.animation.TransitionFactory createTransitionFactory(MolangEnvironment env) {
        return new AnimationTransitionFactory(env, this);
    }

    @Override
    public void forEachBone(Consumer<String> action) {
        if (getOptions() != null) {
            getOptions().forEach((s, option) -> action.accept(s));
        }
    }

    public static class AnimationTransitionFactory extends TransitionFactoryWithDestination {
        public AnimationTransitionFactory(MolangEnvironment env, BasicAnimation source) {
            super(env, source);
        }

        private IKeyFrame calcStartKeyFrame(BasicAnimation sourceAnimation, List<IKeyFrame> sourceKeyFrames, Vector3f modelIdleVec, int existingTime) {
            Vector3f vec = KeyFrameInterpolator.findInterpolationVec(sourceAnimation, this.getEnv(), sourceKeyFrames, existingTime);
            if (vec != null) return KeyFrame.createSimple(0, vec);

            return KeyFrame.createSimple(0, modelIdleVec);
        }

        private Pair<IKeyFrame, IKeyFrame> makeTransitionPair(BasicAnimation source, TimelineSnapshot destinationStartTime, TimeModelPart part, AnimationBone sourceOption, Channel channel, TransitionFactoryWithDestination destFactory, int existingTime, int transitionTime) {
            IKeyFrame startKeyFrame = calcStartKeyFrame(source, sourceOption.getKeyFrames(channel), channel.getDefaultVector(part), existingTime);
            IKeyFrame endKeyFrame = destFactory.getDestKeyFrame(part, destinationStartTime, sourceOption.getName(), channel, transitionTime);
            return Pair.of(startKeyFrame, endKeyFrame);
        }

        @Override
        public @Nullable List<Transition.AnimationBone> createTransitionBones(AnimationStarter.AnimationData dest, ITimeModel model, int existingTime, int transitionTime) {
            BasicAnimation source = getSourceTyped();
            if (source.getOptions() == null || source.getOptions().isEmpty()) {
                return null;
            }

            TransitionFactoryWithDestination destFactory = dest.getAnimation().createTransitionFactory(getEnv()).withRequiredDestination();
            TimelineSnapshot destinationStartTime = TimelineSnapshot.createForStartTime(dest);

            HashMap<String, Transition.AnimationBone> transitionBones = new HashMap<>();
            source.getOptions().forEach((name, sourceBone) -> {
                TimeModelPart part = model.tryGetPart(name);
                if (part != null) {
                    Pair<IKeyFrame, IKeyFrame> rotations = makeTransitionPair(source, destinationStartTime, part, sourceBone, Channel.ROTATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> translations = makeTransitionPair(source, destinationStartTime, part, sourceBone, Channel.TRANSLATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> scales = makeTransitionPair(source, destinationStartTime, part, sourceBone, Channel.SCALE, destFactory, existingTime, transitionTime);
                    transitionBones.put(name, new Transition.AnimationBone(name, getEnv(), rotations, translations, scales));
                }
            });

            ArrayList<Transition.AnimationBone> resultBones = new ArrayList<>();
            Iterable<AnimationBone> destBones = destFactory.getDestinationBones();

            for (AnimationBone destBone : destBones) {
                String destBoneName = destBone.getName();
                if (transitionBones.containsKey(destBoneName)) {
                    continue;
                }

                TimeModelPart part = model.tryGetPart(destBoneName);
                if (part != null) {
                    Pair<IKeyFrame, IKeyFrame> rotations = makeTransitionPairFromIdle(part, destinationStartTime, destBoneName, Channel.ROTATION, destFactory, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> translations = makeTransitionPairFromIdle(part, destinationStartTime, destBoneName, Channel.TRANSLATION, destFactory, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> scales = makeTransitionPairFromIdle(part, destinationStartTime, destBoneName, Channel.SCALE, destFactory, transitionTime);
                    resultBones.add(new Transition.AnimationBone(destBoneName, getEnv(), rotations, translations, scales));
                }
            }

            resultBones.addAll(transitionBones.values());

            return resultBones;
        }

        @Override
        public Iterable<AnimationBone> getDestinationBones() {
            Map<String, AnimationBone> options = this.<BasicAnimation>getSourceTyped().getOptions();
            return options != null ? options.values() : Empty.list();
        }

        @Override
        public @NotNull IKeyFrame getDestKeyFrame(TimeModelPart part, TimelineSnapshot snapshot, String boneName, Channel channel, int transitionTime) {
            BasicAnimation dest = getSourceTyped();

            AnimationBone destBone = dest.getOptions() != null ? dest.getOptions().get(boneName) : null;
            if(destBone != null) {
                Vector3f interpolationVec = KeyFrameInterpolator.findInterpolationVec(dest, this.getEnv(), destBone.getKeyFrames(channel), snapshot.getSavedAnimationTime());
                if(interpolationVec != null) {
                    return KeyFrame.createSimple(transitionTime, interpolationVec);
                }
            }

            return KeyFrame.createSimple(transitionTime, channel.getDefaultVector(part));
        }
    }

    @Override
    public String toString() {
        return "BasicAnimation{" +
                "location=" + name +
                ", id=" + id +
                ", loopMode=" + loopMode +
                ", length=" + length +
                '}';
    }
}
