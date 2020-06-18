package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import ru.timeconqueror.timecore.api.animation.StateMachine;
import ru.timeconqueror.timecore.util.SingleUseBuilder;

import java.util.Objects;

public class StateMachineBuilder<T extends Entity> extends SingleUseBuilder {
    private final AnimationManagerBuilder animationManagerBuilder;

    public StateMachineBuilder(AnimationManagerBuilder animationManagerBuilder) {
        this.animationManagerBuilder = animationManagerBuilder;
    }

    public StateMachine<T> build(T entity, @NotNull World world) {
        Objects.requireNonNull(world);
        verifyNotUsed();

        BaseAnimationManager animationManager = animationManagerBuilder.build(!world.isRemote);

        StateMachine<T> stateMachine = new StateMachineImpl<>(animationManager, entity);

        animationManagerBuilder.init(animationManager, stateMachine);

        setUsed();
        return stateMachine;
    }
}
