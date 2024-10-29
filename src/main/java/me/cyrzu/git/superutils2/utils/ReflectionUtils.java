package me.cyrzu.git.superutils2.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@UtilityClass
public class ReflectionUtils {

    @Nullable
    public static Class<?> getClass(@NotNull String path, @NotNull String name) {
        return ReflectionUtils.getClass(path + "." + name);
    }

    @Nullable
    public static Class<?> getInnerClass(@NotNull String path, @NotNull String name) {
        return ReflectionUtils.getClass(path + "$" + name);
    }

    @Nullable
    private static Class<?> getClass(@NotNull String path) {
        try {
            return Class.forName(path);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    @Nullable
    public static Constructor<?> getConstructor(@Nullable Class<?> clazz, Class<?>... types) {
        try {
            Constructor<?> constructor = clazz != null ? clazz.getDeclaredConstructor(types) : null;
            if(clazz != null) {
                constructor.setAccessible(true);
            }

            return constructor;
        } catch (ReflectiveOperationException e) {
            return null;
        }
    }

    @NotNull
    @SuppressWarnings("unchecked")
    public static <T> T invokeMethod(@NotNull Method method, @Nullable Object obj, Class<T> clazz, @Nullable Object... args) {
        try {
            return (T) invokeMethod(method, obj, args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Object invokeMethod(@NotNull Method method, @Nullable Object obj, @Nullable Object... args) {
        method.setAccessible(true);
        try {
            return method.invoke(obj, args);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    public static Object invokeConstructor(@NotNull Constructor<?> constructor, Object... obj) {
        try {
            return constructor.newInstance(obj);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    public static Method getMethod(@Nullable Class<?> clazz, @NotNull String fieldName, @Nullable Class<?>... o) {
        try {
            return clazz != null ? clazz.getDeclaredMethod(fieldName, o) : null;
        } catch (NoSuchMethodException e) {
            Class<?> superClass = clazz.getSuperclass();
            return superClass == null ? null : getMethod(superClass, fieldName);
        }
    }

    @Nullable
    public static Field getField(@NotNull Class<?> clazz, @NotNull String fieldName, boolean accessible) {
        try {
            Field field = clazz.getField(fieldName);
            if(accessible) {
                field.setAccessible(true);
            }

            return field;
        } catch (Exception ignore) {
            return null;
        }
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public static Object getFieldValue(@NotNull Object source, @NotNull String name) {
        try {
            Class<?> clazz = source instanceof Class<?> ? (Class<?>) source : source.getClass();
            Field field = ReflectionUtils.getField(clazz, name, true);
            if (field == null) return null;

            field.setAccessible(true);
            return field.get(source);
        }
        catch (IllegalAccessException exception) {
            exception.printStackTrace();
        }
        return null;
    }

}