package ch.jodersky.sbt.jni.javah.util;

import ch.jodersky.sbt.jni.javah.ClassName;
import org.objectweb.asm.*;

import java.util.*;

public class ClassMetaInfo extends ClassVisitor {
    public final List<Constant> constants = new LinkedList<>();
    public final List<NativeMethod> methods = new LinkedList<>();
    public final Map<String, Integer> counts = new HashMap<>();

    ClassName superClassName;
    ClassName name;

    public ClassMetaInfo() {
        super(Opcodes.ASM7);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superClassName = superName == null ? null : ClassName.ofInternalName(superName);
        this.name = ClassName.ofInternalName(name);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        counts.put(name, counts.getOrDefault(name, 0) + 1);
        if ((access & Opcodes.ACC_NATIVE) != 0) {
            this.methods.add(NativeMethod.of(access, name, descriptor));
        }
        return null;
    }

    @Override
    public FieldVisitor visitField(int access, String name, String descriptor, String signature, Object value) {
        if (value != null && !(value instanceof String)) {
            constants.add(Constant.of(name, value));
        }
        return null;
    }

    public boolean isOverloadMethod(NativeMethod method) {
        Objects.requireNonNull(method);
        return counts.getOrDefault(method.name(), 1) > 1;
    }
}
