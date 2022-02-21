package ru.timeconqueror.timecore.client.render.model;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.ResourceLocation;

import java.util.function.Function;

public class ModelConfiguration {
    private final TimeModelLocation location;
    private final Function<ResourceLocation, RenderType> renderTypeProvider;
    private final float scale;

    public ModelConfiguration(TimeModelLocation location, Function<ResourceLocation, RenderType> renderTypeProvider, float scale) {
        this.location = location;
        this.renderTypeProvider = renderTypeProvider;
        this.scale = scale;
    }

    public Function<ResourceLocation, RenderType> renderTypeProvider() {
        return renderTypeProvider;
    }

    public TimeModelLocation location() {
        return location;
    }

    public float scale() {
        return scale;
    }

    public static Builder builder(TimeModelLocation location) {
        return new Builder(location);
    }

    public static class Builder {
        private final TimeModelLocation location;
        private Function<ResourceLocation, RenderType> renderTypeProvider = RenderType::entityCutoutNoCull;
        private float scale = 1.0F;

        private Builder(TimeModelLocation location) {
            this.location = location;
        }

        /**
         * @param renderTypeProvider RenderType, which determines the settings of how model will be rendered depending on the provided texture location
         */
        public Builder withRenderType(Function<ResourceLocation, RenderType> renderTypeProvider) {
            this.renderTypeProvider = renderTypeProvider;
            return this;
        }

        /**
         * Set custom scale for the model
         */
        public Builder scaled(float scale) {
            this.scale = scale;
            return this;
        }

        public ModelConfiguration build() {
            return new ModelConfiguration(location, renderTypeProvider, scale);
        }
    }
}