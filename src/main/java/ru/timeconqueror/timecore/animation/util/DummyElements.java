package ru.timeconqueror.timecore.animation.util;

import net.minecraft.entity.Entity;
import ru.timeconqueror.timecore.api.client.render.model.TimeEntityModel;
import ru.timeconqueror.timecore.client.render.model.TimeModelLoader;

public class DummyElements {
    public static TimeEntityModel<Entity> DUMMY_ENTITY_MODEL = new TimeEntityModel<>(TimeModelLoader.BROKEN_MODEL.get(0));
}
