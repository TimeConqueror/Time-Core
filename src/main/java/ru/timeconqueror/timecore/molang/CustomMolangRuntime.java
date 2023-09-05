package ru.timeconqueror.timecore.molang;

import gg.moonflower.molangcompiler.api.MolangRuntime;
import gg.moonflower.molangcompiler.core.object.MolangVariableStorage;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.HashMap;

@SuppressWarnings("UnstableApiUsage")
public class CustomMolangRuntime extends MolangRuntime {
    public CustomMolangRuntime() {
        super(Object2ObjectOpenHashMap::new, HashMap::new, new MolangVariableStorage(true), new MolangVariableStorage(true), new MolangVariableStorage(false));
    }

    @Override
    public void unloadLibrary(String name) {
        objects.remove(name);
    }
}
