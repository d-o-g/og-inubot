package me.mad.modules;

import
        com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Players;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.util.Time;
import com.inubot.bundledscripts.complete.Combot;
import me.mad.Tutorial;
import me.mad.util.interfaces.Module;


/**
 * Created by me.mad on 7/25/15.
 */
public class Combat implements Module {

    @Override
    public boolean validate() {
        return Tutorial.setting() < 510;
    }

    @Override
    public void execute() {

        if(!Tutorial.isChatOpen()) {
            switch (Tutorial.setting()) {
                case 370:
                    Tutorial.interact("Combat Instructor", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 390:
                    Tutorial.openTab(Tab.EQUIPMENT);
                    break;
                case 400:
                    Widget combatStats = Interfaces.getWidget(387,17);
                    combatStats.processAction("View equipment stats");
                    break;
                case 405:
                    WidgetItem dagger = Inventory.getFirst("Bronze dagger");
                    if(dagger !=null) {
                        dagger.processAction("Wield");
                        Time.await(() -> Tutorial.setting() != 405, 1200);
                    }
                    break;
                case 410:
                    Tutorial.interact("Combat Instructor", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 420:
                    WidgetItem shield = Inventory.getFirst("Wooden shield");
                    WidgetItem sword = Inventory.getFirst("Bronze sword");
                    if(shield !=null) {
                        shield.processAction("Wield");
                        Time.await(() -> shield == null, 1200);
                    }
                    if(sword !=null) {
                        sword.processAction("Wield");
                        Time.await(() -> sword == null, 1200);
                    }
                    break;
                case 430:
                    Tutorial.openTab(Tab.COMBAT);
                    break;
                case 440:
                    Tutorial.interactGB("Gate", "Open", new Tile(3111, 9518));
                    break;
                case 450:
                    if(!Players.getLocal().isHealthBarVisible()) {
                        Tutorial.interact("Giant rat", "Attack");
                        Time.await(Tutorial::isAnimating, 1200);
                    }
                    break;
                case 460:
                    if(!Players.getLocal().isHealthBarVisible()) {
                        Tutorial.interact("Giant rat", "Attack");
                        Time.await(Tutorial::isAnimating, 1200);
                    }
                    break;
                case 470:
                    if(Movement.isReachable(new Tile(3107, 9509))) {
                        Tutorial.interact("Combat Instructor", "Talk-to");
                        Time.await(Tutorial::isChatOpen, 1850);
                    } else Tutorial.interactGB("Gate", "Open", new Tile(3111, 9518));
                    break;
                case 480:
                    WidgetItem bow = Inventory.getFirst("Shortbow");
                    WidgetItem arrow = Inventory.getFirst("Bronze arrow");
                    if(bow !=null) {
                        bow.processAction("Wield");
                        Time.await(() -> bow == null, 1200);
                    }
                    if(arrow !=null) {
                        arrow.processAction("Wield");
                        Time.await(() -> arrow == null, 1200);
                    }
                    if(bow == null && arrow == null) {
                        Tutorial.interact("Giant rat", "Attack");
                    }
                    break;
                case 490:
                    com.inubot.api.methods.Combat.setStyle(1);
                    Tutorial.interact("Giant rat", "Attack");
                    break;
                case 500:
                    Tutorial.interactGB("Ladder", "Climb-up");
                    Time.await(() -> Tutorial.setting() != 500, 1200);
            }
        } else Tutorial.continueChat();
    }
}
