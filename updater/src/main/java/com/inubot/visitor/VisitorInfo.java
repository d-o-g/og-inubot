package com.inubot.visitor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Tyler Sedlar
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface VisitorInfo {

    public String[] hooks();
}
