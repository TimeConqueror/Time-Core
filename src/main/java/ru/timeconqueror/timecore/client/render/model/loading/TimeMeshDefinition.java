package ru.timeconqueror.timecore.client.render.model.loading;

public class TimeMeshDefinition {
    private final TimePartDefinition root = TimePartDefinition.makeRoot();

    public TimePartDefinition getRoot() {
        return this.root;
    }
}