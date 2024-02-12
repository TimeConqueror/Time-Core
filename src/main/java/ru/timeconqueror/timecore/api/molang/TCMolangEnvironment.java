package ru.timeconqueror.timecore.api.molang;

import gg.moonflower.molangcompiler.api.MolangEnvironment;
import ru.timeconqueror.timecore.molang.MolangRuntimeProperties;

public interface TCMolangEnvironment extends MolangEnvironment {
    MolangRuntimeProperties getRuntimeProperties();
}
