package me.cyrzu.git.superutils2.commands.annotations;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Use the %sec abbreviation to obtain time in seconds.</br>
 *  Use the %time to get the time.
*/

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CooldownMessage {
    @NotNull
    String value();
}