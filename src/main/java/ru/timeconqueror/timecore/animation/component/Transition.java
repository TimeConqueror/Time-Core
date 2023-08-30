package ru.timeconqueror.timecore.animation.component;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import lombok.Getter;
import lombok.ToString;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.animation.AnimationStarter;
import ru.timeconqueror.timecore.animation.calculation.KeyFrameInterpolator;
import ru.timeconqueror.timecore.animation.util.AnimationUtils;
import ru.timeconqueror.timecore.animation.watcher.TimelineSnapshot;
import ru.timeconqueror.timecore.api.animation.*;
import ru.timeconqueror.timecore.api.client.render.model.ITimeModel;
import ru.timeconqueror.timecore.api.util.holder.Pair;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@ToString
public class Transition extends Animation {
    private final int transitionLength;
    @Getter
    private final String name;
    private final Animation destAnimation;
    @Getter
    private final ResourceLocation id;
    @ToString.Exclude
    private List<AnimationBone> options = new ArrayList<>();

    private Transition(int transitionLength, String name, Animation destAnimation) {
        this.transitionLength = transitionLength;
        this.name = name;
        this.id = new ResourceLocation(TimeCore.MODID, "internal/" + getName());
        this.destAnimation = destAnimation;
    }

    private static Transition createFromIdleState(AnimationStarter.AnimationData destinationData, MolangEnvironment env, ITimeModel model, int transitionTime) {
        Animation dest = destinationData.getAnimation();
        Transition transition = new Transition(transitionTime, "idle_to_" + dest.getName(), dest);

        TransitionFactoryWithDestination destFactory = dest.createTransitionFactory(env).withRequiredDestination();
        TimelineSnapshot destinationStartTime = TimelineSnapshot.createForStartTime(destinationData);

        dest.forEachBone(name -> {
            TimeModelPart part = model.tryGetPart(name);
            if (part != null) {
                Pair<IKeyFrame, IKeyFrame> rotations = TransitionFactory.makeTransitionPairFromIdle(part, destinationStartTime, name, Channel.ROTATION, destFactory, transitionTime);
                Pair<IKeyFrame, IKeyFrame> translations = TransitionFactory.makeTransitionPairFromIdle(part, destinationStartTime,name, Channel.TRANSLATION, destFactory, transitionTime);
                Pair<IKeyFrame, IKeyFrame> scales = TransitionFactory.makeTransitionPairFromIdle(part, destinationStartTime, name, Channel.SCALE, destFactory, transitionTime);
                transition.options.add(new AnimationBone(name, env, rotations, translations, scales));
            }
        });

        return transition;
    }

    public static Animation createForServer(Animation source, Animation dest, int transitionTime) {
        return new Transition(transitionTime, source.getName() + "_to_" + dest.getName(), dest);
    }

    public static Animation create(Animation source, AnimationStarter.AnimationData dest, MolangEnvironment env, ITimeModel model, int existingTime, int transitionTime) {
        TransitionFactory sourceTFactory = source.createTransitionFactory(env);

        List<AnimationBone> options = sourceTFactory.createTransitionBones(dest, model, existingTime, transitionTime);
        if (options == null) {
            return createFromIdleState(dest, env, model, transitionTime);
        }

        Animation destAnimation = dest.getAnimation();
        Transition transition = new Transition(transitionTime, source.getName() + "_to_" + destAnimation.getName(), destAnimation);
        transition.options = options;

        return transition;
    }

    @Override
    public void apply(ITimeModel model, ILayer layer, MolangEnvironment env, int existingTime) {
        if (options != null) {
            if (existingTime <= transitionLength) {
                options.forEach(animationBone -> {
                    TimeModelPart piece = model.tryGetPart(animationBone.name);

                    if (piece != null) {
                        animationBone.apply(piece, layer, existingTime);
                    }
                });
            }
        }
    }

    @Override
    public void forEachBone(Consumer<String> action) {
        options.forEach(transitionAnimationBone -> action.accept(transitionAnimationBone.name));
    }

    @Override
    public LoopMode getLoopMode() {
        return LoopMode.DO_NOT_LOOP;
    }

    @Override
    public @NotNull TransitionFactory createTransitionFactory(MolangEnvironment env) {
        return new InternalTransitionFactory(env, this);
    }

    @Override
    public int getLength() {
        return transitionLength;
    }

    public Animation getDestination() {
        return destAnimation;
    }

    private static class InternalTransitionFactory extends TransitionFactory {

        public InternalTransitionFactory(MolangEnvironment env, Transition source) {
            super(env, source);
        }

        private IKeyFrame calcStartKeyFrame(@Nullable Pair<IKeyFrame, IKeyFrame> sourceFrames, Vector3f modelIdleVec, int existingTime) {
            if (sourceFrames != null) {
                Vector3f vec = KeyFrameInterpolator.interpolateLinear(this.getEnv(), sourceFrames.left(), sourceFrames.right(), existingTime);
                return KeyFrame.createSimple(0, vec);
            }

            return KeyFrame.createSimple(0, modelIdleVec);
        }

        private Pair<IKeyFrame, IKeyFrame> makeTransitionPair(TimeModelPart part, TimelineSnapshot destinationStartTime, AnimationBone option, Channel channel, TransitionFactoryWithDestination destFactory, int existingTime, int transitionTime) {
            IKeyFrame startKeyFrame = calcStartKeyFrame(option.getKeyFrames(channel), channel.getDefaultVector(part), existingTime);
            IKeyFrame endKeyFrame = destFactory.getDestKeyFrame(part, destinationStartTime, option.getName(), channel, transitionTime);
            return Pair.of(startKeyFrame, endKeyFrame);
        }

        @Override
        public @Nullable List<AnimationBone> createTransitionBones(AnimationStarter.AnimationData dest, ITimeModel model, int existingTime, int transitionTime) {
            Transition source = getSourceTyped();
            if (source.options == null || source.options.isEmpty()) {
                return null;
            }

            TransitionFactoryWithDestination destFactory = dest.getAnimation().createTransitionFactory(getEnv()).withRequiredDestination();
            TimelineSnapshot destinationStartTime = TimelineSnapshot.createForStartTime(dest);

            List<AnimationBone> transitionBones = new ArrayList<>();
            source.options.forEach(bone -> {
                TimeModelPart part = model.tryGetPart(bone.name);
                if (part != null) {
                    Pair<IKeyFrame, IKeyFrame> rotations = makeTransitionPair(part, destinationStartTime, bone, Channel.ROTATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> translations = makeTransitionPair(part, destinationStartTime, bone, Channel.TRANSLATION, destFactory, existingTime, transitionTime);
                    Pair<IKeyFrame, IKeyFrame> scales = makeTransitionPair(part, destinationStartTime, bone, Channel.SCALE, destFactory, existingTime, transitionTime);
                    transitionBones.add(new AnimationBone(bone.name, getEnv(), rotations, translations, scales));
                }
            });

            return transitionBones;
        }
    }

    public static class AnimationBone {
        private final MolangEnvironment env;
        private final String name;

        private final Pair<IKeyFrame, IKeyFrame> rotations;
        private final Pair<IKeyFrame, IKeyFrame> positions;
        private final Pair<IKeyFrame, IKeyFrame> scales;

        public AnimationBone(String boneName, MolangEnvironment env, Pair<IKeyFrame, IKeyFrame> rotations, Pair<IKeyFrame, IKeyFrame> positions, Pair<IKeyFrame, IKeyFrame> scales) {
            this.name = boneName;
            this.env = env;
            this.rotations = rotations;
            this.positions = positions;
            this.scales = scales;
        }

        public void apply(TimeModelPart piece, ILayer layer, int existingTime) {
            Vector3f interpolated = KeyFrameInterpolator.interpolateLinear(env, rotations.left(), rotations.right(), existingTime);
            AnimationUtils.applyRotation(piece, layer, interpolated);

            interpolated = KeyFrameInterpolator.interpolateLinear(env, positions.left(), positions.right(), existingTime);
            AnimationUtils.applyOffset(piece, layer, interpolated);

            interpolated = KeyFrameInterpolator.interpolateLinear(env, scales.left(), scales.right(), existingTime);
            AnimationUtils.applyScale(piece, layer, interpolated);
        }

        public String getName() {
            return name;
        }

        public Pair<IKeyFrame, IKeyFrame> getKeyFrames(Channel channel) {
            if (channel == Channel.ROTATION) {
                return rotations;
            } else if (channel == Channel.TRANSLATION) {
                return positions;
            } else if (channel == Channel.SCALE) {
                return scales;
            }

            throw new IllegalArgumentException("Unknown channel: " + channel);
        }
    }
}
