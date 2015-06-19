/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.action.tree;

import org.runedream.api.oldschool.action.ActionOpcodes;
import org.runedream.api.oldschool.action.UID;
import org.runedream.api.util.CacheLoader;
import org.runedream.client.natives.RSObjectDefinition;

public class ObjectAction extends EntityAction {

    public ObjectAction(int opcode, int uid, int localX, int localY) {
        super(opcode, uid, localX, localY);
    }

    public int getActionIndex() {
        return opcode - ActionOpcodes.OBJECT_ACTION_0;
    }

    public int getEntityId() { //Arg0 is the UID, not the direct id of the entity
        return getObjectId();
    }

    public int getUid() {
        return arg0;
    }

    public int getObjectId() {
        return UID.getEntityId(getUid());
    }

    public RSObjectDefinition getDefinition() {
        return CacheLoader.findObjectDefinition(getEntityId());
    }

    public String getName() {
        return getDefinition().getName();
    }

    public String getAction() {
        String[] actions = getDefinition().getActions();
        if (actions == null)
            return null;
        int actionIndex = getActionIndex();
        return actionIndex >= 0 && actionIndex < actions.length ? actions[actionIndex] : null;
    }

    @Override
    public String toString() {
        return "Object Action [object-name(id=" + getEntityId() + ")=" + getName() + ",action-name(index=" +
                getActionIndex() + ")=" + getAction() + ")<" + getX() + "," + getY() + "> on object " /*+ getObject()*/;
    }
}
