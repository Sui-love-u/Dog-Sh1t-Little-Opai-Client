package qwq.arcane.event.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
/* loaded from: Arcane 8.10.jar:qwq/arcane/event/annotations/EventPriority.class */
public @interface EventPriority {
    int value() default 10;
}
