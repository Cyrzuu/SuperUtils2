package me.cyrzu.git.superutils2.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


@UtilityClass
public class FileUtils {

    @NotNull
    public final FilenameFilter JSON_FILTER = (dir, name) -> name.endsWith(".json");

    @NotNull
    public final FilenameFilter YML_FILTER = (dir, name) -> name.endsWith(".yml");

    @NotNull
    private final static Method READ_FILE_TO_STRING;

    static {
        try {
            Class<?> aClass = Class.forName("org.apache.commons.io.FileUtils");
            READ_FILE_TO_STRING = aClass.getMethod("readFileToString", File.class, Charset.class);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public String readFileToString(@NotNull File file) {
        return FileUtils.readFileToString(file, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    @SneakyThrows
    public String readFileToString(@NotNull File file, String def) {
        return (String) READ_FILE_TO_STRING.invoke(null, file, StandardCharsets.UTF_8);
    }

    public boolean createFile(@NotNull File file) {
        if(file.exists()) {
            return false;
        }

        File parent = file.getParentFile();
        if(!parent.exists() && !parent.mkdirs()) {
            return false;
        }

        try {
            return file.createNewFile();
        } catch (Exception e) {
            return false;
        }
    }

}
