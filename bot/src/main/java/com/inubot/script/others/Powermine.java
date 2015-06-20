package com.inubot.script.others;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.filter.Filter;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Dank Memes
 * @since June 18, 2015
 */
public class Powermine extends Script implements Paintable {

    private static final int[] TIN  = new int[] {14883, 14864};
    private static final int[] IRON = new int[] {13445, 13446};


    private static final int[] SELECTED = IRON;

    @Override
    public int loop() {
        if(Game.isLoggedIn()) {
            if (Inventory.isFull()) {
                Inventory.dropAll(Filter.always());
            }
            if (Players.getLocal().getAnimation() == -1) {
                GameObject rock = GameObjects.getNearest(new Filter<GameObject>() {
                    @Override
                    public boolean accept(GameObject go) {
                        if (go.distance(Players.getLocal().getLocation()) <= 2) {
                            for (int id : SELECTED) {
                                if (go.getId() == id)
                                    return true;
                            }
                        }
                        return false;
                    }
                });
                if (rock != null)
                    rock.processAction("Mine");
                return 1500;
            } else {

            }
        }
        return 600;
    }

    @Override
    public void render(Graphics2D g) {

    }
}
