package moe.seikimo.magixbot.data.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Game {
    String name();

    String description() default "Wins";
}
