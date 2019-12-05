package ch.jodersky.sbt.jni.javah;

import java.io.IOException;
import java.net.URI;
import java.nio.file.*;
import java.util.Collections;
import java.util.Objects;

public class RuntimeSearchPath implements SearchPath {
    public static final RuntimeSearchPath INSTANCE = new RuntimeSearchPath();

    private RuntimeSearchPath() {

    }

    @Override
    public Path search(ClassName name) {
        Objects.requireNonNull(name);
        URI uri = null;
        try {
            Class<?> cls = Class.forName(name.className());
            uri = cls.getResource(name.simpleName() + ".class").toURI();
            return Paths.get(uri);
        } catch (FileSystemNotFoundException ex) {
            if (uri == null) {
                return null;
            }
            try {
                return FileSystems.newFileSystem(uri, Collections.emptyMap()).getPath("/", name.relativePath());
            } catch (IOException | NullPointerException ignored) {
            }
        } catch (Exception ignored) {
        }
        return null;
    }

    public static Path searchClass(String name) {
        return INSTANCE.search(name);
    }

    public static Path searchClass(ClassName name) {
        return INSTANCE.search(name);
    }
}
