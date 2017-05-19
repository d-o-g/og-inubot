package me.mad.modules;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;
import me.mad.Tutorial;
import me.mad.util.interfaces.Module;

import static me.mad.Tutorial.*;


/**
 * Created by me.mad on 7/25/15.
 */
public class Chef implements Module, Tutorial {

    @Override
    public boolean validate() {
        return setting() < 183;
    }

    @Override
    public void execute() {
        if(!isChatOpen()) {
            switch (setting()) {

                case 120:
                    interactGB("Gate", "Open");
                    Time.await(() -> setting() != 120, 2800);
                    break;
                case 130:
                    interactGB("Door", "Open");
                    Time.await(() -> setting() != 130, 2800);
                    break;
                case 140:
                    interact("Master Chef", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 150:
                    useItemOn("Pot of flour", "Bucket of water");
                    Time.await(() -> Inventory.contains("Bread dough"), 1200);
                    break;
                case 160:
                    GameObject range = GameObjects.getNearest("Range");
                    useItemOn("Bread dough", range);
                    Time.await(() -> Inventory.contains("Bread"), 2150);
                    break;
                case 170:
                    openTab(Tab.MUSIC_PLAYER);
                    break;
                case 180:
                    interactGB("Door", "Open", new Tile(3072, 3090));
                    Time.await(() -> setting() != 180, 1200);
                    break;

            }
        } else continueChat();
    }
}
