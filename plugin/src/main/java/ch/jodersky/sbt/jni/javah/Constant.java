package ch.jodersky.sbt.jni.javah;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Constant {
    private static final List<Class<?>> TYPES = Arrays.asList(
            Byte.class, Short.class, Integer.class, Long.class, Character.class, Float.class, Double.class
    );

    private final String name;
    private final Object value;
    private final String mangledName;

    public static Constant of(String name, Object value) {
        Objects.requireNonNull(name);
        Objects.requireNonNull(value);

        if (!TYPES.contains(value.getClass())) {
            throw new IllegalArgumentException();
        }
        if (!Utils.SIMPLE_NAME_PATTERN.matcher(name).matches()) {
            throw new IllegalArgumentException(String.format("\"%s\" is not a qualified constant name", name));
        }

        return new Constant(name, value);
    }

    private Constant(String name, Object value) {
        this.name = name;
        this.value = value;
        this.mangledName = Utils.mangleName(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Constant)) return false;
        Constant constant = (Constant) o;
        return name.equals(constant.name) && value.equals(constant.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, value);
    }

    @Override
    public String toString() {
        return String.format("Constant[name=%s, value=%s]", name, value);
    }

    public String name() {
        return name;
    }

    public Object value() {
        return value;
    }

    public String mangledName() {
        return mangledName;
    }

    public String valueToString() {
        if (value instanceof Double) {
            return value.toString();
        }
        if (value instanceof Float) {
            return value + "f";
        }
        if (value instanceof Long) {
            return value + "i64";
        }
        if (value instanceof Character) {
            return ((int) (char) value) + "L";
        }
        return value + "L";
    }
}
