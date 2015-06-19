package org.runedream.script.memes;

import org.runedream.api.methods.GroundItems;
import org.runedream.api.methods.Inventory;
import org.runedream.api.oldschool.GroundItem;
import org.runedream.api.oldschool.WidgetItem;
import org.runedream.api.util.Time;
import org.runedream.api.util.filter.Filter;
import org.runedream.script.Script;

import java.awt.image.FilteredImageSource;

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
