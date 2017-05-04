package com.inubot.api.oldschool.skill;

import com.inubot.api.util.Identifiable;

public interface Resource extends Identifiable {

    /**
     * @return <b>true</b> if this {@link com.inubot.api.oldschool.skill.Resource} is required to be equipped
     */
    default boolean isEquipment() {
        return false;
    }

    default int getQuantity() {
        return 1;
    }
}
