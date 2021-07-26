package ch.jodersky.sbt.jni.javah.search;

import ch.jodersky.sbt.jni.javah.ClassName;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import static ch.jodersky.sbt.jni.javah.util.Utils.*;

public interface SearchPath {
    Path search(ClassName name);

    default Path search(String fullName) {
        Objects.requireNonNull(fullName);
        return search(ClassName.ofFullName(fullName));
    }

    static Path searchFrom(Iterable<SearchPath> searchPaths, ClassName name) {
        Objects.requireNonNull(searchPaths);
        Objects.requireNonNull(name);

        for (SearchPath searchPath : searchPaths) {
            if (searchPath == null) {
                continue;
            }
            Path p = searchPath.search(name);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    static Path searchFromRoots(Iterable<Path> roots, ClassName name) {
        Objects.requireNonNull(roots);
        Objects.requireNonNull(name);
        for (Path root : roots) {
            if (root == null || !Files.isDirectory(root)) {
                continue;
            }

            Path p = root.resolve(name.relativePath());
            if (Files.isRegularFile(p)) {
                return p;
            }
            if (Files.isSymbolicLink(p)) {
                try {
                    p = Files.readSymbolicLink(p);
                    if (Files.isRegularFile(p)) {
                        return p;
                    }
                } catch (IOException ignored) {
                }
            }
        }

        return null;
    }

    static List<Path> multiReleaseRoots(Path root) {
        Objects.requireNonNull(root);
        if (!Files.isDirectory(root)) {
            return Collections.emptyList();
        }
        boolean isMultiRelease = false;
        try (InputStream in = Files.newInputStream(root.resolve("META-INF").resolve("MANIFEST.MF"))) {
            isMultiRelease = "true".equals(new Manifest(in).getMainAttributes().getValue("Multi-Release"));
        } catch (IOException | NullPointerException ignored) {
        }

        if (isMultiRelease) {
            Path base = root.resolve("META-INF").resolve("versions");
            if (Files.isDirectory(base)) {
                try {
                    List<Path> list = Files.list(base)
                            .map(Path::toAbsolutePath)
                            .filter(Files::isDirectory)
                            .filter(p -> MULTI_RELEASE_VERSIONS.contains(p.getFileName().toString()))
                            .sorted(Comparator.comparing((Path p) -> Integer.parseInt(p.getFileName().toString())).reversed())
                            .collect(Collectors.toCollection(LinkedList::new));
                    list.add(root);
                    return Collections.unmodifiableList(list);
                } catch (IOException ignored) {
                }
            }
        }
        return Collections.singletonList(root);
    }
}
