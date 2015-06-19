/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool.collection;

/**
 * @author unsigned
 * @since 06-06-2015
 */

import com.inubot.api.util.*;
import com.inubot.api.util.filter.Filter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import com.inubot.api.util.Random;

/**
 * {@link Pool}'s are used to provide an easy way of selection
 *
 * @param <T> The type of elements which the pool can contain
 */
public class Pool<T, K extends Pool<T, K>> implements Iterable<T> {

    private final List<T> elements;
    private final List<Filter<T>> filters;

    /**
     * @param elements The elements of which the pool should be constructed with.
     *                 Elements can still be added to the {@link Pool} even after this
     * @see Pool#add(T...)
     */
    public Pool(final T... elements) {
        if (elements == null) {
            throw new RuntimeException("null elements");
        }
        this.elements = new CopyOnWriteArrayList<>();
        this.filters = new ArrayList<>();
        Collections.addAll(this.elements, elements);
    }

    public Pool() {
        this.elements = new CopyOnWriteArrayList<>();
        this.filters = new ArrayList<>();
    }

    /**
     * @param elements The elements of which the pool should be constructed with.
     *                 Elements can still be added to the {@link Pool} even after this
     * @see Pool#add(Iterable...)
     */
    public Pool(final Iterable<T>... elements) {
        this.elements = new CopyOnWriteArrayList<>();
        this.filters = new ArrayList<>();
        for (final Iterable<T> elem : elements) {
            for (final T elem0 : elem) {
                this.elements.add(elem0);
            }
        }
    }

    public static <T, K extends Pool<T, K>> K getEmpty() {
        return (K) new Pool<>();
    }

    public final K subPool(final int startIndex) {
        return (K) subPool(startIndex, size());
    }

    public final K subPool(final int startIndex, final int endIndex) {
        return (K) new Pool<T, K>(elements.subList(startIndex, endIndex));
    }

    /**
     * adds elements to the pool
     *
     * @param elements
     * @return self
     */
    public K add(final T... elements) {
        Collections.addAll(this.elements, elements);
        return (K) this;
    }

    /**
     * Adds the elements to the pool if they're accepted by the filter
     *
     * @param filter
     * @param elements
     * @return self
     */
    public final K add(final Filter<T> filter, final T... elements) {
        for (final T element : elements) {
            if (filter.accept(element)) {
                this.elements.add(element);
            }
        }
        return (K) this;
    }

    public final K merge(final Filter<T> filter, final K... pools) {
        for (final K pool : pools) {
            for (final T element : pool) {
                if (!filter.accept(element)) {
                    continue;
                }
                this.elements.add(element);
            }
        }
        return (K) this;
    }

    public final K merge(final K... pools) {
        for (final K pool : pools) {
            this.elements.addAll(pool.getElements());
        }
        return (K) this;
    }

    /**
     * adds elements to the pool
     *
     * @param elements
     * @return self
     */
    public final K add(final Iterable<T>... elements) {
        for (final Iterable<T> elem : elements) {
            for (final T elem0 : elem) {
                this.elements.add(elem0);
            }
        }
        return (K) this;
    }

    /**
     * Filters out any elements that don't match the included filters
     * Calling Pool.include(Filter<T>...) calls this method too
     *
     * @return self
     */
    public final List<T> results() {
        for (final T next : this) {
            if (next == null || !accepts(next)) {
                elements.remove(next);
            }
        }
        return elements;
    }

    /**
     * Sorts the current elements using the given Comparator
     *
     * @param sorter
     * @return self
     */
    public final K sort(final Comparator<T> sorter) {
        Collections.sort(elements, sorter);
        return (K) this;
    }

    protected final boolean accepts(final T element) {
        if (filters.isEmpty()) return true;
        for (final Filter<T> filter : filters) {
            if (!filter.accept(element)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return A {@link java.util.List} of the current elements which exist in the pool
     */
    public final List<T> getElements() {
        return elements;
    }

    /**
     * Adds a filter to the criteria and removes any elements that are rejected by these filters
     *
     * @param filters
     * @return self
     */
    public final K include(final Filter<T>... filters) {
        Collections.addAll(this.filters, filters);
        results();
        return (K) this;
    }

    /**
     * @return The amount of elements which are currently in the pool
     */
    public final int size() {
        return elements.size();
    }

    /**
     * @return <tt>true</tt> if there are no elements in the pool
     */
    public final boolean isEmpty() {
        return size() == 0;
    }

    /**
     * @param index
     * @return An element from the pool at the given index
     */
    public final T get(final int index) {
        final List<T> results = results();
        return results.size() > index ? results.get(index) : null;
    }

    /**
     * @return The first element in the pool
     */
    public final T first() {
        return get(0);
    }

    public final T first(final Filter<T>... filters) {
        return include(filters).first();
    }

    /**
     * @return The last element in the pool
     */
    public final T last() {
        final List<T> results = results();
        return results.get(results.size() - 1);
    }

    public final T last(final Filter<T>... filters) {
        return include(filters).last();
    }

    /**
     * Removes elements until the remaining element count matches the given count
     *
     * @param count The amount of elements to keep
     * @return self
     */
    public final K limit(final int count) {
        while (elements.size() > count - 1) {
            elements.remove(elements.size() - 1);
        }
        return (K) this;
    }

    /**
     * Shuffles the List of elements
     *
     * @return self
     */
    public final K shuffle() {
        Collections.shuffle(elements);
        return (K) this;
    }

    /**
     * @return an {@link java.util.Iterator}
     * @see {@link java.util.Iterator}
     */
    public final Iterator<T> iterator() {
        return elements.iterator();
    }

    /**
     * @return a {@link java.util.ListIterator}
     * @see {@link java.util.ListIterator}
     */
    public final ListIterator<T> listIterator() {
        return results().listIterator();
    }

    /**
     * Fishes for a random element in the {@link Pool}
     *
     * @return A random element in the pool
     */
    public final T fish() {
        return fish(size() - 1);
    }

    /**
     * Fishes for a random element in the {@link Pool} whose index is before the specified max index
     * @param maxIndex the max index to limit the random search to
     * @return A random element in the pool
     */
    public final T fish(final int maxIndex) {
        return fish(0, com.inubot.api.util.Random.nextInt(maxIndex));
    }

    public final T fish(final int minIndex, final int maxIndex) {
        return get(com.inubot.api.util.Random.nextInt(minIndex, maxIndex));
    }

    /**
     * Removes all elements from this {@link Pool}
     *
     * @return self
     */
    public final K electrify() {
        elements.clear();
        return (K) this;
    }

    public T[] toArray(final T[] objs) {
        return elements.toArray(objs);
    }

    public Object[] toArray() {
        return elements.toArray();
    }

    public Stream<T> stream() {
        return elements.stream();
    }

    public Stream<T> parallelStream() {
        return elements.parallelStream();
    }

    public final T remove(final T t) {
        elements.remove(t);
        return t;
    }
}

