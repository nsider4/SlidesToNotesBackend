package com.nsider.SlidesToNotes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to indicate that a method parameter, return value, or field cannot be {@code null}.
 * <p>
 * This annotation is used to enforce non-null constraints at the code level for improved null safety and documentation.
 * It is typically used in conjunction with tools like static analysis or IDE plugins to detect potential null pointer exceptions.
 * </p>
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Nonnull {
}