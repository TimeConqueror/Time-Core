package ru.timeconqueror.timecore.animation.component;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.Empty;
import ru.timeconqueror.timecore.animation.watcher.AnimationWatcher;
import ru.timeconqueror.timecore.animation.watcher.Timeline;
import ru.timeconqueror.timecore.animation.watcher.TimelineSnapshot;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.Channel;
import ru.timeconqueror.timecore.api.animation.ILayer;
import ru.timeconqueror.timecore.api.animation.TransitionFactoryWithDestination;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.holder.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.*;
import java.util.function.Consumer;

public class BasicAnimation extends Animation {
    private final LoopMode loopMode;
    private final String name;
    private final ResourceLocation id;
    /**
     * animation length in ms
     */
    private final int length;

    /**
     * Unmodifiable map of bone options.
     * Key - bone location.
     */
    @Nullable
    private final Map<String, BoneOption> options;

    public BasicAnimation(LoopMode loopMode, ResourceLocation id, String name, int length, @Nullable Map<String, BoneOption> options) {
        this.loopMode = loopMode;
        this.name = name;
        this.id = id;
        this.length = length;
        this.options = options;
    }

    public void apply(ITimeModel model, ILayer layer, int existingTime) {
        if (options != null) {
            if (existingTime <= length) {
                options.forEach((s, boneOption) -> {
                    TimeModelPart piece = model.tryGetPart(boneOption.getName());

                    if (piece != null) {
                        boneOption.apply(this, layer, piece, existingTime);
                    }
                });
            }
        }
    }

    public int getLength() {
        return length;
    }

    public String getName() {
        return name;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    public LoopMode getLoopMode() {
        return loopMode;
    }

    public @Nullable Map<String, BoneOption> getOptions() {
        return options;
    }

    @Override
    public @NotNull ru.timeconqueror.timecore.api.animation.TransitionFactory getTransitionFactory() {
        return new AnimationTransitionFactory(this);
    }

    @Override
    public void forEachBone(Consumer<String> action) {
        if (getOptions() != null) {
            getOptions().forEach((s, option) -> action.accept(s));
        }
    }

    public static class AnimationTransitionFactory extends TransitionFactoryWithDestination {
        public AnimationTransitionFactory(BasicAnimation source) {
            super(source);
        }

        private static IKeyFrame calcStartKeyFrame(BasicAnimation sourceAnimation, List<IKeyFrame> sourceKeyFrames, Vector3f modelIdleVec, int existingTime) {
            Vector3f vec = KeyFrameInterpolator.findInterpolationVec(sourceAnimation, sourceKeyFrames, existingTime);
            if (vec != null) return new KeyFrame(0, vec);

            return KeyFrame.createIdleKeyFrame(0, modelIdleVec);
        }

        private static Pair<IKeyFrame, IKeyFrame> makeTransitionPair(BasicAnimation source, TimelineSnapshot destinationStartTime, TimeModelPart part, BoneOption sourceOption, Channel channel, TransitionFactoryWithDestination destFactory, int existingTime, int transitionTime) {
            IKeyFrame startKeyFrame = calcStartKeyFrame(source, sourceOption.getKeyFrames(channel), channel.getDefaultVector(part), existingTime);
            IKeyFrame endKeyFrame = destFactory.getDestKeyFrame(part, destinationStartTime, sourceOption.getName(), channel, transitionTime);
            return Pair.of(startKeyFrame, endKeyFrame);
        }

        @Override
        public @Nullable List<Transition.BoneOption> createTransitionBones(AnimationStarter.AnimationData dest, ITimeModel model, int existingTime, int transitionTime) {
            BasicAnimation source = getSourceTyped();
            if (source.getOptions() == null || source.getOptions().isEmpty()) {
                return null;
            }

            TransitionFactoryWithDestination destFactory = dest.getAnimation().getTransitionFactory().withRequiredDestination();
            TimelineSnapshot destinationStartTime = TimelineSnapshot.createForStartTime(dest);

            HashMap<String, Transition.BoneOption> transitionBones = new HashMap<>();
            source.getOptions().forEach((name, sourceBone) -> {
                TimeModelPart part = model.tryGetPart(name);
                if (part != null) {
                    Pair<IKeyFrame, IKeyFrame> rotations = makeTransitionPair(source, destinationStartTime, part, sourceBone, Channel.ROTATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> translations = makeTransitionPair(source, destinationStartTime, part, sourceBone, Channel.TRANSLATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> scales = makeTransitionPair(source, destinationStartTime, part, sourceBone, Channel.SCALE, destFactory, existingTime, transitionTime);
                    transitionBones.put(name, new Transition.BoneOption(name, rotations, translations, scales));
                }
            });

            ArrayList<Transition.BoneOption> resultBones = new ArrayList<>();
            Iterable<BoneOption> destBones = destFactory.getDestinationBones();

            for (BoneOption destBone : destBones) {
                String destBoneName = destBone.getName();
                if(transitionBones.containsKey(destBoneName)) {
                    continue;
                }

                TimeModelPart part = model.tryGetPart(destBoneName);
                if (part != null) {
                    Pair<IKeyFrame, IKeyFrame> rotations = makeTransitionPairFromIdle(part, destinationStartTime, destBoneName, Channel.ROTATION, destFactory, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> translations = makeTransitionPairFromIdle(part, destinationStartTime, destBoneName, Channel.TRANSLATION, destFactory, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> scales = makeTransitionPairFromIdle(part, destinationStartTime, destBoneName, Channel.SCALE, destFactory, transitionTime);
                    resultBones.add(new Transition.BoneOption(destBoneName, rotations, translations, scales));
                }
            }

            resultBones.addAll(transitionBones.values());

            return resultBones;
        }

        @Override
        public Iterable<BoneOption> getDestinationBones() {
            Map<String, BoneOption> options = this.<BasicAnimation>getSourceTyped().getOptions();
            return options != null ? options.values() : Empty.list();
        }

        @Override
        public @NotNull IKeyFrame getDestKeyFrame(TimeModelPart part, TimelineSnapshot snapshot, String boneName, Channel channel, int transitionTime) {
            BasicAnimation dest = getSourceTyped();

            BoneOption destBone = dest.getOptions() != null ? dest.getOptions().get(boneName) : null;
            if(destBone != null) {
                Vector3f interpolationVec = KeyFrameInterpolator.findInterpolationVec(dest, destBone.getKeyFrames(channel), snapshot.getSavedAnimationTime());
                if(interpolationVec != null) {
                    return new KeyFrame(transitionTime, interpolationVec);
                }
            }

            return KeyFrame.createIdleKeyFrame(transitionTime, channel.getDefaultVector(part));
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
