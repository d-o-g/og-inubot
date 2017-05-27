package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.awt.*;
import java.util.Map;

/**
 * Created by Asus on 25/05/2017.
 */
@Manifest(name = "1t Karambwans", developer = "Dogerina", desc = "Cooks karambwans")
public class Karambwans1t extends ProScript {

    private int actions = 0;

    private int cycle = 0;

    @Override
    public boolean setup() {
        setLineColor(Color.orange);
        getTickTasks().add(() -> {
            //1 server cycle = ~600 ms
            //24 client ticks in 600 ms
            //
            if (++cycle >= 18) {
                cycle = 0; //if we dont reset, cycle will go > max int
                WidgetItem karambwan = Inventory.getLast("Raw karambwan");
                if (karambwan == null) {
                    if (!Bank.isOpen()) {
                        Bank.open();
                    } else if (Inventory.contains("Cooked karambwan") || Inventory.contains("Burnt karambwan") || Inventory.isEmpty()) {
                        Bank.depositInventory();
                        Bank.withdrawAll("Raw karambwan");
                        Bank.close();
                    }
                }
                if (karambwan != null) {
                    if (Interfaces.get(303, 1) != null && Interfaces.get(303, 1).isVisible()) {
                        Client.processAction(Action.valueOf(30, 0, -1, 19857419), "", "");
                    }
                    if (Inventory.getCount() == 28 || (Interfaces.get(303, 1) != null && Interfaces.get(303, 1).isVisible())) {
                        GameObject fire = GameObjects.getNearest(o -> o.getX() == 3043 && o.getY() == 4973 && o.getName().contains("Fire"));
                        if (fire != null) {
                            karambwan.use(fire);
                        }
                    }
                }
            }
        });
        return true;
    }

    @Override
    public int loop() {
        return 1000;
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        data.put("Actions", actions);
        data.put("Actions/hr", stopWatch.getHourlyRate(actions));
    }

    public void messageReceived(MessageEvent e) {
        if (e.getText().contains("the karambwan")) {
            actions++;
        }
    }
}
