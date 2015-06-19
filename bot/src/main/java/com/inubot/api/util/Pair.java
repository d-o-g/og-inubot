/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.util;

import java.util.Objects;

/**
 * @author unsigned
 * @since 26-04-2015
 */
public final class Pair<Left, Right> {

    private Left left;
    private Right right;

    public Pair(Left left, Right right) {
        this.left = left;
        this.right = right;
    }

    public Pair() {

    }

    public boolean equals(Object other) {
        if (other instanceof Pair) {
            Pair otherType = (Pair) other;
            return left.equals(otherType.left) && right.equals(otherType.right);
        }
        return false;
    }

    public final Left getLeft() {
        return left;
    }

    public final Right getRight() {
        return right;
    }

    public final void setLeft(Left left) {
        this.left = left;
    }

    public final void setRight(Right right) {
        this.right = right;
    }

    public int hashCode() {
        return Objects.hash(left, right);
    }
}

