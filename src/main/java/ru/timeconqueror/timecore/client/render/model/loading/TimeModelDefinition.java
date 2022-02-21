package ru.timeconqueror.timecore.client.render.model.loading;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import ru.timeconqueror.timecore.client.render.model.TimeModelPart;

@OnlyIn(Dist.CLIENT)
public class TimeModelDefinition {
    private final TimeMeshDefinition mesh;
    private final MaterialDefinition material;

    private TimeModelDefinition(TimeMeshDefinition mesh, MaterialDefinition material) {
        this.mesh = mesh;
        this.material = material;
    }

    public TimeModelPart bakeRoot() {
        MaterialDefinition material = this.material;
        return this.mesh.getRoot().bake(null, material);
    }

    public static TimeModelDefinition create(TimeMeshDefinition mesh, MaterialDefinition material) {
        return new TimeModelDefinition(mesh, material);
    }
}
