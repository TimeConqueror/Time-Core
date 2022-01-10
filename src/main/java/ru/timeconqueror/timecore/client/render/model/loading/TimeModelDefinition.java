package ru.timeconqueror.timecore.client.render.model.loading;

import net.minecraft.client.model.geom.builders.MaterialDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;
import ru.timeconqueror.timecore.mixins.accessor.client.MaterialDefinitionAccessor;

@OnlyIn(Dist.CLIENT)
public class TimeModelDefinition {
    private final TimeMeshDefinition mesh;
    private final MaterialDefinition material;

    private TimeModelDefinition(TimeMeshDefinition mesh, MaterialDefinition material) {
        this.mesh = mesh;
        this.material = material;
    }

    public TimeModelPart bakeRoot() {
        MaterialDefinitionAccessor material = (MaterialDefinitionAccessor) this.material;
        return this.mesh.getRoot().bake(null, material.getTextureWidth(), material.getTextureHeight());
    }

    public static TimeModelDefinition create(TimeMeshDefinition mesh, int texWidth, int texHeight) {
        return new TimeModelDefinition(mesh, new MaterialDefinition(texWidth, texHeight));
    }
}
