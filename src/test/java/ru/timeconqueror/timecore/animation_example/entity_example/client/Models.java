package ru.timeconqueror.timecore.animation_example.entity_example.client;

import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.registry.TimeModelRegister;
import ru.timeconqueror.timecore.api.registry.util.AutoRegistrable;
import ru.timeconqueror.timecore.client.render.model.InFileLocation;

public class Models {
    @AutoRegistrable
    private static final TimeModelRegister REGISTER = new TimeModelRegister(TimeCore.MODID);

    public static final InFileLocation FLORO = REGISTER.register("models/entity/floro.json");
}
