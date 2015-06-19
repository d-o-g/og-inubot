package com.inubot.script.memes;

import com.inubot.api.methods.GroundItems;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.GroundItem;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;
import com.inubot.api.methods.GroundItems;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.GroundItem;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;

/**
 * Created by luckruns0ut on 26/04/15.
 */
public class ItemGrabber extends Script {
    private GroundItem g;

    @Override
    public int loop() {
        try {
            g = GroundItems.getNearest("Logs");
            if(g != null) {
                g.processAction(20, "Take");
                Time.sleep(2000);
            }

            WidgetItem widgetItem = Inventory.getFirst(new Filter<WidgetItem>() {
                @Override
                public boolean accept(WidgetItem widgetItem) {
                    return widgetItem.getName().equals("Logs");
                }
            });
            if(widgetItem != null) {
                widgetItem.processAction("Drop");
                Time.sleep(2000);
            }
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return 500;
    }
}
