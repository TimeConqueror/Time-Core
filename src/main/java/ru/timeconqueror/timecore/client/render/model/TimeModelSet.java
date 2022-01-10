package ru.timeconqueror.timecore.client.render.model;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.loading.JsonModelParser;
import ru.timeconqueror.timecore.client.render.model.loading.TimeModelDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TimeModelSet implements ResourceManagerReloadListener {
    //TODO make TimeModelRegister#register be called upon reload!
    public static final Set<TimeModelLocation> MODELS = ConcurrentHashMap.newKeySet();

    private Map<TimeModelLocation, TimeModelDefinition> roots = ImmutableMap.of();

    public ModelPart bakeDefinition(TimeModelLocation location) {
        TimeModelDefinition definition = this.roots.get(location);
        if (definition == null) {
            throw new IllegalArgumentException("No model was found with location " + location);
        } else {
            return definition.bakeRoot();
        }
    }

    /**
     * Performs the reload in the apply executor, or the game engine.
     *
     * @param resourceManager the resource manager
     */
    public void onResourceManagerReload(ResourceManager resourceManager) {
        JsonModelParser parser = new JsonModelParser();

        Map<TimeModelLocation, TimeModelDefinition> roots = new HashMap<>();

        for (TimeModelLocation model : MODELS) {
            if (roots.containsKey(model)) {
                continue;
            }

            List<Pair<TimeModelLocation, TimeModelDefinition>> pairs = parser.parseGeometryFile(model.location());
            if (model.modelName().equals(TimeModelLocation.SINGLE_MODEL_MASK)) {
                if (pairs.size() != 1) {
                    throw new IllegalStateException(String.format("File was defined by model location as a holder of single model definition, but found %s (%s)",
                            pairs.size(),
                            mapToModelNames(pairs)
                    ));
                }

                Pair<TimeModelLocation, TimeModelDefinition> pair = pairs.get(0);

                if (!model.equals(pair.left())) {
                    throw new IllegalStateException("Tried to find " + model.modelName() + " in file " + model + " but found " + pair.left().modelName());
                }

                roots.put(pair.left(), pair.right());
            } else {
                boolean found = false;

                for (Pair<TimeModelLocation, TimeModelDefinition> pair : pairs) {
                    if (pair.left().equals(model)) found = true;

                    roots.put(pair.left(), pair.right());
                }

                if (!found) {
                    throw new IllegalStateException("Tried to find " + model.modelName() + " in file " + model + " but found " + mapToModelNames(pairs));
                }
            }
        }

        this.roots = roots;
    }

    private String mapToModelNames(List<Pair<TimeModelLocation, TimeModelDefinition>> definitions) {
        return definitions.stream().map(pair -> pair.left().modelName()).collect(Collectors.joining());
    }
}
