package com.inubot.script.bytehound;

import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.oldschool.action.tree.InputButtonAction;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.methods.*;

import java.awt.*;

/**
 * Created by Cameron on 2015-04-30.
 */
public class Miner extends Script implements Paintable {

    private static final Filter<Widget> LOBBY_FILTER = w -> w.getText() != null && w.getText().equals("Play RuneScape");
    public static final Filter<Widget> DIALOGUE_FILTER = w -> w.getText() != null && (w.getText().equals("Click here to continue") || w.getText().equals("Sure, I'll give it a go."));

    @Override
    public int loop() {
        if (Interfaces.getWidgets(LOBBY_FILTER).length > 0) {
            for (Widget widget : Interfaces.getWidgets(DIALOGUE_FILTER)) {
                if (widget.getText() == null)
                    continue;
                Client.processAction(new InputButtonAction(widget.getId()), "Play RuneScape", "");
            }
        }
        if (Skills.getLevel(Skill.MINING) > 14)
            Toolkit.getDefaultToolkit().beep();
        if (Game.isLoggedIn()) {
            if (Inventory.isFull()) {
                Inventory.dropAll(wi -> wi.getName().equals("Tin ore"));
            } else if (Players.getLocal().getAnimation() == -1) {
                GameObject gameObject = GameObjects.getNearest(gameObject1 -> gameObject1.getId() == 13447 || gameObject1.getId() == 13448 || gameObject1.getId() == 13449);
                if (gameObject != null) {
                    gameObject.processAction("Mine");
                    Time.sleep(400, 700);
                }
            }
        }
        return 300;
    }

    private final Filter<WidgetItem> ITEM_FILTER = wi -> wi.getName().contains("ore");

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "AutoMiner", 20, 20, Color.MAGENTA);
        for (GameObject gameObject : GameObjects.getLoaded()) {
            Point p = Projection.locatableToViewport(gameObject);
            if (Projection.isOnViewport(p)) {
                AWTUtil.drawBoldedString(g, ""+gameObject.getId(), p.x, p.y, Color.RED);
            }
        }
    }
}
