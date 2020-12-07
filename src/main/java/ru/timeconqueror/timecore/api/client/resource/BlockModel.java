package ru.timeconqueror.timecore.api.client.resource;

/**
 * Code analog to simple block model json files.
 * Contains some static methods for its creating.
 * <p>
 * See {@link BlockModels} for common models.
 */
public class BlockModel extends JSONTimeResource {
    private final String jsonString;

    public BlockModel(String jsonString) {
        this.jsonString = jsonString;
    }

    @Override
    public String toJson() {
        return jsonString;
    }
}