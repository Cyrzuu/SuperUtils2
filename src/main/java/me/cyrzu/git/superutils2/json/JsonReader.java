package me.cyrzu.git.superutils2.json;

import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import me.cyrzu.git.superutils2.utils.EnumUtils;
import me.cyrzu.git.superutils2.utils.FileUtils;
import me.cyrzu.git.superutils2.utils.StringUtils;
import me.cyrzu.git.superutils2.world.Bound;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

public class JsonReader {

    @NotNull
    public static JsonReader EMPTY = new JsonReader(new JsonObject());

    @NotNull
    private final JsonObject jsonObject;

    private JsonReader(@NotNull JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    @NotNull
    public JsonWriter toJsonWriter() {
        return new JsonWriter(this.jsonObject.deepCopy());
    }

    public boolean isSet(@NotNull String path) {
        return this.get(path) != null;
    }

    @NotNull
    public Set<String> keySet() {
        return jsonObject.keySet();
    }

    @NotNull
    public Map<String, JsonReader> getKeysWithReader(@NotNull String path) {
        JsonReader reader = this.getReader(path);
        if(reader == null) {
            return Collections.emptyMap();
        }

        return reader.getKeysWithReader();
    }

    @NotNull
    public Map<String, JsonReader> getKeysWithReader() {
        Map<String, JsonReader> reader = new HashMap<>();

        Set<String> keys = this.keySet();
        for (String key : keys) {
            JsonElement jsonElement = this.get(key);
            if(!(jsonElement instanceof JsonObject object)) {
                continue;
            }

            reader.put(key, new JsonReader(object));
        }

        return ImmutableMap.copyOf(reader);
    }

    @NotNull
    public Map<String, JsonPrimitive> getKeysWithValue(@NotNull String path) {
        JsonReader reader = this.getReader(path);
        if(reader == null) {
            return Collections.emptyMap();
        }

        return reader.getKeysWithValue();
    }

    @NotNull
    public Map<String, JsonPrimitive> getKeysWithValue() {
        Map<String, JsonPrimitive> primatives = new HashMap<>();

        for (String key: this.keySet()) {
            JsonElement jsonElement = this.get(key);
            if(!(jsonElement instanceof JsonPrimitive object)) {
                continue;
            }

            primatives.put(key, object);
        }

        return primatives;
    }

    public String getFirstString(@NotNull Collection<String> paths) {
        return this.getFirstString(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String getFirstString(@NotNull Collection<String> paths, @Nullable String def) {
        return paths.stream().map(this::getString).filter(Objects::nonNull).findFirst().orElse(def);
    }

    @Nullable
    public String getString(@NotNull String path) {
        return this.getString(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public String getString(@NotNull String path, @Nullable String def) {
        try {
            JsonElement element = this.get(path);
            return element == null ? def : element.getAsString();
        } catch (Exception e) {
            return def;
        }
    }

    public int getFirstInt(@NotNull Collection<String> paths) {
        return this.getFirstInt(paths, 0);
    }

    public int getFirstInt(@NotNull Collection<String> paths, int def) {
        for (String path : paths) {
            JsonElement jsonElement = this.get(path);
            if(!(jsonElement instanceof JsonPrimitive primitive) || !primitive.isNumber()) {
                continue;
            }

            def = jsonElement.getAsInt();
            break;
        }

        return def;
    }

    public int getInt(@NotNull String path) {
        return getInt(path, 0);
    }

    public int getInt(@NotNull String path, int def) {
        try {
            JsonElement element = this.get(path);
            return element == null ? def : element.getAsInt();
        } catch (Exception e) {
            return def;
        }
    }

    public boolean getFirstBoolean(@NotNull Collection<String> paths) {
        return this.getFirstBoolean(paths, false);
    }

    public boolean getFirstBoolean(@NotNull Collection<String> paths, boolean def) {
        for (String path : paths) {
            JsonElement jsonElement = this.get(path);
            if(!(jsonElement instanceof JsonPrimitive primitive) || !primitive.isBoolean()) {
                continue;
            }

            def = primitive.getAsBoolean();
            break;
        }

        return def;
    }

    public boolean getBoolean(@NotNull JsonObject object, @NotNull String path) {
        return getBoolean(path, false);
    }

    public double getFirstDouble(@NotNull Collection<String> paths) {
        return this.getFirstDouble(paths, 0D);
    }

    public double getFirstDouble(@NotNull Collection<String> paths, double def) {
        for (String path : paths) {
            JsonElement jsonElement = this.get(path);
            if(!(jsonElement instanceof JsonPrimitive primitive) || !primitive.isNumber()) {
                continue;
            }

            def = primitive.getAsDouble();
            break;
        }

        return def;
    }

    public boolean getBoolean(@NotNull String path, boolean def) {
        try {
            JsonElement element = get(path);
            return element == null ? def : element.getAsBoolean();
        } catch (Exception e) {
            return def;
        }
    }

    public double getDouble(@NotNull String path) {
        return getDouble(path, 0);
    }

    public double getDouble(@NotNull String path, double def) {
        try {
            JsonElement element = get(path);
            return element == null ? def : element.getAsDouble();
        } catch (Exception e) {
            return def;
        }
    }
    public long getFirstLong(@NotNull Collection<String> paths) {
        return this.getFirstLong(paths, 0L);
    }

    public long getFirstLong(@NotNull Collection<String> paths, long def) {
        for (String path : paths) {
            JsonElement jsonElement = this.get(path);
            if(!(jsonElement instanceof JsonPrimitive primitive) || !primitive.isNumber()) {
                continue;
            }

            def = primitive.getAsLong();
            break;
        }

        return def;
    }

    public long getLong(@NotNull String path) {
        return getLong(path, 0);
    }

    public long getLong(@NotNull String path, long def) {
        try {
            JsonElement element = get(path);
            return element == null ? def : element.getAsLong();
        } catch (Exception e) {
            return def;
        }
    }

    @Nullable
    public <T extends Enum<T>> T getFirstEnum(@NotNull Collection<String> paths, @NotNull Class<T> clazz) {
        return this.getFirstEnum(paths, clazz, null);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T extends Enum<T>> T getFirstEnum(@NotNull Collection<String> paths, @NotNull Class<T> clazz, @Nullable T def) {
        return paths.stream().map(path -> this.getEnum(path, clazz)).filter(Objects::nonNull).findFirst().orElse(def);
    }

    @Nullable
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz) {
        return getEnum(path, clazz, null);
    }

    @Nullable
    @Contract("_, _, !null -> !null")
    public <T extends Enum<T>> T getEnum(@NotNull String path, @NotNull Class<T> clazz, @Nullable T def) {
        return EnumUtils.getEnum(getString(path, ""), clazz, def);
    }

    @Nullable
    public Location getFirstLocation(@NotNull Collection<String> paths) {
        return this.getFirstLocation(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getFirstLocation(@NotNull Collection<String> paths, @Nullable Location def) {
        return paths.stream().map(this::getLocation).filter(Objects::nonNull).findFirst().orElse(def);
    }


    @Nullable
    public Location getLocation(@NotNull String path) {
        return this.getLocation(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getLocation(@NotNull String path, @Nullable Location def) {
        JsonReader reader = this.getReader(path);
        if(reader == null || (!reader.isSet("x") || !reader.isSet("y") || !reader.isSet("z"))) {
            return def;
        }

        World world = Bukkit.getWorld(reader.getString("world", ""));
        if(world == null) {
            return def;
        }

        return new Location(world, reader.getDouble("x"), reader.getDouble("y"), reader.getDouble("z"), (float) reader.getDouble("yaw"), (float) reader.getDouble("pitch"));
    }

    @Nullable
    public Location getFirstLocationBlock(@NotNull Collection<String> paths) {
        return this.getFirstLocationBlock(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getFirstLocationBlock(@NotNull Collection<String> paths, @Nullable Location def) {
        return paths.stream().map(this::getLocationBlock).filter(Objects::nonNull).findFirst().orElse(def);
    }


    @Nullable
    public Location getLocationBlock(@NotNull String path) {
        return this.getLocationBlock(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Location getLocationBlock(@NotNull String path, @Nullable Location def) {
        JsonReader reader = this.getReader(path);
        if(reader == null || (!reader.isSet("x") || !reader.isSet("y") || !reader.isSet("z"))) {
            return def;
        }

        World world = Bukkit.getWorld(reader.getString("world", ""));
        if(world == null) {
            return def;
        }

        return new Location(world, reader.getDouble("x"), reader.getDouble("y"), reader.getDouble("z"));
    }

    @Nullable
    public Vector getFirstVector(@NotNull Collection<String> paths) {
        return this.getFirstVector(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Vector getFirstVector(@NotNull Collection<String> paths, @Nullable Vector def) {
        return paths.stream().map(this::getVector).filter(Objects::nonNull).findFirst().orElse(def);
    }


    @Nullable
    public Vector getVector(@NotNull String path) {
        return this.getVector(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Vector getVector(@NotNull String path, @Nullable Vector def) {
        JsonReader reader = this.getReader(path);
        if(reader == null || (!reader.isSet("x") || !reader.isSet("y") || !reader.isSet("z"))) {
            return def;
        }

        return new Vector(reader.getDouble("x"), reader.getDouble("y"), reader.getDouble("z"));
    }

    @Nullable
    public Bound getFirstBound(@NotNull Collection<String> paths) {
        return this.getFirstBound(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Bound getFirstBound(@NotNull Collection<String> paths, @Nullable Bound def) {
        return paths.stream().map(this::getBound).filter(Objects::nonNull).findFirst().orElse(def);
    }


    @Nullable
    public Bound getBound(@NotNull String path) {
        return this.getBound(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Bound getBound(@NotNull String path, @Nullable Bound def) {
        JsonReader reader = this.getReader(path);
        if(reader == null) {
            return def;
        }

        JsonReader min = reader.getReader("min");
        JsonReader max = reader.getReader("max");
        if(min == null || max == null) {
            return def;
        }

        return new Bound(
                min.getInt("x"), min.getInt("y"), min.getInt("z"),
                max.getInt("x"), max.getInt("y"), max.getInt("z")
        );
    }

    @NotNull
    public JsonArray getJsonArray(@NotNull String path) {
        return this.getJsonArray(path, new JsonArray());
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonArray getJsonArray(@NotNull String path, @Nullable JsonArray def) {
        JsonElement jsonElement = this.get(path);
        return jsonElement instanceof JsonArray array ? array : def;
    }

    @NotNull
    public List<String> getListString(@NotNull String path) {
        return this.getListString(path, new ArrayList<>());
    }

    @NotNull
    public List<String> getFirstListString(@NotNull Collection<String> paths) {
        return this.getFirstListString(paths, List.of());
    }

    @NotNull
    public List<String> getFirstListString(@NotNull Collection<String> paths, @NotNull List<String> def) {
        return paths.stream().map(this::getListString).filter(list -> !list.isEmpty()).findFirst().orElse(def);
    }


    @NotNull
    public List<String> getListString(@NotNull String path, @NotNull List<String> def) {
        try {
            List<JsonElement> list = this.getList(path);
            if(list == null) {
                return def;
            }

            return list.stream().map(JsonElement::getAsString).toList();
        } catch (Exception e) {
            return def;
        }
    }

    @NotNull
    public List<JsonReader> getFirstListReader(@NotNull Collection<String> paths) {
        return this.getFirstListReader(paths, List.of());
    }

    @NotNull
    public List<JsonReader> getFirstListReader(@NotNull Collection<String> paths, @NotNull List<JsonReader> def) {
        return paths.stream().map(this::getListReader).filter(list -> !list.isEmpty()).findFirst().orElse(def);
    }


    @NotNull
    public List<JsonReader> getListReader(@NotNull String path) {
        return getListReader(path, new ArrayList<>());
    }

    @NotNull
    public List<JsonReader> getListReader(@NotNull String path, @NotNull List<JsonReader> def) {
        try {
            List<JsonElement> list = this.getList(path);
            if(list == null) {
                return def;
            }

            return list.stream()
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .map(JsonReader::parseObject)
                    .toList();
        } catch (Exception e) {
            return def;
        }
    }

    @NotNull
    public List<JsonElement> getFirstList(@NotNull Collection<String> paths) {
        return this.getFirstList(paths, List.of());
    }

    @NotNull
    @Contract("_, !null -> !null")
    public List<JsonElement> getFirstList(@NotNull Collection<String> paths, @NotNull List<JsonElement> def) {
        return paths.stream().map(this::getList).filter(list -> !list.isEmpty()).findFirst().orElse(def);
    }

    @Nullable
    public List<JsonElement> getList(@NotNull String path) {
        return this.getList(path, new ArrayList<>());
    }

    @Nullable
    @Contract("_, !null -> !null")
    public List<JsonElement> getList(@NotNull String path, @Nullable List<JsonElement> def) {
        if(!(this.get(path) instanceof JsonArray array)) {
            return def;
        }

        List<JsonElement> list = new ArrayList<>();
        array.forEach(list::add);
        return list;
    }

    @Nullable
    public UUID getFirstUUID(@NotNull Collection<String> paths) {
        return this.getFirstUUID(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public UUID getFirstUUID(@NotNull Collection<String> paths, @Nullable UUID def) {
        return paths.stream().map(this::getUUID).filter(Objects::nonNull).findFirst().orElse(def);
    }

    @Nullable
    public UUID getUUID(@NotNull String path) {
        return this.getUUID(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public UUID getUUID(@NotNull String path, @Nullable UUID def) {
        UUID uuid = StringUtils.toUUID(this.getString(path, ""));
        return uuid != null ? uuid : def;
    }

    public void getReader(@NotNull String path, @NotNull Consumer<JsonReader> fun) {
        this.getReader(Collections.singletonList(path), fun);
    }

    public void getReader(@NotNull Collection<String> paths, @NotNull Consumer<JsonReader> fun) {
        for (String path : paths) {
            JsonReader reader = this.getReader(path);
            if(reader == null) {
                continue;
            }

            fun.accept(reader);
            return;
        }
    }

    @Nullable
    public JsonReader getFirstReader(@NotNull Collection<String> paths) {
        return this.getFirstReader(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonReader getFirstReader(@NotNull Collection<String> paths, @Nullable JsonReader def) {
        return paths.stream().map(this::getReader).filter(Objects::nonNull).findFirst().orElse(def);
    }


    @Nullable
    public JsonReader getReader(@NotNull String path) {
        return this.getReader(path, (JsonReader) null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonReader getReader(@NotNull String path, @Nullable JsonReader def) {
        JsonElement jsonElement = this.get(path);
        return jsonElement instanceof JsonObject object ? new JsonReader(object) : def;
    }

    @Nullable
    public Object getFirstObject(@NotNull Collection<String> paths) {
        return this.getFirstObject(paths, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Object getFirstObject(@NotNull Collection<String> paths, @Nullable Object def) {
        return paths.stream().map(this::getObject).filter(Objects::nonNull).findFirst().orElse(def);
    }


    @Nullable
    public Object getObject(@NotNull String path) {
        return this.getObject(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public Object getObject(@NotNull String path, @Nullable Objects def) {
        JsonElement jsonElement = this.get(path);
        if(jsonElement instanceof JsonObject o) {
            return new JsonReader(o);
        }

        if(jsonElement instanceof JsonArray a) {
            return this.getList(path, Object.class);
        }

        if(jsonElement instanceof JsonPrimitive primitive) {
            try {
                Field value = JsonPrimitive.class.getDeclaredField("value");
                value.setAccessible(true);

                Object item = value.get(primitive);
                value.setAccessible(false);
                return item;
            } catch (Exception ignored) { }
        }

        return def;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getList(@NotNull String path, @NotNull Class<T> clazz) {
        try {
            Field value = JsonPrimitive.class.getDeclaredField("value");
            value.setAccessible(true);

            JsonElement jsonElement = this.get(path);
            if(!(jsonElement instanceof JsonArray array)) {
                return List.of();
            }

            List<T> list = new ArrayList<>();
            for (JsonElement element : array) {
                Object item = value.get(element);

                if(clazz.isAssignableFrom(item.getClass())) {
                    list.add((T) item);
                } else if(!Objects.equals(clazz, String.class)) {
                    list.add((T) item.toString());
                }
            }

            value.setAccessible(false);
            return List.copyOf(list);
        } catch (Exception ignored) {}
        return List.of();
    }

    public <T> void getAndRun(@NotNull String path, @NotNull Class<T> clazz, @NotNull Runnable function) {
        this.getAndRun(path, clazz, (value) -> function.run());
    }

    public <T> void getAndRun(@NotNull List<String> paths, @NotNull Class<T> clazz, @NotNull Runnable function) {
        this.getAndRun(paths, clazz, (value) -> function.run());
    }

    public <T> void getAndRun(@NotNull String path, @NotNull Class<T> clazz, @NotNull Consumer<T> function) {
        this.getAndRun(Collections.singletonList(path), clazz, function);
    }

    public <T> void getAndRun(@NotNull Collection<String> paths, @NotNull Class<T> clazz, @NotNull Consumer<T> function) {
        for (String path : paths) {
            JsonElement json = this.get(path);
            if(json instanceof JsonObject obj && clazz.equals(JsonReader.class)) {
                function.accept(clazz.cast(JsonReader.parseObject(obj)));
                return;
            } else if(json instanceof JsonObject obj && clazz.equals(JsonObject.class)) {
                function.accept(clazz.cast(JsonReader.parseObject(obj)));
                return;
            } else if (json instanceof JsonPrimitive primitive) {
                Object value = null;

                if (clazz.equals(String.class)) {
                    value = primitive.getAsString();
                } else if ((clazz.equals(Integer.class) || clazz.equals(int.class)) && primitive.isNumber()) {
                    value = primitive.getAsInt();
                } else if ((clazz.equals(Double.class) || clazz.equals(double.class)) && primitive.isNumber()) {
                    value = primitive.getAsDouble();
                } else if ((clazz.equals(Boolean.class) || clazz.equals(boolean.class)) && primitive.isBoolean()) {
                    value = primitive.getAsBoolean();
                } else if ((clazz.equals(Long.class) || clazz.equals(long.class)) && primitive.isNumber()) {
                    value = primitive.getAsLong();
                } else if ((clazz.equals(Float.class) || clazz.equals(float.class)) && primitive.isNumber()) {
                    value = primitive.getAsFloat();
                }

                if(value != null && clazz.equals(value.getClass())) {
                    function.accept(clazz.cast(value));
                    return;
                }
            } else if(json instanceof JsonArray array && clazz.equals(JsonArray.class)) {
                function.accept(clazz.cast(array));
                return;
            }
        }
    }

    @Nullable
    public JsonElement get(@NotNull String path) {
        return this.get(path, null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public JsonElement get(@NotNull String path, @Nullable JsonElement def) {
        JsonElement object = jsonObject;
        try {
            String[] array = path.split("\\.");
            int length = array.length;

            if(length == 1) {
                return jsonObject.get(array[0]);
            }

            int index = 0;
            for (String key : array) {
                if(!(object instanceof JsonObject obj)) {
                    return def;
                }

                if(++index >= length) {
                    return obj.get(key);
                }

                object = obj.get(key);
            }
        } catch (JsonSyntaxException ignored) { }

        return def;
    }

    public static void parseFile(@NotNull File file, @NotNull Consumer<JsonReader> function) {
        JsonReader reader = JsonReader.parseFile(file);
        if(reader != null) {
            function.accept(reader);
        }
    }

    @NotNull
    public static JsonReader parseFileOrEmpty(@Nullable File file) {
        return file != null ? JsonReader.parseFile(file, EMPTY) : EMPTY;
    }

    @Nullable
    public static JsonReader parseFile(@NotNull File file) {
        return JsonReader.parseString(FileUtils.readFileToString(file, "{}"));
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static JsonReader parseFile(@NotNull File file, @Nullable JsonReader def) {
        return JsonReader.parseString(FileUtils.readFileToString(file, "{}"), def);
    }

    @NotNull
    public static JsonReader parseStringOrEmpty(@Nullable String json) {
        return json != null ? JsonReader.parseString(json, EMPTY) : EMPTY;
    }

    @Nullable
    public static JsonReader parseString(@NotNull String json) {
        return JsonReader.parseString(json, (JsonReader) null);
    }

    @Nullable
    @Contract("_, !null -> !null")
    public static JsonReader parseString(@NotNull String json, @Nullable JsonReader def) {
        try {
            JsonElement element = JsonParser.parseString(json);
            return element instanceof JsonObject object ? new JsonReader(object) : null;
        } catch (Exception e) {
            return def;
        }
    }

    public static void parseString(@NotNull String json, @NotNull Consumer<JsonReader> function) {
        JsonReader reader = JsonReader.parseString(json);
        if(reader != null) {
            function.accept(reader);
        }
    }

    @NotNull
    public static JsonReader parseObject(@NotNull JsonObject json) {
        return new JsonReader(json);
    }

    @NotNull
    public static <T> JsonArray getArrayString(@NotNull Stream<T> stream, Function<T, String> function) {
        return JsonReader.getArrayString(stream.toList(), function);
    }

    @NotNull
    public static <T> JsonArray getArrayString(@NotNull Collection<T> collect, Function<T, String> function) {
        JsonArray array = new JsonArray();
        collect.forEach(object -> array.add(function.apply(object)));
        return array;
    }

    @NotNull
    public static List<JsonReader> getArray(@NotNull JsonReader reader, @NotNull String path) {
        List<JsonElement> list = reader.getList(path);
        if(list == null) {
            return Collections.emptyList();
        }

        return list.stream()
                .map(element -> element instanceof JsonObject object ? object : null)
                .filter(Objects::nonNull).map(JsonReader::new).toList();
    }

    @NotNull
    public static List<JsonReader> parseJsonArray(@NotNull File file) {
        return JsonReader.parseJsonArray(FileUtils.readFileToString(file, "[]"));
    }

    @NotNull
    public static List<JsonReader> parseJsonArray(@NotNull String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if(!(jsonElement instanceof JsonArray array)) {
                return Collections.emptyList();
            }

            List<JsonElement> list = new ArrayList<>();
            array.forEach(list::add);

            return list.stream()
                    .filter(JsonElement::isJsonObject)
                    .map(JsonElement::getAsJsonObject)
                    .map(JsonReader::parseObject)
                    .toList();

        } catch (Exception ignore) { }
        return Collections.emptyList();
    }

    @NotNull
    public static List<String> parseJsonArrayString(@NotNull String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if(!(jsonElement instanceof JsonArray array)) {
                return Collections.emptyList();
            }

            List<JsonElement> list = new ArrayList<>();
            array.forEach(list::add);

            return list.stream()
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonElement::getAsJsonPrimitive)
                    .map(JsonPrimitive::getAsString)
                    .toList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @NotNull
    public static List<JsonPrimitive> parseJsonArrayPrimative(@NotNull String json) {
        try {
            JsonElement jsonElement = JsonParser.parseString(json);
            if(!(jsonElement instanceof JsonArray array)) {
                return Collections.emptyList();
            }

            List<JsonElement> list = new ArrayList<>();
            array.forEach(list::add);

            return list.stream()
                    .filter(JsonElement::isJsonPrimitive)
                    .map(JsonElement::getAsJsonPrimitive)
                    .toList();

        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return jsonObject.toString();
    }
}
