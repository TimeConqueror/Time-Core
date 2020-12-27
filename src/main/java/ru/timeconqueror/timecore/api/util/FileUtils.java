package ru.timeconqueror.timecore.api.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileUtils {
    public static void prepareFileForRead(File file) throws IOException {
        org.apache.commons.io.FileUtils.touch(file);

        if (!Files.isReadable(file.toPath())) {
            throw new IOException("File " + file + " is not readable!");
        }
    }

    public static void prepareFileForWrite(File file) throws IOException {
        org.apache.commons.io.FileUtils.touch(file);

        if (!Files.isWritable(file.toPath())) {
            throw new IOException("File " + file + " is not writable!");
        }
    }
}
