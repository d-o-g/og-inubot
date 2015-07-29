/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.util.filter;

public interface Filter<E> {

    boolean accept(E e);

    /**
     * @param <E> the type
     * @return a Filter that always accepts the element
     */
    static <E> Filter<E> always() {
        return t -> true;
    }

    /**
     * @param <E> the type
     * @return a Filter that always rejects the element
     */
    static <E> Filter<E> never() {
        return t -> false;
    }

    static <E> Filter<E> not(Filter<E> filter) {
        return e -> !filter.accept(e);
    }
}
