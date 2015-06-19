package com.inubot.script.memes.range;

import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.script.memes.Action;

/**
 * Created by Cameron on 2015-04-24.
 */
public class EquipArrows implements Action {

    @Override
    public boolean validate() {
        return Inventory.getFirst(f -> f.getId() == 882) != null && RangeGuild.isGameStarted()
                && RangeGuild.getShotsFired() == 0 && Interfaces.getWidgets(RangeGuild.DIALOGUE_FILTER).length == 0;
    }

    @Override
    public void execute() {
        WidgetItem arrows = Inventory.getFirst(f -> f.getId() == 882);
        if (arrows != null)
            arrows.processAction("Wield");
    }
}
