package me.mad.modules;

import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.oldschool.Tab;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;
import me.mad.Tutorial;
import me.mad.util.interfaces.Module;

import static me.mad.Tutorial.*;


/**
 * Created by me.mad on 7/25/15.
 */
public class Monk implements Module, Tutorial {

    @Override
    public boolean validate() {
        return setting() < 620;
    }

    @Override
    public void execute() {
        if(!isChatOpen()) {
            switch (setting()) {
                case 550:
                    if(Movement.isReachable(new Tile(3126,3107,0))) {
                        interact("Brother Brace", "Talk-to", new Tile(3126,3107,0).derive(1,2));
                        Time.await(Tutorial::isChatOpen, 1200);
                    } else interactGB("Large door", "Open", new Tile(3129, 3107, 0));
                    break;
                case 560:
                    openTab(Tab.PRAYER);
                    break;
                case 570:
                    interact("Brother Brace", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 580:
                    openTab(Tab.FRIENDS_LIST);
                    break;
                case 590:
                    openTab(Tab.IGNORE_LIST);
                    break;
                case 600:
                    interact("Brother Brace", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 610:
                    interactGB("Door", "Open", new Tile(3122,3102));
                    break;
            }

        } else continueChat();
    }
}
