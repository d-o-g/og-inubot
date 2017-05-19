import com.inubot.api.methods.*;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.awt.*;
import java.util.Map;

/**
 * Created by Asus on 13/05/2017.
 */
@Manifest(name = "Thiever2", developer = "Dogerina", desc = "Thiefs")
public class Thief extends ProScript {

    @Override
    public boolean setup() {
        setLineColor(Color.CYAN);
        return true;
    }

    @Override
    public int loop() {
        Inventory.dropAllExcept(item -> {
            String n = item.getName().toLowerCase();
            return n.contains("glory") || n.contains("coin") || n.contains("stam");
        });

        GameObject stall = GameObjects.getNearest("Fruit Stall");
        if (stall != null && stall.containsAction("Steal-from")) {
            stall.processAction("Steal-from");
        }
        return 200;
    }

    @Override
    public void getPaintData(Map<String, Object> data) {

    }
}
