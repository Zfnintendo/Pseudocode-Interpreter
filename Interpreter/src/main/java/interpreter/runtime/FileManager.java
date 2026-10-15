package interpreter.runtime;

import ast.FileMode;

import java.io.Closeable;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.io.BufferedWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class FileManager implements AutoCloseable {

    private record OpenFile(FileMode mode, Closeable stream) {}

    private final Map<Path, OpenFile> files = new HashMap<>();

    private Path key(String filename) {
        return Path.of(filename).toAbsolutePath().normalize();
    }

    public void open(String filename, FileMode mode) {
        Path path = key(filename);

        if (files.containsKey(path)) {
            throw new IllegalStateException("File is already open: " + filename);
        }

        try {
            Closeable stream = switch(mode) {
                case READ -> Files.newBufferedReader(path, StandardCharsets.UTF_8);
                case WRITE -> Files.newBufferedWriter(path, StandardCharsets.UTF_8);
            };

            files.put(path, new OpenFile(mode, stream));
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not open file: " + filename, exception);
        }
    }

    public void close(String filename) {
        Path path = key(filename);
        OpenFile file = files.get(path);

        if (file == null) {
            throw new IllegalStateException("File is not open: " + filename);
        }

        try {
            file.stream().close();
            files.remove(path);
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not close file: " + filename, exception);
        }
    }

    @Override
    public void close() {
        IOException failure = null;

        for (OpenFile file : files.values()) {
            try {
                file.stream().close();
            } catch (IOException exception) {
                if (failure == null) {
                    failure = exception;
                } else {
                    failure.addSuppressed(exception);
                }
            }
        }

        files.clear();

        if (failure != null) {
            throw new UncheckedIOException("Could not close all files.", failure);
        }
    }

    public void writeLine(String filename, String text) {
        OpenFile file = requireFile(filename, FileMode.WRITE);
        BufferedWriter writer = (BufferedWriter) file.stream();

        try {
            writer.write(text);
            writer.newLine();
        } catch (IOException exception) {
            throw new UncheckedIOException("Could not write to file: " + filename, exception);
        }
    }

    private OpenFile requireFile(String filename, FileMode requiredMode) {
        OpenFile file = files.get(key(filename));

        if (file == null) {
            throw new IllegalStateException("File is not open: " + filename);
        }

        if (file.mode() != requiredMode) {
            throw new IllegalStateException("File must be opened for " + requiredMode + ": " + filename);
        }

        return file;
    }

}
