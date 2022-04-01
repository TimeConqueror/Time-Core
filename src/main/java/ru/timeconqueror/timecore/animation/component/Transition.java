package ru.timeconqueror.timecore.animation.component;

import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.*;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Transition extends Animation {
    private final int transitionLength;
    private final String name;
    @Nullable
    private final Animation destAnimation;
    private final ResourceLocation id;
    private List<BoneOption> options = new ArrayList<>();

    private Transition(int transitionLength, String name, @Nullable Animation destAnimation) {
        this.transitionLength = transitionLength;
        this.name = name;
        this.id = new ResourceLocation(TimeCore.MODID, "internal/" + getName());
        this.destAnimation = destAnimation;
    }

    private static Transition createFromIdleState(@NotNull Animation dest, ITimeModel model, int transitionTime) {
        Transition transition = new Transition(transitionTime, "idle_to_" + dest.getName(), dest);

        TransitionFactoryWithDestination destFactory = dest.getTransitionFactory().withRequiredDestination();

        dest.forEachBone(name -> {
            TimeModelPart part = model.tryGetPart(name);
            if (part != null) {
                Pair<KeyFrame, KeyFrame> rotations = TransitionFactory.makeTransitionPairFromIdle(part, name, Channel.ROTATION, destFactory, transitionTime);
                Pair<KeyFrame, KeyFrame> positions = TransitionFactory.makeTransitionPairFromIdle(part, name, Channel.POSITION, destFactory, transitionTime);
                Pair<KeyFrame, KeyFrame> scales = TransitionFactory.makeTransitionPairFromIdle(part, name, Channel.SCALE, destFactory, transitionTime);
                transition.options.add(new BoneOption(name, rotations, positions, scales));
            }
        });

        return transition;
    }

    private static Transition createToIdleState(@Nullable Animation source, ITimeModel model, int existingTime, int transitionTime) {
        Transition transition = new Transition(transitionTime, (source != null ? source.getName() : "idle") + "_to_idle", null);

        if (source != null) {
            TransitionFactory transitionFactory = source.getTransitionFactory();
            transition.options = transitionFactory.createBoneOptions(Animation.NULL, model, existingTime, transitionTime);
        }

        return transition;
    }

    @NotNull
    public static Animation create(@Nullable Animation source, int sourceExistingTime, @Nullable Animation dest, ITimeModel model, int transitionTime) {
        if (dest == null) {
            return createToIdleState(source, model, source != null ? sourceExistingTime : 0, transitionTime);
        } else if (source == null) {
            return createFromIdleState(dest, model, transitionTime);
        }

        return create(source, dest, model, sourceExistingTime, transitionTime);
    }

    public static Animation createForServer(@Nullable Animation source, @Nullable Animation dest, int transitionTime) {
        String sourceName = source != null ? source.getName() : "idle";
        String destName = dest != null ? dest.getName() : "idle";
        return new Transition(transitionTime, sourceName + "_to_" + destName, dest);
    }

    private static Animation create(@NotNull Animation source, @NotNull Animation dest, ITimeModel model, int existingTime, int transitionTime) {
        TransitionFactory sourceTFactory = source.getTransitionFactory();

        List<BoneOption> options = sourceTFactory.createBoneOptions(dest, model, existingTime, transitionTime);
        if (options == null) {
            return createFromIdleState(dest, model, transitionTime);
        }

        Transition transition = new Transition(transitionTime, source.getName() + "_to_" + dest.getName(), dest);
        transition.options = options;

        return transition;
    }

    @Override
    public void apply(ITimeModel model, AnimationLayer layer, int existingTime) {
        if (options != null) {
            if (existingTime <= transitionLength) {
                options.forEach(boneOption -> {
                    TimeModelPart piece = model.tryGetPart(boneOption.name);

                    if (piece != null) {
                        boneOption.apply(piece, layer, existingTime);
                    }
                });
            }
        }
    }

    @Override
    public void forEachBone(Consumer<String> action) {
        options.forEach(transitionBoneOption -> action.accept(transitionBoneOption.name));
    }

    @Override
    public Animation reverse() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isLooped() {
        return false;
    }

    @Override
    public @NotNull TransitionFactory getTransitionFactory() {
        return new InternalTransitionFactory(this);
    }

    @Override
    public int getLength() {
        return transitionLength;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Nullable
    public Animation getDestination() {
        return destAnimation;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "location='" + name + '\'' +
                ", id=" + id +
                ", transitionLength=" + transitionLength +
                ", destAnimation=" + destAnimation +
                '}';
    }

    private static class InternalTransitionFactory extends TransitionFactory {

        public InternalTransitionFactory(Transition source) {
            super(source);
        }

        private static KeyFrame calcStartKeyFrame(@Nullable Pair<KeyFrame, KeyFrame> sourceFrames, Vector3f modelIdleVec, int existingTime) {
            if (sourceFrames != null) {
                Vector3f vec = KeyFrameInterpolator.interpolateLinear(sourceFrames.left(), sourceFrames.right(), existingTime);
                return new KeyFrame(0, vec);
            }

            return KeyFrame.createIdleKeyFrame(0, modelIdleVec);
        }

        private static Pair<KeyFrame, KeyFrame> makeTransitionPair(TimeModelPart part, BoneOption option, Channel channel, TransitionFactoryWithDestination destFactory, int existingTime, int transitionTime) {
            KeyFrame startKeyFrame = calcStartKeyFrame(option.getKeyFrames(channel), channel.getDefaultVector(part), existingTime);
            KeyFrame endKeyFrame = destFactory.getDestKeyFrame(part, option.getName(), channel, transitionTime);
            return Pair.of(startKeyFrame, endKeyFrame);
        }

        @Override
        public @Nullable List<BoneOption> createBoneOptions(Animation dest, ITimeModel model, int existingTime, int transitionTime) {
            Transition source = getSourceTyped();
            if (source.options == null || source.options.isEmpty()) {
                return null;
            }

            TransitionFactoryWithDestination destFactory = dest.getTransitionFactory().withRequiredDestination();

            List<BoneOption> transitionBones = new ArrayList<>();
            source.options.forEach(bone -> {
                TimeModelPart part = model.tryGetPart(bone.name);
                if (part != null) {
                    Pair<KeyFrame, KeyFrame> rotations = makeTransitionPair(part, bone, Channel.ROTATION, destFactory, existingTime, transitionTime);
                    Pair<KeyFrame, KeyFrame> positions = makeTransitionPair(part, bone, Channel.POSITION, destFactory, existingTime, transitionTime);
                    Pair<KeyFrame, KeyFrame> scales = makeTransitionPair(part, bone, Channel.SCALE, destFactory, existingTime, transitionTime);
                    transitionBones.add(new BoneOption(bone.name, rotations, positions, scales));
                }
            });

            return transitionBones;
        }
    }

    public static class BoneOption {
        private final String name;

        private final Pair<KeyFrame, KeyFrame> rotations;
        private final Pair<KeyFrame, KeyFrame> positions;
        private final Pair<KeyFrame, KeyFrame> scales;

        public BoneOption(String boneName, Pair<KeyFrame, KeyFrame> rotations, Pair<KeyFrame, KeyFrame> positions, Pair<KeyFrame, KeyFrame> scales) {
            this.name = boneName;
            this.rotations = rotations;
            this.positions = positions;
            this.scales = scales;
        }

        public void apply(TimeModelPart piece, AnimationLayer layer, int existingTime) {
            Vector3f interpolated = KeyFrameInterpolator.interpolateLinear(rotations.left(), rotations.right(), existingTime);
            AnimationUtils.applyRotation(piece, layer, interpolated);

            interpolated = KeyFrameInterpolator.interpolateLinear(positions.left(), positions.right(), existingTime);
            AnimationUtils.applyOffset(piece, layer, interpolated);

            interpolated = KeyFrameInterpolator.interpolateLinear(scales.left(), scales.right(), existingTime);
            AnimationUtils.applyScale(piece, layer, interpolated);
        }

        public String getName() {
            return name;
        }

        public Pair<KeyFrame, KeyFrame> getKeyFrames(Channel channel) {
            if (channel == Channel.ROTATION) {
                return rotations;
            } else if (channel == Channel.POSITION) {
                return positions;
            } else if (channel == Channel.SCALE) {
                return scales;
            }

            throw new IllegalArgumentException("Unknown channel: " + channel);
        }
    }
}
