package ru.timeconqueror.timecore.animation.component;

import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.Empty;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.animation.Channel;
import ru.timeconqueror.timecore.api.animation.TransitionFactoryWithDestination;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

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

    public void apply(ITimeModel model, AnimationLayer layer, int existingTime) {
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

    @Override
    public Animation reverse() {
        Map<String, BoneOption> reversedOptions;

        if (options == null) {
            reversedOptions = null;
        } else {
            reversedOptions = new HashMap<>();

            options.forEach((s, boneOption) -> {
                List<IKeyFrame> rotations = reverseKeyFrames(boneOption.getKeyFrames(Channel.ROTATION));
                List<IKeyFrame> translations = reverseKeyFrames(boneOption.getKeyFrames(Channel.TRANSLATION));
                List<IKeyFrame> scales = reverseKeyFrames(boneOption.getKeyFrames(Channel.SCALE));

                reversedOptions.put(boneOption.getName(), new BoneOption(boneOption.getName(), rotations, translations, scales));
            });
        }

        return new BasicAnimation(loopMode, new ResourceLocation(id.getNamespace(), id.getPath() + "-reversed"), name + "-reversed", length, reversedOptions);
    }

    private List<IKeyFrame> reverseKeyFrames(List<IKeyFrame> keyFrames) {
        if (keyFrames.isEmpty()) return Empty.list();

        return keyFrames.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(IKeyFrame::getTime)))
                //  .map(keyFrame -> new KeyFrame(length - keyFrame.getTime(), keyFrame.getVec(zzz)))//FIXME
                .collect(Collectors.toList());
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

        private static Pair<IKeyFrame, IKeyFrame> makeTransitionPair(BasicAnimation source, TimeModelPart part, BoneOption option, Channel channel, TransitionFactoryWithDestination destFactory, int existingTime, int transitionTime) {
            IKeyFrame startKeyFrame = calcStartKeyFrame(source, option.getKeyFrames(channel), channel.getDefaultVector(part), existingTime);
            IKeyFrame endKeyFrame = destFactory.getDestKeyFrame(part, option.getName(), channel, transitionTime);
            return Pair.of(startKeyFrame, endKeyFrame);
        }

        @Override
        public @Nullable List<Transition.BoneOption> createBoneOptions(Animation dest, ITimeModel model, int existingTime, int transitionTime) {
            BasicAnimation source = getSourceTyped();
            if (source.getOptions() == null || source.getOptions().isEmpty()) {
                return null;
            }

            TransitionFactoryWithDestination destFactory = dest.getTransitionFactory().withRequiredDestination();

            List<Transition.BoneOption> transitionBones = new ArrayList<>();
            source.getOptions().forEach((name, sourceBone) -> {
                TimeModelPart part = model.tryGetPart(name);
                if (part != null) {
                    Pair<IKeyFrame, IKeyFrame> rotations = makeTransitionPair(source, part, sourceBone, Channel.ROTATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> translations = makeTransitionPair(source, part, sourceBone, Channel.TRANSLATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> scales = makeTransitionPair(source, part, sourceBone, Channel.SCALE, destFactory, existingTime, transitionTime);
                    transitionBones.add(new Transition.BoneOption(name, rotations, translations, scales));
                }
            });

            Iterable<BoneOption> destBones = destFactory.getDestAnimationBones();
            main:
            for (BoneOption destBone : destBones) {
                String destBoneName = destBone.getName();
                for (Transition.BoneOption bone : transitionBones) {
                    if (bone.getName().equals(destBoneName)) {// TODO improve by checking for index, not for name
                        continue main;
                    }
                }

                TimeModelPart part = model.tryGetPart(destBoneName);
                if (part != null) {
                    Pair<IKeyFrame, IKeyFrame> rotations = makeTransitionPairFromIdle(part, destBoneName, Channel.ROTATION, destFactory, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> translations = makeTransitionPairFromIdle(part, destBoneName, Channel.TRANSLATION, destFactory, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> scales = makeTransitionPairFromIdle(part, destBoneName, Channel.SCALE, destFactory, transitionTime);
                    transitionBones.add(new Transition.BoneOption(destBoneName, rotations, translations, scales));
                }
            }

            return transitionBones;
        }

        @Override
        public Iterable<BoneOption> getDestAnimationBones() {
            Map<String, BoneOption> options = this.<BasicAnimation>getSourceTyped().getOptions();
            return options != null ? options.values() : Empty.list();
        }

        private static IKeyFrame makeDestKeyFrame(TimeModelPart part, @Nullable BoneOption destBone, Channel channel, int transitionTime) {
            if (destBone == null) {
                return KeyFrame.createIdleKeyFrame(transitionTime, channel.getDefaultVector(part));
            }

            List<IKeyFrame> keyFrames = destBone.getKeyFrames(channel);
            if (keyFrames.isEmpty()) {
                return KeyFrame.createIdleKeyFrame(transitionTime, channel.getDefaultVector(part));
            }

            IKeyFrame keyFrame = keyFrames.get(0);
            if (keyFrame.getTime() != 0) {//FIXME should have new logic
                return KeyFrame.createIdleKeyFrame(transitionTime, channel.getDefaultVector(part));
            }

            return keyFrame.withNewTime(transitionTime);
        }

        @Override
        public @NotNull IKeyFrame getDestKeyFrame(TimeModelPart part, String boneName, Channel channel, int transitionTime) {
            BasicAnimation dest = getSourceTyped();
            boolean destContainsSameBone = dest.getOptions() != null && dest.getOptions().containsKey(boneName);
            BoneOption destBone = destContainsSameBone ? dest.getOptions().get(boneName) : null;

            return makeDestKeyFrame(part, destBone, channel, transitionTime);
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
