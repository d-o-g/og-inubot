package me.mad.modules;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.LogoutTab;
import com.inubot.api.methods.Varps;
import com.inubot.api.util.Time;
import me.mad.MadTutorial;
import me.mad.util.interfaces.Module;

/**
 * Created by me.mad on 7/26/15.
 */
public class Done implements Module {
    @Override
    public boolean validate() {
        return !Game.isLoggedIn() ||  Varps.get(281) >= 1000; //TODO get var
    }

    @Override
    public void execute() {
        if (Game.isLoggedIn()) {
            MadTutorial.nextAcc();
            while (Game.isLoggedIn()) {
                LogoutTab.logout();
                Time.sleep(3000);
            }
        }
    }
}
