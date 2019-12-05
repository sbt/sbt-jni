package ch.jodersky.sbt.jni.javah;

import org.objectweb.asm.*;

import java.util.*;

class ClassMetaInfo extends ClassVisitor {
    final List<Constant> constants = new LinkedList<>();
    final List<NativeMethod> methods = new LinkedList<>();
    final Map<String, Integer> counts = new HashMap<>();

    ClassName superClassName;
    ClassName name;

    public ClassMetaInfo() {
        super(Opcodes.ASM6);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.superClassName = superName == null ? null : ClassName.of(superName.replace('/', '.'));
        this.name = ClassName.of(name.replace('/', '.'));
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

    boolean isOverloadMethod(NativeMethod method) {
        Objects.requireNonNull(method);
        return counts.getOrDefault(method.name(), 1) > 1;
    }
}
