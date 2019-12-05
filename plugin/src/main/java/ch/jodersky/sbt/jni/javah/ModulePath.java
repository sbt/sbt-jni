package ch.jodersky.sbt.jni.javah;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ModulePath implements SearchPath {
    private final Path path;
    private List<Path> roots;

    public ModulePath(Path path) {
        Objects.requireNonNull(path);
        path = path.toAbsolutePath();
        this.path = path;
        if (Files.notExists(path) || !Files.isDirectory(path)) {
            roots = Collections.emptyList();
        } else {
            try {
                roots = Files.list(path)
                        .map(Path::toAbsolutePath)
                        .filter(Files::isRegularFile)
                        .filter(p -> {
                            String n = p.getFileName().toString().toLowerCase();
                            return n.endsWith(".jar") || n.endsWith(".zip") || n.endsWith(".jmod");
                        })
                        .map(Utils::classPathRoot)
                        .filter(Objects::nonNull)
                        .flatMap(p -> SearchPath.multiReleaseRoots(p).stream())
                        .collect(Collectors.toList());
            } catch (IOException e) {
                roots = Collections.emptyList();
            }
        }
    }

    @Override
    public Path search(ClassName name) {
        Objects.requireNonNull(name);
        return SearchPath.searchFromRoots(roots, name);
    }

    @Override
    public String toString() {
        return "ModulePath[" + path + "]";
    }
}
