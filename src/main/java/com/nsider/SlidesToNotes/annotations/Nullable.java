package com.nsider.SlidesToNotes.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation to indicate that a method parameter, return value, or field can be {@code null}.
 * <p>
 * This annotation is used to explicitly declare that a method parameter, return value, or field may have a {@code null} value.
 * It is typically used in combination with tools or static analysis to ensure proper null safety and documentation.
 * </p>
 */
@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Nullable {
}