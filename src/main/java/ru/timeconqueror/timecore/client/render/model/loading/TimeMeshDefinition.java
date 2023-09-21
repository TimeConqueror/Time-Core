package ru.timeconqueror.timecore.client.render.model.loading;

import lombok.Getter;

@Getter
public class TimeMeshDefinition {
    private final TimePartDefinition root = TimePartDefinition.makeRoot();
}