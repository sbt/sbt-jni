package ch.jodersky.sbt.jni.javah;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.util.Objects;
import java.util.regex.Matcher;

public final class NativeMethod {
    private final int access;
    private final String name;
    private final Type type;
    private final String mangledName;
    private final String longMangledName;

    public static NativeMethod of(String name, String descriptor) {
        return NativeMethod.of(0, name, descriptor);
    }

    public static NativeMethod of(String name, Type type) {
        return NativeMethod.of(0, name, type);
    }

    public static NativeMethod of(int access, String name, String descriptor) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(descriptor);
        return NativeMethod.of(access, name, Type.getType(descriptor));
    }

    public static NativeMethod of(int access, String name, Type type) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(type);
        if (!Utils.METHOD_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a qualified method name", name));
        }
        Matcher m = Utils.METHOD_TYPE_PATTERN.matcher(type.toString());
        if (!m.matches()) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a method type", type));
        }
        return new NativeMethod(access, name, type, m.group("args"));
    }

    private NativeMethod(int access, String name, Type type, String arguments) {
        this.access = access;
        this.name = name;
        this.type = type;
        this.mangledName = Utils.mangleName(name);
        this.longMangledName = mangledName + "__" + Utils.mangleName(arguments);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof NativeMethod)) {
            return false;
        }
        NativeMethod that = (NativeMethod) o;
        return name.equals(that.name) && type.equals(that.type);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, type);
    }

    @Override
    public String toString() {
        return String.format("NativeMethod[name=%s, type=%s}", name, type);
    }

    public String name() {
        return name;
    }

    public Type type() {
        return type;
    }

    public String mangledName() {
        return mangledName;
    }

    public String longMangledName() {
        return longMangledName;
    }

    public boolean isStatic() {
        return (access & Opcodes.ACC_STATIC) != 0;
    }
}
