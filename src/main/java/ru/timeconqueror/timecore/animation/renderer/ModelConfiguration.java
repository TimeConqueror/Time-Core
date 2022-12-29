package ru.timeconqueror.timecore.animation.renderer;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import ru.timeconqueror.timecore.client.render.model.TimeModelLocation;

import java.util.function.Function;

public record ModelConfiguration(TimeModelLocation location,
                                 Function<ResourceLocation, RenderType> renderTypeProvider) {
    public static Builder builder(TimeModelLocation location) {
        return new Builder(location);
    }

    public static class Builder {
        private final TimeModelLocation location;
        private Function<ResourceLocation, RenderType> renderTypeProvider = RenderType::entityCutoutNoCull;

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

        public ModelConfiguration build() {
            return new ModelConfiguration(location, renderTypeProvider);
        }
    }
}