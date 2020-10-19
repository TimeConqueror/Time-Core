package ru.timeconqueror.timecore.animation.component;

import net.minecraft.client.renderer.Vector3f;
import net.minecraft.util.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.client.render.model.TimeModelRenderer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Transition extends Animation {
    private static final Animation DUMMY_ANIMATION = new Animation() {
        private final ResourceLocation id = new ResourceLocation(TimeCore.MODID, "internal/" + getName());

        @Override
        public void apply(ITimeModel model, AnimationLayer layer, int existingTime) {
        }

        @Override
        public String getName() {
            return "dummy";
        }

        @Override
        public ResourceLocation getId() {
            return id;
        }

        @Override
        public int getLength() {
            return 0;
        }

        @Override
        public boolean isLooped() {
            return false;
        }

        @Override
        public @NotNull Animation.TransitionFactory getTransitionFactory() {
            return IDLE_END_TRANSITION_FACTORY;
        }

        @Override
        public void forEachBone(Consumer<String> action) {
        }

        @Override
        public Animation reverse() {
            throw new UnsupportedOperationException();
        }
    };
    private static final Animation.TransitionFactory IDLE_END_TRANSITION_FACTORY = new Animation.TransitionFactory(DUMMY_ANIMATION) {
        @Override
        public @Nullable List<TransitionBoneOption> createBoneOptions(Animation dest, ITimeModel model, int existingTime, int transitionTime) {
            throw new UnsupportedOperationException("Idle End Transition Factory shouldn't be used with source animation");
        }

        @Override
        public @NotNull KeyFrame getDestKeyFrame(TimeModelRenderer piece, String boneName, OptionType optionType, int transitionTime) {
            if (optionType == OptionType.ROTATION) {
                return KeyFrame.createIdleKeyFrame(transitionTime, new Vector3f(0, 0, 0));
            } else if (optionType == OptionType.POSITION) {
                return KeyFrame.createIdleKeyFrame(transitionTime, piece.offset);
            } else if (optionType == OptionType.SCALE) {
                return KeyFrame.createIdleKeyFrame(transitionTime, piece.getScaleFactor());
            }

            throw new UnsupportedOperationException("Can't handle " + optionType + " option type");
        }
    };
    private final int transitionLength;
    private final String name;
    @Nullable
    private final Animation destAnimation;
    private final ResourceLocation id;
    private List<TransitionBoneOption> options = new ArrayList<>();

    private Transition(int transitionLength, String name, @Nullable Animation destAnimation) {
        this.transitionLength = transitionLength;
        this.name = name;
        this.id = new ResourceLocation(TimeCore.MODID, "internal/" + getName());
        this.destAnimation = destAnimation;
    }

    private static Animation createFromIdleState(@NotNull Animation dest, ITimeModel model, int transitionTime) {
        Transition transition = new Transition(transitionTime, "idle_to_" + dest.getName(), dest);

        Animation.TransitionFactory transitionFactory = dest.getTransitionFactory();

        dest.forEachBone(name -> {
            TimeModelRenderer piece = model.getPiece(name);
            if (piece != null) {
                // Rotations
                KeyFrame startKeyFrame = KeyFrame.createIdleKeyFrame(0, new Vector3f(0, 0, 0));
                KeyFrame endKeyFrame = transitionFactory.getDestKeyFrame(piece, name, OptionType.ROTATION, transitionTime);
                Pair<KeyFrame, KeyFrame> rotations = Pair.of(startKeyFrame, endKeyFrame);

                // Positions
                startKeyFrame = KeyFrame.createIdleKeyFrame(0, piece.offset);
                endKeyFrame = transitionFactory.getDestKeyFrame(piece, name, OptionType.POSITION, transitionTime);
                Pair<KeyFrame, KeyFrame> positions = Pair.of(startKeyFrame, endKeyFrame);

                // Scales
                startKeyFrame = KeyFrame.createIdleKeyFrame(0, piece.getScaleFactor());
                endKeyFrame = transitionFactory.getDestKeyFrame(piece, name, OptionType.SCALE, transitionTime);

                Pair<KeyFrame, KeyFrame> scales = Pair.of(startKeyFrame, endKeyFrame);

                transition.options.add(new TransitionBoneOption(name, rotations, positions, scales));
            }
        });

        return transition;
    }

    private static Animation createToIdleState(@Nullable Animation source, ITimeModel model, int existingTime, int transitionTime) {
        Transition transition = new Transition(transitionTime, (source != null ? source.getName() : "idle") + "_to_idle", null);

        if (source != null) {
            Animation.TransitionFactory transitionFactory = source.getTransitionFactory();
            transition.options = transitionFactory.createBoneOptions(DUMMY_ANIMATION, model, existingTime, transitionTime);
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
        Animation.TransitionFactory sourceTFactory = source.getTransitionFactory();

        List<TransitionBoneOption> options = sourceTFactory.createBoneOptions(dest, model, existingTime, transitionTime);
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
                    TimeModelRenderer piece = model.getPiece(boneOption.name);

                    if (piece != null) {
                        boneOption.apply(piece, layer, existingTime);
                    } else {
                        TimeCore.LOGGER.error("Can't find bone with name " + boneOption.name + " for transition " + getName() + " applied for model " + model.getName());
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
    public @NotNull Animation.TransitionFactory getTransitionFactory() {
        return new TransitionFactory(this);
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
    public Animation getDestAnimation() {
        return destAnimation;
    }

    @Override
    public String toString() {
        return "Transition{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", transitionLength=" + transitionLength +
                ", destAnimation=" + destAnimation +
                '}';
    }

    private static class TransitionFactory extends Animation.TransitionFactory {

        public TransitionFactory(Transition source) {
            super(source);
        }

        private static KeyFrame calcStartKeyFrame(Animation sourceAnimation, @Nullable Pair<KeyFrame, KeyFrame> sourceKeyFrames, Vector3f modelIdleVec, int existingTime) {
            if (sourceKeyFrames != null) {
                Vector3f vec = BoneOption.calcCurrentVectorFor(sourceAnimation, sourceKeyFrames, modelIdleVec, existingTime);
                return new KeyFrame(0, vec);
            }

            return KeyFrame.createIdleKeyFrame(0, modelIdleVec);
        }

        @Override
        public java.util.@Nullable List<Transition.TransitionBoneOption> createBoneOptions(Animation dest, ITimeModel model, int existingTime, int transitionTime) {
            Transition source = getSourceTyped();
            if (source.options == null || source.options.isEmpty()) {
                return null;
            }

            Animation.TransitionFactory destFactory = dest.getTransitionFactory();

            List<Transition.TransitionBoneOption> transitionBones = new ArrayList<>();
            source.options.forEach(sourceBone -> {
                TimeModelRenderer piece = model.getPiece(sourceBone.name);
                if (piece != null) {
                    // Rotations
                    KeyFrame startKeyFrame = calcStartKeyFrame(source, sourceBone.rotations, new Vector3f(0, 0, 0), existingTime);
                    KeyFrame endKeyFrame = destFactory.getDestKeyFrame(piece, sourceBone.name, OptionType.ROTATION, transitionTime);
                    Pair<KeyFrame, KeyFrame> rotations = Pair.of(startKeyFrame, endKeyFrame);

                    // Positions
                    startKeyFrame = calcStartKeyFrame(source, sourceBone.positions, piece.offset, existingTime);
                    endKeyFrame = destFactory.getDestKeyFrame(piece, sourceBone.name, OptionType.POSITION, transitionTime);
                    Pair<KeyFrame, KeyFrame> positions = Pair.of(startKeyFrame, endKeyFrame);

                    // Scales
                    startKeyFrame = calcStartKeyFrame(source, sourceBone.scales, piece.getScaleFactor(), existingTime);
                    endKeyFrame = destFactory.getDestKeyFrame(piece, sourceBone.name, OptionType.SCALE, transitionTime);
                    Pair<KeyFrame, KeyFrame> scales = Pair.of(startKeyFrame, endKeyFrame);

                    transitionBones.add(new Transition.TransitionBoneOption(sourceBone.name, rotations, positions, scales));
                }
            });

            return transitionBones;
        }

        @Override
        public @NotNull KeyFrame getDestKeyFrame(TimeModelRenderer piece, String boneName, OptionType optionType, int transitionTime) {
            throw new UnsupportedOperationException("This should never be reached. Transition shouldn't be set manually as an destination animation");
        }
    }

    public static class TransitionBoneOption {
        private final String name;

        private final Pair<KeyFrame, KeyFrame> rotations;
        private final Pair<KeyFrame, KeyFrame> positions;
        private final Pair<KeyFrame, KeyFrame> scales;

        public TransitionBoneOption(String boneName, Pair<KeyFrame, KeyFrame> rotations, Pair<KeyFrame, KeyFrame> positions, Pair<KeyFrame, KeyFrame> scales) {
            this.name = boneName;
            this.rotations = rotations;
            this.positions = positions;
            this.scales = scales;
        }

        private static Vector3f interpolate(KeyFrame start, KeyFrame end, int existingTime) {
            return BoneOption.interpolate(start.getVec(), end.getVec(), start.getStartTime(), end.getStartTime(), existingTime);
        }

        public void apply(TimeModelRenderer piece, AnimationLayer layer, int existingTime) {
            Vector3f interpolated = interpolate(rotations.getA(), rotations.getB(), existingTime);
            AnimationUtils.applyRotation(piece, layer, interpolated);

            interpolated = interpolate(positions.getA(), positions.getB(), existingTime);
            AnimationUtils.applyOffset(piece, layer, interpolated);

            interpolated = interpolate(scales.getA(), scales.getB(), existingTime);
            AnimationUtils.applyScale(piece, layer, interpolated);
        }
    }
}
