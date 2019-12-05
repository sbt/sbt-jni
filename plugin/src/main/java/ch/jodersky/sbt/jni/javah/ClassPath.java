package ch.jodersky.sbt.jni.javah;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClassPath implements SearchPath {
    private final Path path;
    private final List<Path> roots;

    public ClassPath(Path path) {
        Objects.requireNonNull(path);
        this.path = path.toAbsolutePath();

        Path root = Utils.classPathRoot(path);
        roots = root == null ? Collections.emptyList() : multiReleaseRoots(root);
    }

    @Override
    public Path search(ClassName name) {
        Objects.requireNonNull(name);
        return searchFromRoots(roots, name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassPath classPath = (ClassPath) o;
        return Objects.equals(path, classPath.path);
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }

    @Override
    public String toString() {
        return "ClassPath[" + path + "]";
    }
}
