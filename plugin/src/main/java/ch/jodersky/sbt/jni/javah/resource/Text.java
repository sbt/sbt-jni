package ch.jodersky.sbt.jni.javah.resource;

import java.util.ListResourceBundle;

public final class Text extends ListResourceBundle {
    @Override
    protected final Object[][] getContents() {
        return new Object[][]{
                {"javah.help", "Usage:\n" +
                        "  gjavah [options] <classes>\n" +
                        "where [options] include:\n" +
                        //"  -o <file>                    Output file (only one of -d or -o may be used)\n" +
                        "  -d <dir>                     Output directory\n" +
                        "  -v  -verbose                 Enable verbose output\n" +
                        "  -h  --help  -?               Print this message\n" +
                        "  -version                     Print version information\n" +
                        "  --module-path <path>         Path from which to load application modules\n" +
                        "  --class-path <path>          Path from which to load classes\n" +
                        "  -classpath <path>            Path from which to load classes\n" +
                        "  -cp <path>                   Path from which to load classes\n" +
                        "\n" +
                        "Each class must be specified by its fully qualified names, optionally\n" +
                        "prefixed by a module name followed by /. Examples:\n" +
                        "    java.lang.Object\n" +
                        "    java.base/java.io.File"},
                {"javah.version", "gjavah version \"%s\""},
                {"javah.error.missArg", "Error: value missing for option \"%s\""},
                {"javah.error.unknownOption", "Error: unknown option: %s"},
                {"javah.error.noClasses", "Error: no classes specified"}
        };
    }
}
