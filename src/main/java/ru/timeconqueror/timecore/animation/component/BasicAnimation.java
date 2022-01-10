package ru.timeconqueror.timecore.animation.component;

import com.mojang.math.Vector3f;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.api.animation.Animation;
import ru.timeconqueror.timecore.api.animation.AnimationLayer;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BasicAnimation extends Animation {
    private final boolean loop;
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

    public BasicAnimation(boolean loop, ResourceLocation id, String name, int length, @Nullable Map<String, BoneOption> options) {
        this.loop = loop;
        this.name = name;
        this.id = id;
        this.length = length;
        this.options = options;
    }

    public void apply(ITimeModel model, AnimationLayer layer, int existingTime) {
        if (options != null) {
            if (existingTime <= length) {
                options.forEach((s, boneOption) -> {
                    TimeModelPart piece = model.getPiece(boneOption.getName());

                    if (piece != null) {
                        boneOption.apply(this, layer, piece, existingTime);
                    } else {
                        TimeCore.LOGGER.error("Can't find bone with location " + boneOption.getName() + " in animation " + getName() + " applied for model " + model.getName());
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

    public boolean isLooped() {
        return loop;
    }

    public @Nullable Map<String, BoneOption> getOptions() {
        return options;
    }

    @Override
    public @NotNull Animation.TransitionFactory getTransitionFactory() {
        return new TransitionFactory(this);
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
                List<KeyFrame> positions = null;
                if (boneOption.getPositions() != null) {
                    positions = reverseKeyFrames(boneOption.getPositions());
                }

                List<KeyFrame> rotations = null;
                if (boneOption.getRotations() != null) {
                    rotations = reverseKeyFrames(boneOption.getRotations());
                }

                List<KeyFrame> scales = null;
                if (boneOption.getScales() != null) {
                    scales = reverseKeyFrames(boneOption.getScales());
                }

                reversedOptions.put(boneOption.getName(), new BoneOption(boneOption.getName(), rotations, positions, scales));
            });
        }

        return new BasicAnimation(loop, new ResourceLocation(id.getNamespace(), id.getPath() + "-reversed"), name + "-reversed", length, reversedOptions);
    }

    private List<KeyFrame> reverseKeyFrames(List<KeyFrame> keyFrames) {
        return keyFrames.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(KeyFrame::getTime)))
                .map(keyFrame -> new KeyFrame(length - keyFrame.getTime(), keyFrame.getVec()))
                .collect(Collectors.toList());
    }

    public static class TransitionFactory extends Animation.TransitionFactory {
        public TransitionFactory(BasicAnimation source) {
            super(source);
        }

        private static KeyFrame calcStartKeyFrame(BasicAnimation sourceAnimation, @Nullable List<KeyFrame> sourceKeyFrames, Vector3f modelIdleVec, int existingTime) {
            if (sourceKeyFrames != null) {
                Vector3f vec = KeyFrameInterpolator.findInterpolationVec(sourceAnimation, sourceKeyFrames, modelIdleVec, existingTime);
                if (vec != null) return new KeyFrame(0, vec);
            }

            return KeyFrame.createIdleKeyFrame(0, modelIdleVec);
        }

        private static KeyFrame calcEndKeyFrame(@Nullable List<KeyFrame> destKeyFrames, Vector3f modelIdleVec, int transitionTime /*may cause flicking? maybe -1?*/) {
            if (destKeyFrames != null && !destKeyFrames.isEmpty()) {
                KeyFrame keyFrame = destKeyFrames.get(0);
                if (keyFrame.getTime() == 0) {
                    return keyFrame.withNewTime(transitionTime);
                }
            }

            return KeyFrame.createIdleKeyFrame(transitionTime, modelIdleVec);
        }

        @Override
        public @Nullable List<Transition.BoneOption> createBoneOptions(Animation dest, ITimeModel model, int existingTime, int transitionTime) {
            BasicAnimation source = getSourceTyped();
            if (source.getOptions() == null || source.getOptions().isEmpty()) {
                return null;
            }

            Animation.TransitionFactory destFactory = dest.getTransitionFactory();

            List<Transition.BoneOption> transitionBones = new ArrayList<>();
            source.getOptions().forEach((name, sourceBone) -> {
                TimeModelPart piece = model.getPiece(name);
                if (piece != null) {
                    // Rotations
                    KeyFrame startKeyFrame = calcStartKeyFrame(source, sourceBone.getRotations(), new Vector3f(0, 0, 0), existingTime);
                    KeyFrame endKeyFrame = destFactory.getDestKeyFrame(piece, name, OptionType.ROTATION, transitionTime);
                    Pair<KeyFrame, KeyFrame> rotations = Pair.of(startKeyFrame, endKeyFrame);

                    // Positions
                    startKeyFrame = calcStartKeyFrame(source, sourceBone.getPositions(), piece.offset, existingTime);
                    endKeyFrame = destFactory.getDestKeyFrame(piece, name, OptionType.POSITION, transitionTime);
                    Pair<KeyFrame, KeyFrame> positions = Pair.of(startKeyFrame, endKeyFrame);

                    // Scales
                    startKeyFrame = calcStartKeyFrame(source, sourceBone.getScales(), piece.getScaleFactor(), existingTime);
                    endKeyFrame = destFactory.getDestKeyFrame(piece, name, OptionType.SCALE, transitionTime);
                    Pair<KeyFrame, KeyFrame> scales = Pair.of(startKeyFrame, endKeyFrame);

                    transitionBones.add(new Transition.BoneOption(name, rotations, positions, scales));
                }
            });

            return transitionBones;
        }

        @Override
        public @NotNull KeyFrame getDestKeyFrame(TimeModelPart piece, String boneName, OptionType optionType, int transitionTime) {
            BasicAnimation dest = getSourceTyped();
            boolean destContainsSameBone = dest.getOptions() != null && dest.getOptions().containsKey(boneName);
            BoneOption destBone = destContainsSameBone ? dest.getOptions().get(boneName) : null;

            if (optionType == OptionType.ROTATION) {
                if (destBone != null) {
                    return calcEndKeyFrame(destBone.getRotations(), new Vector3f(0, 0, 0), transitionTime);
                } else {
                    return KeyFrame.createIdleKeyFrame(transitionTime, new Vector3f(0, 0, 0));
                }
            } else if (optionType == OptionType.POSITION) {
                if (destBone != null) {
                    return calcEndKeyFrame(destBone.getPositions(), piece.offset, transitionTime);
                } else {
                    return KeyFrame.createIdleKeyFrame(transitionTime, piece.offset);
                }
            } else if (optionType == OptionType.SCALE) {
                if (destBone != null) {
                    return calcEndKeyFrame(destBone.getScales(), piece.getScaleFactor(), transitionTime);
                } else {
                    return KeyFrame.createIdleKeyFrame(transitionTime, piece.getScaleFactor());
                }
            }

            throw new UnsupportedOperationException("Can't handle " + optionType + " option type");
        }
    }

    @Override
    public String toString() {
        return "BasicAnimation{" +
                "location=" + name +
                ", id=" + id +
                ", looped=" + loop +
                ", length=" + length +
                '}';
    }
}
