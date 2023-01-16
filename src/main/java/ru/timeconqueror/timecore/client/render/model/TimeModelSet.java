package ru.timeconqueror.timecore.client.render.model;

import com.google.common.base.Stopwatch;
import com.google.common.collect.ImmutableMap;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import ru.timeconqueror.timecore.TimeCore;
import ru.timeconqueror.timecore.api.util.Pair;
import ru.timeconqueror.timecore.client.render.model.loading.JsonModelParser;
import ru.timeconqueror.timecore.client.render.model.loading.TimeModelDefinition;
import ru.timeconqueror.timecore.internal.client.handlers.ClientLoadingHandler;
import ru.timeconqueror.timecore.mixins.accessor.client.ClientModLoaderAccessor;

import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TimeModelSet implements ResourceManagerReloadListener {
    //TODO make TimeModelRegister#register be called upon reload!
    private final List<WeakReference<ReloadListener>> reloadListeners = new ArrayList<>();
    private final Set<TimeModelLocation> modelLocations = ConcurrentHashMap.newKeySet();

    private Map<TimeModelLocation, TimeModelDefinition> roots = ImmutableMap.of();

    public TimeModelPart bakeRoot(TimeModelLocation location) {
        if (!modelLocations.contains(location)) {
            throw new IllegalArgumentException(String.format("Location '%s' was used before it was registered!", location));
        }

        TimeModelDefinition definition = this.roots.get(location);
        if (definition == null) {
            throw new IllegalArgumentException("No model was found with location " + location);
        } else {
            return definition.bakeRoot();
        }
    }

    public void regModelLocation(TimeModelLocation location) {
        modelLocations.add(location);
    }

    public void regModelLocations(Collection<TimeModelLocation> locations) {
        modelLocations.addAll(locations);
    }

    /**
     * Performs the reload in the apply executor, or the game engine.
     *
     * @param resourceManager the resource manager
     */
    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        TimeCore.LOGGER.debug("Loading models from TimeModelSet...");

        if (ClientModLoaderAccessor.getError() != null) {
            TimeCore.LOGGER.warn("Loading models will be skipped due to global mod loading error.");
            return;
        }

        Stopwatch watch = Stopwatch.createStarted();

        JsonModelParser parser = new JsonModelParser();

        Map<TimeModelLocation, TimeModelDefinition> roots = new HashMap<>();

        for (TimeModelLocation model : modelLocations) {
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

        reloadListeners();

        TimeCore.LOGGER.debug("Loading TimeModelSet took {}", watch);
    }

    private void reloadListeners() {
        TimeCore.LOGGER.debug("Reloading listeners...");
        Stopwatch w = Stopwatch.createStarted();

        reloadListeners.removeIf(ref -> ref.get() == null);

        for (WeakReference<ReloadListener> watchedListener : reloadListeners) {
            ReloadListener listener = watchedListener.get();
            if (listener != null) {
                listener.reload();
            }
        }

        TimeCore.LOGGER.debug("Reloading listeners took {}", w);
    }

    private String mapToModelNames(List<Pair<TimeModelLocation, TimeModelDefinition>> definitions) {
        return definitions.stream().map(pair -> pair.left().modelName()).collect(Collectors.joining());
    }

    public static abstract class ReloadListener {
        public ReloadListener() {
            ClientLoadingHandler.MODEL_SET.reloadListeners.add(new WeakReference<>(this));
        }

        abstract void reload();
    }
}
