/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.util.filter;

public interface Filter<E> {

    public boolean accept(E e);

    /**
     * @param <T> the type
     * @return a Filter that always accepts the element
     */
    public static <T> Filter<T> always() {
        return t -> true;
    }

    /**
     * @param <T> the type
     * @return a Filter that always rejects the element
     */
    public static <T> Filter<T> never() {
        return t -> false;
    }

    public static <E> Filter<E> not(Filter<E> filter) {
        return e -> !filter.accept(e);
    }
}
