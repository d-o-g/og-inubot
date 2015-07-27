package me.mad.modules;

import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.util.Time;
import me.mad.util.interfaces.Module;

import static me.mad.modules.Tutorial.*;


/**
 * Created by mad on 7/25/15.
 */
public class Bank implements Module, Tutorial {
    @Override
    public boolean validate() {
        return setting() < 550;
    }

    @Override
    public void execute() {
        if(!isChatOpen()) {
            switch (setting()) {
                case 510:
                    if(!Interfaces.isViewingOptionDialog()) {
                        interactGB("Bank booth", "Use");
                        Time.await(Tutorial::isChatOpen, 1200);

                    } else Interfaces.processDialogOption(0);
                break;

                case 520:
                    interactGB("Poll booth", "Use");
                    Time.await(() -> setting() != 520, 1200);
                    break;
                case 525:
                    interactGB("Door", "Open", new Tile(3125, 3124, 0));
                    Time.await(() -> setting() != 525, 1200);
                    break;
                case 530:
                    interact("Financial Advisor", "Talk-to");
                    Time.await(Tutorial::isChatOpen, 1200);
                    break;
                case 540:
                    interactGB("Door", "Open", new Tile(3130, 3124, 0));
                    Time.await(() -> setting() != 540, 1200);
                    break;
            }

        } else continueChat();
    }
}
