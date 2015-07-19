package com.inubot.script;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
public @interface Manifest {
    String name();
    String developer();
    String desc();
    double version() default 1.0;
}
