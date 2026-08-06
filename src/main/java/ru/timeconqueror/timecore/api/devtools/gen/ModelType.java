package ru.timeconqueror.timecore.api.devtools.gen;

public record ModelType(String folderName) {
    public static final ModelType ITEM = new ModelType("item");
    public static final ModelType BLOCK = new ModelType("block");
}
