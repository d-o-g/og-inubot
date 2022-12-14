/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.client.natives.oldschool.RSNode;
import com.inubot.client.natives.oldschool.RSNodeTable;

import java.util.Iterator;

public class NodeTable extends Wrapper<RSNodeTable> implements Iterator<RSNode> {

    private int index;
    private RSNode current;

    public NodeTable(RSNodeTable table) {
        super(table);
    }

    public RSNode first() {
        index = 0;
        return next();
    }

    @Override
    public boolean hasNext() {
        return index < raw.getBuckets().length;
    }

    public RSNode next() {
        RSNode[] buckets = raw.getBuckets();
        if (index > 0 && current != buckets[index - 1]) {
            RSNode node = current;
            current = node.getPrevious();
            return node;
        }
        while (index < buckets.length) {
            RSNode node = buckets[index++].getPrevious();
            if (index - 1 >= buckets.length || node == null)
                return null;
            if (buckets[index - 1] != node) {
                current = node.getPrevious();
                return node;
            }
        }
        return null;
    }

    public RSNode lookup(long uid) {
        for (RSNode node = first(); node != null; node = next()) {
            if (node.getUid() == uid)
                return node;
        }
        return null;
    }
}
