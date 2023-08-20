package ru.timeconqueror.timecore.molang;

import gg.moonflower.molangcompiler.api.MolangExpression;
import gg.moonflower.molangcompiler.api.object.MolangLibrary;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

import java.util.Map;
import java.util.function.BiConsumer;

public class MolangSharedQuery extends MolangLibrary {
    private final String queryName;

    public MolangSharedQuery(Map<String, MolangExpression> values, String queryName) {
        super(values);
        this.queryName = queryName;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    protected void populate(BiConsumer<String, MolangExpression> biConsumer) {

    }

    @Override
    public String getName() {
        return queryName;
    }

    public static class Builder {
        private final Map<String, MolangExpression> map = new Object2ObjectOpenHashMap<>();

        public Builder add(String name, MolangExpression expression) {
            map.put(name, expression);
            return this;
        }

        public MolangSharedQuery build(String name) {
            return new MolangSharedQuery(map, name);
        }
    }
}
