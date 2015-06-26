package com.inubot.script.others.test;

import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.util.Paintable;
import com.inubot.script.Script;

import java.awt.*;

/**
 * @author Septron
 * @since June 26, 2015
 */
public class Continue extends Script implements Paintable {

    @Override
    public void render(Graphics2D g) {

    }

    @Override
    public int loop() {
        if (Interfaces.canContinue())
            Interfaces.clickContinue();
        return 600;
    }
}
