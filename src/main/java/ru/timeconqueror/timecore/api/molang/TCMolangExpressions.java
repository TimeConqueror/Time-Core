package ru.timeconqueror.timecore.api.molang;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import gg.moonflower.molangcompiler.api.MolangExpression;
import lombok.extern.log4j.Log4j2;
import ru.timeconqueror.timecore.api.util.holder.ToFloatBiFunction;
import ru.timeconqueror.timecore.molang.MolangRuntimeProperties;

@Log4j2
public class TCMolangExpressions {
    public static MolangExpression usingRuntimeProperties(ToFloatBiFunction<MolangEnvironment, MolangRuntimeProperties> expression) {
        return molangEnvironment -> {
            if (molangEnvironment instanceof TCMolangEnvironment myEnv) {
                return expression.applyAsFloat(myEnv, myEnv.getRuntimeProperties());
            }

            Molang.OnErrorBehaviour errorBehaviour = Molang.getErrorResolving();
            if (errorBehaviour == Molang.OnErrorBehaviour.CRASH_ON_ERROR) {
                throw new IllegalArgumentException("Provided not a timecore molang environment");
            } else if (errorBehaviour == Molang.OnErrorBehaviour.STUB_AND_LOG_ON_ERROR) {
                log.debug("Provided not a timecore molang environment", new IllegalArgumentException());
            }

            return 0;
        };
    }
}
