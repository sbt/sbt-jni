package com.github.sbt.jni.javah.util;

import com.github.sbt.jni.javah.ClassName;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public final class Utils {
    public static final int MAX_SUPPORTED_VERSION = 13;

    public static final List<String> MULTI_RELEASE_VERSIONS =
            IntStream.rangeClosed(9, MAX_SUPPORTED_VERSION).mapToObj(Integer::toString).collect(Collectors.toList());

    public static final Pattern SIMPLE_NAME_PATTERN = Pattern.compile("[^.;\\[/]+");
    public static final Pattern FULL_NAME_PATTERN =
            Pattern.compile("[^.;\\[/]+(\\.[^.;\\[/]+)*");

    public static final Pattern METHOD_NAME_PATTERN = Pattern.compile("(<init>)|(<cinit>)|([^.;\\[/<>]+)");
    public static final Pattern METHOD_TYPE_PATTERN =
            Pattern.compile("\\((?<args>(\\[*([BCDFIJSZ]|L[^.;\\[/]+(/[^.;\\\\\\[/]+)*;))*)\\)(?<ret>\\[*([BCDFIJSZV]|L[^.;\\[/]+(/[^.;\\[/]+)*;))");

    public static final PrintWriter NOOP_WRITER = new PrintWriter(new Writer() {
        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void close() throws IOException {
        }
    });

    public static String mangleName(String name) {
        StringBuilder builder = new StringBuilder(name.length() * 2);
        int len = name.length();
        for (int i = 0; i < len; i++) {
            char ch = name.charAt(i);
            if (ch == '.') {
                builder.append('_');
            } else if (ch == '_') {
                builder.append("_1");
            } else if (ch == ';') {
                builder.append("_2");
            } else if (ch == '[') {
                builder.append("_3");
            } else if ((ch >= '0' && ch <= '9') || (ch >= 'a' && ch <= 'z') || (ch >= 'A' && (ch <= 'Z'))) {
                builder.append(ch);
            } else {
                builder.append(String.format("_0%04x", (int) ch));
            }
        }
        return builder.toString();
    }

    public static String escape(String unicode) {
        Objects.requireNonNull(unicode);
        int len = unicode.length();
        StringBuilder builder = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            char ch = unicode.charAt(i);
            if (ch >= ' ' && ch <= '~') {
                builder.append(ch);
            } else {
                builder.append(String.format("\\u%04x", (int) ch));
            }
        }
        return builder.toString();
    }

    public static Path classPathRoot(Path p) {
        Objects.requireNonNull(p);
        p = p.toAbsolutePath();

        if (Files.notExists(p)) {
            return null;
        }
        if (Files.isDirectory(p)) {
            return p;
        }

        try {
            FileSystem fs = FileSystems.newFileSystem(p, (ClassLoader) null);
            String name = p.getFileName().toString().toLowerCase();
            if (name.endsWith(".jar") || name.endsWith(".zip")) {
                return fs.getPath("/");
            }
            if (name.endsWith(".jmod")) {
                return fs.getPath("/", "classes");
            }

            fs.close();
        } catch (IOException ignored) {
            return null;
        }
        return null;
    }

    static final class SuperNameVisitor extends ClassVisitor {
        SuperNameVisitor() {
            super(Opcodes.ASM7);
        }

        ClassName superName = null;

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            if (superName != null) {
                this.superName = ClassName.ofInternalName(superName);
            }
        }
    }

    public static ClassName superClassOf(ClassReader reader) {
        Objects.requireNonNull(reader);
        SuperNameVisitor v = new SuperNameVisitor();
        reader.accept(v, ClassReader.SKIP_CODE | ClassReader.SKIP_FRAMES | ClassReader.SKIP_DEBUG);
        return v.superName;
    }
}
