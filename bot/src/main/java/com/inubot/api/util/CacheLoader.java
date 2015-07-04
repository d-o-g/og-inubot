/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.util;

import com.inubot.bot.modscript.ModScript;
import com.inubot.bot.modscript.hooks.InvokeHook;
import com.inubot.client.natives.*;

import java.util.HashMap;
import java.util.Map;

public class CacheLoader {

    private static final String NULL = "null";

    private static final Map<Integer, RSObjectDefinition> OBJECT_DEFINITIONS = new HashMap<>();
    private static final Map<Integer, RSNpcDefinition> NPC_DEFINITIONS = new HashMap<>();
    private static final Map<Integer, RSItemDefinition> ITEM_DEFINITIONS = new HashMap<>();

    public static RSObjectDefinition findObjectDefinition(int id) {
        return OBJECT_DEFINITIONS.get(id);
    }

    public static RSNpcDefinition findNpcDefinition(int id) {
        return NPC_DEFINITIONS.get(id);
    }

    public static RSItemDefinition findItemDefinition(int id) {
        return ITEM_DEFINITIONS.get(id);
    }

    public static int itemIdFor(String name) {
        for (Map.Entry<Integer, RSItemDefinition> def : ITEM_DEFINITIONS.entrySet()) {
            if (def.getValue() == null || !name.equals(def.getValue().getName()))
                continue;
            return def.getKey();
        }
        return -1;
    }

    /**
     * This method may cause the game to crash, so it is safe to only call this while logged out.
     *
     * @param client the {@link RSClient} native instance
     */
    public static void load(RSClient client) {
        loadObjectDefinitions(client);
        loadNpcDefinitions(client);
        loadItemDefinitions(client);
    }

    private static boolean loadObjectDefinitions(RSClient client) {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadObjectDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, RSObjectDefinition> data = new HashMap<>();
            for (int i = 0; i < Short.MAX_VALUE; i++) {
                RSObjectDefinition raw = client.loadObjectDefinition(i);
                if (raw != null) {
                    String name = raw.getName();
                    if (name != null && name.equals(NULL)) {
                        raw = raw.transform();
                        if (raw == null)
                            continue;
                    }
                    data.put(i, raw);
                }
            }
            OBJECT_DEFINITIONS.putAll(data);
            data.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean loadNpcDefinitions(RSClient client) {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadNpcDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, RSNpcDefinition> data = new HashMap<>();
            for (int i = 0; i < 20000; i++) {
                RSNpcDefinition raw = client.loadNpcDefinition(i);
                if (raw != null) {
                    String name = raw.getName();
                    if (name != null) {
                        if (name.equals(NULL)) {
                            raw = raw.transform();
                            if (raw == null)
                                continue;
                        }
                        data.put(i, raw);
                    }
                }
            }
            NPC_DEFINITIONS.putAll(data);
            data.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static boolean loadItemDefinitions(RSClient client) {
        InvokeHook invoke = ModScript.serveInvoke("Client#loadItemDefinition");
        if (invoke == null)
            return false;
        try {
            Map<Integer, RSItemDefinition> data = new HashMap<>();
            for (int i = 0; i < 15000; i++) {
                RSItemDefinition raw = client.loadItemDefinition(i);
                if (raw != null) {
                    String name = raw.getName();
                    if (name != null && !name.equals(NULL))
                        data.put(i, raw);
                }
            }
            ITEM_DEFINITIONS.putAll(data);
            data.clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
