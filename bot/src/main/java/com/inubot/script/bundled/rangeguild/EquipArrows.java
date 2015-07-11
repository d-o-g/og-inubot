package com.inubot.script.bundled.rangeguild;

import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.action.ActionOpcodes;

/**
 * Created by Cameron on 2015-04-24.
 */
public class EquipArrows implements Action {

    @Override
    public boolean validate() {
        return Inventory.getFirst(f -> f.getId() == 882) != null && Interfaces.getWidgets(RangeGuild.DIALOGUE_FILTER).length == 0;
    }

    @Override
    public void execute() {
        WidgetItem arrows = Inventory.getFirst(f -> f.getId() == 882);
        if (arrows != null)
            arrows.processAction(ActionOpcodes.ITEM_ACTION_1, "Wield");
    }
}
