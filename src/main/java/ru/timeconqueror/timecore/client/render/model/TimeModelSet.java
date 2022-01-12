package ru.timeconqueror.timecore.client.render.model;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.loading.JsonModelParser;
import ru.timeconqueror.timecore.client.render.model.loading.TimeModelDefinition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TimeModelSet implements ResourceManagerReloadListener {
    //TODO make TimeModelRegister#register be called upon reload!
    private final Set<TimeModelLocation> MODELS = ConcurrentHashMap.newKeySet();

    private Map<TimeModelLocation, TimeModelDefinition> roots = ImmutableMap.of();

    public TimeModelPart bakeRoot(TimeModelLocation location) {
        TimeModelDefinition definition = this.roots.get(location);
        if (definition == null) {
            throw new IllegalArgumentException("No model was found with location " + location);
        } else {
            return definition.bakeRoot();
        }
    }

    public void regModelLocation(TimeModelLocation location) {
        MODELS.add(location);
    }

    public void regModelLocations(Collection<TimeModelLocation> locations) {
        MODELS.addAll(locations);
    }

    /**
     * Performs the reload in the apply executor, or the game engine.
     *
     * @param resourceManager the resource manager
     */
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        TimeCore.LOGGER.debug("Loading models from TimeModelSet...");
        Stopwatch watch = Stopwatch.createStarted();

        JsonModelParser parser = new JsonModelParser();

        Map<TimeModelLocation, TimeModelDefinition> roots = new HashMap<>();

        for (TimeModelLocation model : MODELS) {
            if (roots.containsKey(model)) {
                continue;
            }

            List<Pair<TimeModelLocation, TimeModelDefinition>> pairs = parser.parseGeometryFile(model.location());
            if (model.isWildcard() && pairs.size() != 1) {
                throw new IllegalStateException(String.format("Expected to find single model in file %s but found %d ('%s)'", model, pairs.size(), mapToModelNames(pairs)));
            }

            if (pairs.size() == 1) {
                Pair<TimeModelLocation, TimeModelDefinition> pair = pairs.get(0);
                roots.put(pair.left(), pair.right());
                roots.put(TimeModelLocation.wildcarded(pair.left().location()), pair.right());
                continue;
            }


            boolean found = false;

            for (Pair<TimeModelLocation, TimeModelDefinition> pair : pairs) {
                if (pair.left().equals(model)) found = true;

                roots.put(pair.left(), pair.right());
            }

            if (!found) {
                throw new IllegalStateException("Tried to find model '" + model.modelName() + "' in file " + model + " but found '" + mapToModelNames(pairs) + "'");
            }
        }

        this.roots = roots;

        TimeCore.LOGGER.debug("Loading TimeModelSet took {}", watch);
    }

    private String mapToModelNames(List<Pair<TimeModelLocation, TimeModelDefinition>> definitions) {
        return definitions.stream().map(pair -> pair.left().modelName()).collect(Collectors.joining());
    }
}
