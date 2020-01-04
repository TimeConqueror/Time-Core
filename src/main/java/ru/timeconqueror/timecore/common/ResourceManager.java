package ru.timeconqueror.timecore.common;

import com.google.common.base.Charsets;
import com.google.common.io.CharSource;
import com.google.common.io.Resources;
import net.minecraft.util.ResourceLocation;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;

@SuppressWarnings("UnstableApiUsage")
public class ResourceManager {
    private static final String PARENT_PATH = "assets/";

    /**
     * Returns the URL to resource with given {@code resourceLocation}.
     * It finds resource in path:
     * <p>mod.jar/assets/{@link ResourceLocation#getNamespace()}/{@link ResourceLocation#getPath()}
     * <p>
     * Returns null, if there is no resource with given {@code resourceLocation}.
     */
    public static URL getResourceURL(ResourceLocation resourceLocation) {
        return ResourceManager.class.getClassLoader().getResource(toPathString(resourceLocation));
    }

    /**
     * Returns the InputStream of resource with given {@code resourceLocation}.
     * It finds resource in path:
     * <p>mod.jar/assets/{@link ResourceLocation#getNamespace()}/{@link ResourceLocation#getPath()}
     * <p>
     * Returns null, if there is no resource with given {@code resourceLocation}.
     */
    public static InputStream getResourceStream(ResourceLocation resourceLocation) {
        return ResourceManager.class.getClassLoader().getResourceAsStream(toPathString(resourceLocation));
    }

    /**
     * Returns the CharSource of resource with given {@code resourceLocation} at UTF-8 encoding.
     * It finds resource in path:
     * <p>mod.jar/assets/{@link ResourceLocation#getNamespace()}/{@link ResourceLocation#getPath()}
     * <p>
     * Returns null, if there is no resource with given {@code resourceLocation}.
     */
    public static CharSource getResourceAsCharSource(ResourceLocation resourceLocation) {
        return getResourceAsCharSource(resourceLocation, Charsets.UTF_8);
    }

    /**
     * Returns the CharSource of resource with given {@code resourceLocation} at given encoding {@code charset}.
     * It finds resource in path:
     * <p>mod.jar/assets/{@link ResourceLocation#getNamespace()}/{@link ResourceLocation#getPath()}
     * <p>
     * Returns null, if there is no resource with given {@code resourceLocation}.
     */
    public static CharSource getResourceAsCharSource(ResourceLocation resourceLocation, Charset charset) {
        URL resourceURL = getResourceURL(resourceLocation);
        return resourceURL != null ? Resources.asCharSource(resourceURL, charset) : null;
    }

    private static String toPathString(ResourceLocation resourceLocation) {
        return PARENT_PATH + resourceLocation.getNamespace() + "/" + resourceLocation.getPath();
    }
}
