package ru.timeconqueror.timecore.molang;

import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.object.MolangLibrary;

import java.util.Map;
import java.util.function.BiConsumer;

public class MolangLibraryImpl extends MolangLibrary {
    private final String name;

    public MolangLibraryImpl(String name, Map<String, MolangExpression> values) {
        super(values);
        this.name = name;
    }

    @Override
    protected void populate(BiConsumer<String, MolangExpression> biConsumer) {

    }

    @Override
    protected String getName() {
        return name;
    }
}
