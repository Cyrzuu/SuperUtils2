package me.cyrzu.git.superutils2.utils;

import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

@UtilityClass
public class StringUtils {

    @NotNull
    private final Pattern KEY_VALUE_DEFINITION_PATTERN = Pattern.compile("(\\w+)\\s*[:=]\\s*\\{([^}]*(?:\\\\}|[^}]*)*)}");

    @NotNull
    private final Pattern KEY_VALUE_PATTERN = Pattern.compile("(\\w+)\\s*[:=]\\s*\"((?:\\\\\"|[^\"])*?)\"");

    @NotNull
    public Map<String, String> parseKeyValueDefinition(@NotNull String input) {
        Map<String, String> resultMap = new HashMap<>();

        Matcher itemMatcher = KEY_VALUE_DEFINITION_PATTERN.matcher(input);
        while (itemMatcher.find()) {
            String key = itemMatcher.group(1);
            String content = itemMatcher.group(2).replace("\\}", "}");

            resultMap.put(key, content);
        }

        return resultMap;
    }

    public Map<String, String> parseKeyValue(@NotNull String input) {
        Map<String, String> resultMap = new HashMap<>();

        Matcher itemMatcher = KEY_VALUE_PATTERN.matcher(input);
        while (itemMatcher.find()) {
            String key = itemMatcher.group(1);
            String content = itemMatcher.group(2).replace("\\\"", "\"");

            resultMap.put(key, content);
        }

        return resultMap;
    }


    @Nullable
    public String compressToBase64(@NotNull String text) {
        return StringUtils.compressToBase64(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String compressToBase64(@NotNull String text, @Nullable String def) {
        try (ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
             GZIPOutputStream gzipStream = new GZIPOutputStream(byteStream)) {
            gzipStream.write(text.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(byteStream.toByteArray());
        } catch (IOException e) {
            return def;
        }
    }

    @Nullable
    public String decodeFromBase64(@NotNull String text) {
        return StringUtils.decodeFromBase64(text, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String decodeFromBase64(@NotNull String text, @Nullable String def) {
        byte[] decodedBytes = Base64.getDecoder().decode(text);
        try (ByteArrayInputStream byteInputStream = new ByteArrayInputStream(decodedBytes);
             GZIPInputStream gzipInputStream = new GZIPInputStream(byteInputStream);
             ByteArrayOutputStream decompressedStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzipInputStream.read(buffer)) > 0) {
                decompressedStream.write(buffer, 0, len);
            }

            return decompressedStream.toString();
        } catch (IOException e) {
            return def;
        }
    }

    @Nullable
    public UUID toUUID(@Nullable String text) {
        if(text == null) {
            return null;
        }

        try {
            return UUID.fromString(text);
        } catch (Exception e) {
            return null;
        }
    }

    public String capitalize(@NotNull String str, final char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        final Predicate<Integer> isDelimiter = generateIsDelimiterFunction(delimiters);
        final int strLen = str.length();
        final int[] newCodePoints = new int[strLen];
        int outOffset = 0;

        boolean capitalizeNext = true;
        for (int index = 0; index < strLen;) {
            final int codePoint = str.codePointAt(index);

            if (isDelimiter.test(codePoint)) {
                capitalizeNext = true;
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            } else if (capitalizeNext) {
                final int titleCaseCodePoint = Character.toTitleCase(codePoint);
                newCodePoints[outOffset++] = titleCaseCodePoint;
                index += Character.charCount(titleCaseCodePoint);
                capitalizeNext = false;
            } else {
                newCodePoints[outOffset++] = codePoint;
                index += Character.charCount(codePoint);
            }
        }

        return new String(newCodePoints, 0, outOffset);
    }

    public String capitalizeFully(final String str) {
        return capitalizeFully(str, null);
    }

    public String capitalizeFully(String str, final char... delimiters) {
        if (str.isEmpty()) {
            return str;
        }
        str = str.toLowerCase();
        return capitalize(str, delimiters);
    }

    private Predicate<Integer> generateIsDelimiterFunction(final char[] delimiters) {
        final Predicate<Integer> isDelimiter;
        if (delimiters == null || delimiters.length == 0) {
            isDelimiter = delimiters == null ? Character::isWhitespace : c -> false;
        } else {
            final Set<Integer> delimiterSet = new HashSet<>();
            for (int index = 0; index < delimiters.length; index++) {
                delimiterSet.add(Character.codePointAt(delimiters, index));
            }
            isDelimiter = delimiterSet::contains;
        }

        return isDelimiter;
    }

}
