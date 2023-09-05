package ru.timeconqueror.timecore.api.molang;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.exception.MolangRuntimeException;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

//TODO more
public class Molang {
    @Getter
    @Setter
    private static OnErrorBehaviour errorResolving = OnErrorBehaviour.CRASH_ON_ERROR;

    @SneakyThrows
    public static float resolve(MolangEnvironment env, MolangExpression exp) {
        return switch (getErrorResolving()) {
            case CRASH_ON_ERROR -> env.resolve(exp);
            case STUB_AND_PRINT_STACKTRACE_ON_ERROR -> env.safeResolve(exp);
            case SUPPRESS_ERROR -> {
                try {
                    //noinspection OverrideOnly
                    yield exp.get(env);
                } catch (MolangRuntimeException ignored) {
                    yield 0.0F;
                }
            }
        };
    }

    public enum OnErrorBehaviour {
        CRASH_ON_ERROR, STUB_AND_PRINT_STACKTRACE_ON_ERROR, SUPPRESS_ERROR
    }

    public static class Query {
        public static class Domains {
            public static final String ANIMATION = "animation";
        }

        @MolangQueryDomain(Domains.ANIMATION)
        public static class Animation {
            /**
             * <b>Bedrock Wiki</b>: Time in seconds since the current animation started, else 0.0 if not called within an animation.
             * <br>
             * <b>TimeCore clarifies</b>: Time in seconds from the start of the animation declared by its file.
             * The max value is the animation length.
             * If the animation is reversed, the anim_time will also go backwards.
             */
            public static final String ANIM_TIME = "anim_time";
        }
    }
}
