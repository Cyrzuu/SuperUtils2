package me.cyrzu.git.superutils2.helper;

import com.google.gson.JsonElement;
import org.jetbrains.annotations.NotNull;

public interface JsonSerializer<T extends JsonElement> {

    @NotNull
    T getJsonElement();

}
