package ru.timeconqueror.timecore.api.client.resource;

import ru.timeconqueror.timecore.api.client.resource.location.BlockModelLocation;

import java.util.ArrayList;
//TODO what if store only blockmodel locations, but not all 100+ lines of blockstate

/**
 * See {@link BlockStateResources} for common blockstates.
 */
public class BlockStateResource extends JSONTimeResource {

    private final String json;

    private BlockStateResource(String json) {
        this.json = json;
    }

    public static BlockStateResource fromJson(String json) {
        return new BlockStateResource(json);
    }

    public static BlockStateResource fromBuilder(Builder builder) {
        return builder.build();
    }

    @Override
    public String toJson() {
        return json;
    }

    public static class Variant {
        /**
         * variant's location
         * Examples: "north=true"
         */
        private final String name;
        private BlockModelLocation model;

        public Variant(String name, BlockModelLocation model) {
            this.name = name;
            this.model = model;
        }
    }

    public static class Builder {
        private final ArrayList<Variant> variants = new ArrayList<>(1);

        public static Builder create() {
            return new Builder();
        }

        /**
         * Adds provided model to the default variant.
         *
         * @return this resource.
         */
        public Builder addDefaultVariant(BlockModelLocation model) {
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

        /**
         * Adds special variant to the resource
         *
         * @return this resource.
         */
        public Builder addVariant(Variant variant) {
            variants.add(variant);
            return this;
        }

        public BlockStateResource build() {
            return fromJson(buildJson());
        }

        private String buildJson() {
            return object(null, listOf(
                    object("variants", listOf(
                            () -> {
                                String[] vars = new String[variants.size()];
                                for (int i = 0; i < variants.size(); i++) {
                                    Variant variant = variants.get(i);
                                    vars[i] = object(variant.name, listOf(
                                            property("model", variant.model.toString())
                                    ));
                                }

                                return vars;
                            }
                    ))
            ));
        }
    }
}
