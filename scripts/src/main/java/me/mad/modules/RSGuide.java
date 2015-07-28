package me.mad.modules;

import com.inubot.api.oldschool.Tab;
import com.inubot.api.util.Time;
import me.mad.util.interfaces.Module;


/**
 * Created by mad on 7/25/15.
 */
public class RSGuide implements Module {
    @Override
    public boolean validate() {
        return Tutorial.setting() < 20;
    }

    @Override
    public void execute() {
        if (!Tutorial.isChatOpen()) {
            switch (Tutorial.setting()) {
                case 0:
                    Tutorial.interact("RuneScape Guide", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;

                case 3:
                    Tutorial.openTab(Tab.OPTIONS);
                    break;

                case 7:
                    Tutorial.interact("RuneScape Guide", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 10:
                    Tutorial.interactGB("Door", "Open");
                    Time.await(() -> Tutorial.setting() != 10, 1200);
                    break;
            }
        } else Tutorial.continueChat();
    }
}
