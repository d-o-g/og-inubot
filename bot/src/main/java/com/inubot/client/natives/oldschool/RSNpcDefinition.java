package com.inubot.client.natives.oldschool;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Varps;
import com.inubot.client.Artificial;

public interface RSNpcDefinition extends RSDoublyLinkedNode {

    String getName();
    String[] getActions();
    int getId();
    int getVarpIndex();
    int getVarpBitIndex();
    int[] getTransformIds();

    @Artificial
    default RSNpcDefinition transform() {
        int[] trans = getTransformIds();
        if (trans == null) {
            return null;
        }

        int index = -1;
        if (getVarpBitIndex() != -1) {
            RSVarpBit v = Varps.getBit(index);
            if (v != null) {
                index = v.getValue();
            }
        } else if (getVarpIndex() != -1) {
            index = Varps.get(getVarpIndex());
        }

        if (index >= 0 && index < trans.length && trans[index] != -1) {
            return Game.getClient().loadNpcDefinition(trans[index]);
        }
        return null;
    }
}
