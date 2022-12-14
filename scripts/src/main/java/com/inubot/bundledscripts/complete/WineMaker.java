package com.inubot.bundledscripts.complete;

import com.inubot.Hotkey;
import com.inubot.api.methods.*;
import com.inubot.api.methods.Bank;
import com.inubot.api.methods.Inventory;
import com.inubot.api.oldschool.Item;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.script.Manifest;
import com.inubot.script.Script;

import java.awt.*;

@Manifest(name = "WineMaker", developer = "", desc = "Makes wine for over 450k cooking experience per hour!")
public class WineMaker extends Script implements Paintable {

    private final StopWatch stopWatch = new StopWatch(0);
    private int xp;

    @Override
    public boolean setup() {
        super.getTickTasks().add(Interfaces::processContinue);
        this.xp = Skills.getExperience(Skill.COOKING);
        return true;
    }

    @Override
    public int loop() {
        if (Inventory.getCount("Grapes") == 0 || Inventory.getCount("Jug of water") == 0) {
            if (!Bank.isOpen()) {
                Hotkey.OPEN_NEAREST_BANK_OBJECT.onActivation();
                return 800;
            }
            Bank.depositInventory();
            Item a = Bank.getFirst(new IdFilter<>(1937));
            Item b = Bank.getFirst(new IdFilter<>(1987));
            if (a != null && b != null) {
                a.processAction("Withdraw-14");
                b.processAction("Withdraw-14");
            }
        } else {
            if (Bank.isOpen()) {
                Bank.close();
                return 600;
            }
            Item src = Inventory.getFirst("Grapes");
            Item dst = Inventory.getFirst("Jug of water");
            if (src != null && dst != null) {
                src.use(dst);
                if (Time.await(() -> Interfaces.getComponent(t -> t.getId() == 20250627) != null, 1500)) {
                    Client.processAction(0, -1, 20250627, 30, "Make All", "", 50, 50);
                    //lame hack, interaction never fails so no fucks given anyway
                    if (Time.await(() -> Inventory.getCount() == 14, 17000)) {
                        return 500;
                    }
                }
            }
        }
        return 700;
    }

    private boolean isInidle() {
        for (int i = 0; i < 15; i++) {
            if (Players.getLocal().getAnimation() != -1)
                return true;
            Time.sleep(25);
        }
        return false;
    }

    @Override
    public void render(Graphics2D g) {
        AWTUtil.drawBoldedString(g, "PerfectCooker PRO", 20, 40, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Runtime: " + stopWatch.toElapsedString(), 20, 60, Color.YELLOW);
        AWTUtil.drawBoldedString(g, "Exp: " + (Skills.getExperience(Skill.COOKING) - xp), 20, 80, Color.YELLOW);
    }
}
