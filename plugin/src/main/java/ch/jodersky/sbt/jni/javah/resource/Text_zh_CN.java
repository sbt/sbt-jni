package ch.jodersky.sbt.jni.javah.resource;

import java.util.ListResourceBundle;

public final class Text_zh_CN extends ListResourceBundle {
    @Override
    protected final Object[][] getContents() {
        return new Object[][]{
                {"javah.help", "用法:\n" +
                        "  gjavah [options] <classes>\n" +
                        "其中, [options] 包括:\n" +
                        //"  -o <file>                    输出文件 (只能使用 -d 或 -o 之一)\n" +
                        "  -d <dir>                     输出目录\n" +
                        "  -v  -verbose                 启用详细输出\n" +
                        "  -h  --help  -?               输出此消息\n" +
                        "  -version                     输出版本信息\n" +
                        "  --module-path <路径>         从中加载应用程序模块的路径\n" +
                        "  --class-path <路径>          从中加载类的路径\n" +
                        "  -classpath <path>            从中加载类的路径\n" +
                        "  -cp <path>                   从中加载类的路径\n" +
                        "\n" +
                        "每个类必须由其全限定名称指定,\n" +
                        "可以选择性地使用模块名后跟 / 作为前缀。示例:\n" +
                        "    java.lang.Object\n" +
                        "    java.base/java.io.File"},
                {"javah.version", "gjavah 版本 \"%s\""},
                {"javah.error.missArg", "错误：选项 \"%s\" 缺少值"},
                {"javah.error.unknownOption", "错误: 未知选项: %s"},
                {"javah.error.noClasses", "错误: 未指定类"}
        };
    }
}
