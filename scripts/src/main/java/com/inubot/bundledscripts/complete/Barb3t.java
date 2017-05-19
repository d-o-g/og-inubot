package com.inubot.bundledscripts.complete;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Npcs;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Npc;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Random;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

/**
 * Created by Asus on 09/05/2017.
 */
@Manifest(name = "3 tick barb", desc = "", developer = "Dogerina")
public class Barb3t extends ProScript {

    @Override
    public int loop() {
        if (Game.isLoggedIn()) {
            WidgetItem claws = Inventory.getFirst("Guam leaf", "Marrentill");
            WidgetItem vambs = Inventory.getFirst("Swamp tar");
            Npc spot = Npcs.getNearest("Fishing spot");
            WidgetItem extra = Inventory.getFirst(new NameFilter<>(true, "Grimy"));
            WidgetItem fish = Inventory.getFirst(new NameFilter<>(true, "Leap"));
            if (fish != null) {
                fish.drop();
            }
            if (claws != null && vambs != null) {
                if (spot != null) {
                    int anim = Players.getLocal().getAnimation();
                    if (anim == 623) { //fishing
                        vambs.use(claws);
                    }
                    if (anim == -1 || anim == 5243 || anim == 5249 || anim == 829) { //idle, crafting, herbtar or eating
                        if (!Players.getLocal().isMoving()) {
                            spot.processAction("Use-rod");
                            if (spot.distance() > 1) {
                                Time.await(() -> Players.getLocal().isMoving(), 3000);
                            }
                        }
                    }
                }
            } else if (claws == null && extra != null) {
                extra.processAction("Clean");
                Time.sleep(1000, 2000);
            } else if (spot != null && Players.getLocal().getAnimation() == -1) {
                if (!Players.getLocal().isMoving()) {
                    spot.processAction("Use-rod");
                    if (spot.distance() > 1) {
                        Time.await(() -> Players.getLocal().isMoving(), 3000);
                    }
                }
            }
        }
        return Random.nextInt(200, 400);
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        data.put("Animation", Players.getLocal().getAnimation());
    }
}
