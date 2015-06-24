package me.septron.db;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * @author Septron
 * @since June 24, 2015
 */
@Retention(RUNTIME)
public @interface Column {

    boolean increment() default false;

    String name();
}