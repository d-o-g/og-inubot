package com.inubot.net;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Septron
 * @since July 02, 2015
 */
public class Manager {

    private static final Map<Short, Handler> handlers = new HashMap<>();

    private Manager() {}

    public static void add(Handler handler) {
        handlers.put(handler.opcode(), handler);
    }

    public static Handler get(short opcode) {
        return handlers.get(opcode);
    }
}
