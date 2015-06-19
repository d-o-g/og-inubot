/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.oldschool;

import com.inubot.client.natives.RSNode;
import com.inubot.client.natives.RSNodeDeque;
import com.inubot.client.natives.RSNode;
import com.inubot.client.natives.RSNodeDeque;

import java.util.Iterator;

/**
 * @author unsigned
 * @since 23-05-2015
 */
public class NodeDeque extends Wrapper<RSNodeDeque> implements Iterable<RSNode> {

    private RSNode current;

    public NodeDeque(RSNodeDeque raw) {
        super(raw);
        if (raw.getTail() != null)
            current = raw.getTail().getNext();
    }

    public int getSize() {
        int size = 0;
        RSNode head = raw.getTail();
        RSNode node = head.getNext();
        while (node != head) { //start at 0 + 1, keep going next until 0 is reach
            node = node.getNext();
            size++;
        }
        return size;
    }

    @Override
    public Iterator<RSNode> iterator() {
        return new Iterator<RSNode>() {
            @Override
            public boolean hasNext() {
                return current != null && current.getNext() != null;
            }

            @Override
            public RSNode next() {
                RSNode tmp = current;
                if (tmp == raw.getTail())
                    return current = null;
                current = tmp.getNext();
                return tmp;
            }
        };
    }
}
