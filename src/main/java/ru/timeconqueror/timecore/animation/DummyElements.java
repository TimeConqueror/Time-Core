package ru.timeconqueror.timecore.animation;

import net.minecraft.entity.Entity;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;

public class DummyElements {
    public static TimeEntityModel<Entity> DUMMY_ENTITY_MODEL = new TimeEntityModel<>(AnimationLoader.BROKEN_MODEL.get(0));
}
