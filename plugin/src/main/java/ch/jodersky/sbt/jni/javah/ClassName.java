package ch.jodersky.sbt.jni.javah;

import java.util.Objects;

import static ch.jodersky.sbt.jni.javah.util.Utils.*;

public final class ClassName {
    private final String moduleName;
    private final String className;
    private final String simpleName;
    private final String mangledName;

    public static ClassName of(String moduleName, String className) {
        Objects.requireNonNull(className, "Class name is null");

        if (moduleName != null && !FULL_NAME_PATTERN.matcher(moduleName).matches()) {
            throw new IllegalArgumentException("Illegal module name: " + moduleName);
        }
        if (!FULL_NAME_PATTERN.matcher(className).matches()) {
            throw new IllegalArgumentException("Illegal class name: " + moduleName);
        }

        return new ClassName(moduleName, className);
    }

    /* Example: "java.base/java.lang.Object" */
    public static ClassName ofFullName(String fullName) {
        Objects.requireNonNull(fullName, "class name is null");
        int idx = fullName.indexOf('/');
        if (idx == -1) {
            return ClassName.of(null, fullName);
        }

        return ClassName.of(fullName.substring(0, idx), fullName.substring(idx + 1));
    }

    public static ClassName ofInternalName(String name) {
        return of(null, name.replace('/', '.'));
    }

    private ClassName(String moduleName, String className) {
        this.moduleName = moduleName;
        this.className = className;
        this.simpleName = className.substring(className.lastIndexOf('.') + 1);
        this.mangledName = mangleName(className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ClassName)) return false;
        ClassName className1 = (ClassName) o;
        return Objects.equals(moduleName, className1.moduleName) && className.equals(className1.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(moduleName, className);
    }

    @Override
    public String toString() {
        return moduleName == null ? className : moduleName + '/' + className;
    }


    //
    // Getters and Setters
    //

    public final String moduleName() {
        return moduleName;
    }

    public final String className() {
        return className;
    }

    public final String simpleName() {
        return simpleName;
    }

    public final String mangledName() {
        return mangledName;
    }

    public final String relativePath() {
        return className.replace('.', '/') + ".class";
    }
}
