package ch.jodersky.sbt.jni.javah;

import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

public final class JavahTask {
    private final List<SearchPath> searchPaths = new LinkedList<>();
    private Path outputDir;
    private PrintWriter errorHandle = new PrintWriter(System.err, true);
    private final List<ClassName> classes = new LinkedList<>();

    public void run() {
        Objects.requireNonNull(outputDir, "outputDir");
        JNIGenerator generator = new JNIGenerator(outputDir, searchPaths, errorHandle);
        for (ClassName cls : classes) {
            try {
                generator.generate(cls);
            } catch (Exception ex) {
                ex.printStackTrace(errorHandle);
            }
        }
    }

    public void addClass(ClassName name) {
        Objects.requireNonNull(name);
        classes.add(name);
    }

    public void addClass(String name) {
        Objects.requireNonNull(name);
        classes.add(ClassName.of(name));
    }

    public void addClasses(Iterable<String> i) {
        Objects.requireNonNull(i);
        i.forEach(c -> classes.add(ClassName.of(c)));
    }

    public void addRuntimeSearchPath() {
        searchPaths.add(RuntimeSearchPath.INSTANCE);
    }

    public void addSearchPath(SearchPath searchPath) {
        Objects.requireNonNull(searchPath);
        searchPaths.add(searchPath);
    }

    public void addClassPath(Path classPath) {
        Objects.requireNonNull(classPath);
        searchPaths.add(new ClassPath(classPath));
    }

    public void addModulePath(Path modulePath) {
        Objects.requireNonNull(modulePath);
        searchPaths.add(new ModulePath(modulePath));
    }

    public Path getOutputDir() {
        return outputDir;
    }

    public void setOutputDir(Path outputDir) {
        this.outputDir = outputDir;
    }

    public PrintWriter getErrorHandle() {
        return errorHandle;
    }

    public void setErrorHandle(Writer errorHandle) {
        if (errorHandle instanceof PrintWriter || errorHandle == null) {
            this.errorHandle = (PrintWriter) errorHandle;
        } else {
            this.errorHandle = new PrintWriter(errorHandle);
        }
    }
}
