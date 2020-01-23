package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;

import java.util.ArrayList;

public class BlockStateResource extends JSONTimeResource {

    private ArrayList<Variant> variants = new ArrayList<>(1);

    @Override
    public String buildJSONString() {
        return object(null, listOf(
                object("variants", listOf(
                        () -> {
                            String[] vars = new String[variants.size()];
                            for (int i = 0; i < variants.size(); i++) {
                                Variant variant = variants.get(i);
                                vars[i] = object(variant.name, listOf(
                                        value("model", variant.model.toString())
                                ));
                            }

                            return vars;
                        }
                ))
        ));
    }

    public BlockStateResource addDefaultVariant(BlockModelLocation model) {
        boolean added = false;
        for (Variant variant : variants) {
            if (variant.name.equals("")) {
                variant.model = model;

                added = true;
                break;
            }
        }

        if (!added) {
            variants.add(new Variant("", model));
        }

        return this;
    }

    public BlockStateResource addVariant(Variant variant) {
        variants.add(variant);
        return this;
    }

    public static class Variant {
        private String name;
        private BlockModelLocation model;

        public Variant(String name, BlockModelLocation model) {
            this.name = name;
            this.model = model;
        }
    }
}
