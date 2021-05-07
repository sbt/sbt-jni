package ch.jodersky.sbt.jni.javah.resource;

import java.util.Locale;
import java.util.ResourceBundle;

public final class Resource {
    public static ResourceBundle text;

    static {
        reload(Locale.getDefault());
    }

    public static void reload(Locale locale) {
        text = ResourceBundle.getBundle("ch.jodersky.sbt.jni.javah.resource.Text", locale);
    }

    public static String getText(String key) {
        return text.getString(key);
    }

    public static String getText(String key, Object... args) {
        return String.format(text.getString(key), args);
    }

}
