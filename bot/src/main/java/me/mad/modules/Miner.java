package me.mad.modules;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.util.Random;
import com.inubot.api.util.Time;
import me.mad.Tutorial;
import me.mad.util.interfaces.Module;

import static me.mad.Tutorial.*;


/**
 * Created by mad on 7/25/15.
 */
public class Miner implements Module, Tutorial {

    @Override
    public boolean validate() {
        return setting() < 370;
    }

    @Override
    public void execute() {
        if (!isChatOpen()) {
            switch (setting()) {

                case 260:
                    interact("Mining Instructor", "Talk-to", new Tile(3081, 9506).derive(1,2));
                    Time.await(Tutorial::isChatOpen, Random.nextInt(1850,2800));
                    break;
                case 270:
                    interactGB("Rocks", "Prospect", new Tile(3076, 9504, 0));
                    break;
                case 280:
                    interactGB("Rocks", "Prospect", new Tile(3086, 9501, 0));
                    break;
                case 290:
                    interact("Mining Instructor", "Talk-to", new Tile(3081, 9506).derive(1,2));
                    Time.await(Tutorial::isChatOpen, Random.nextInt(1850,2800));
                    break;
                case 300:
                    interactGB("Rocks", "Mine", new Tile(3076, 9504, 0));
                    break;
                case 310:
                    interactGB("Rocks", "Mine", new Tile(3086, 9501, 0));
                    break;
                case 320:
                    GameObject furnace = GameObjects.getNearest("Furnace");
                    if (Inventory.contains("Copper ore") && Inventory.contains("Tin ore")) {
                        useItemOn("Copper ore", furnace);
                    } else {
                        if (!Inventory.contains("Tin ore")) interactGB("Rocks", "Mine", new Tile(3076, 9504, 0));
                        if (!Inventory.contains("Copper ore")) interactGB("Rocks", "Mine", new Tile(3086, 9501, 0));
                    }
                    Time.await(Tutorial::isAnimating, 1200);
                    break;
                case 330:
                    interact("Mining Instructor", "Talk-to");
                    Time.await(Tutorial::isChatOpen, Random.nextInt(1850,2800));
                    break;
                case 340:
                    GameObject anvil = GameObjects.getNearest("Anvil");
                    useItemOn("Bronze bar", anvil);
                    Time.await(() -> setting() != 340, 1200);
                    break;
                case 350:
                    Widget dagger = Interfaces.getWidget(312, 2);
                    if (dagger != null && dagger.isVisible()) {
                        dagger.processAction("Smith 1");
                    }
                    break;
                case 360:
                    interactGB("Gate", "Open");
                    Time.await(() -> setting() != 360, 1200);
            }
        } else continueChat();
    }
}
