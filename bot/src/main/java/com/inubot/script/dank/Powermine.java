package com.inubot.script.dank;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.Tile;
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

    private static final int[] ids = new int[] {14883, 14864};

    @Override
    public int loop() {
        if (Inventory.isFull()) {
            Inventory.dropAll(Filter.always());
        }
        if (Players.getLocal().getAnimation() == -1) {
            GameObject rock = GameObjects.getNearest(new IdFilter<GameObject>(ids));
            rock.processAction("Mine");
            return Skills.getCurrentLevel(Skill.MINING) > 15 ? 1000 : 2000;
        }
        return 600;
    }

    @Override
    public void render(Graphics2D g) {

    }
}
