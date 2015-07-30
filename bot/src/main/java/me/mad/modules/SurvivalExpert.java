package me.mad.modules;

import com.inubot.api.methods.GameObjects;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.util.Time;
import me.mad.Tutorial;
import me.mad.util.interfaces.Module;

/**
 * Created by mad on 7/25/15.
 */
public class SurvivalExpert implements Module {

    @Override
    public boolean validate() {
        return Tutorial.setting() < 120;
    }

    @Override
    public void execute() {
        if (!Tutorial.isChatOpen()) {
            switch (Tutorial.setting()) {
                case 20:
                    Tutorial.interact("Survival Expert", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 2400);
                    break;
                case 30:
                    Tutorial.openTab(Tab.INVENTORY);
                    break;
                case 40:
                    Tutorial.interactGB("Tree", "Chop down");
                    Time.await(Tutorial::isAnimating, 1200);
                    break;
                case 50:
                    makeFire(50);
                    break;
                case 60:
                    Tutorial.openTab(Tab.STATS);
                    break;
                case 70:
                    Tutorial.interact("Survival Expert", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 2400);
                    break;
                case 80:
                    Tutorial.interact("Fishing spot", "Net");
                    Time.await(Tutorial::isAnimating, 1850);
                    break;
                case 90:
                    makeFire(90);
                    break;
                case 100:
                    GameObject fire = GameObjects.getNearest("Fire");
                    if(fire !=null) {
                        Tutorial.useItemOn("Raw shrimps", fire);
                        Time.await(Tutorial::isAnimating, 1850);
                    } else makeFire(100);
                    break;
                case 110:
                    GameObject fire1 = GameObjects.getNearest("Fire");
                    if(Inventory.contains("Raw shrimps")) {
                        if (fire1 != null) {
                            Tutorial.useItemOn("Raw shrimps", fire1);
                            Time.await(Tutorial::isAnimating, 1850);

                        } else makeFire(100);
                    } else {
                        Tutorial.interact("Fishing spot", "Net");
                        Time.await(Tutorial::isAnimating, 1850);
                    }
                    break;
            }
        } else Tutorial.continueChat();
    }

    private void makeFire(int setting) {
        GameObject fire = GameObjects.getNearest(gameObject -> {
            return gameObject.getLocation().equals(Players.getLocal().getLocation());
        });

        if(fire != null) {
            Movement.walkTo(Players.getLocal().getLocation().derive(1, 2));
            Time.await(() -> fire == null, 1200);

        } else if(Inventory.contains("Logs") ? Tutorial.useItemOn("Tinderbox", "Logs") : Tutorial.interactGB("Tree", "Chop down")) {
                Time.await(Tutorial::isAnimating, 2200);
            }
            Time.await(() -> Tutorial.setting() != setting, 1200);

    }
}
