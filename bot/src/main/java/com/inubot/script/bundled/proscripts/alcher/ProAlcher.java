/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.alcher;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.SelectableSpellButtonAction;
import com.inubot.api.oldschool.action.tree.TableAction;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.bundled.proscripts.framework.ProScript;

import java.util.Map;

/**
 * @author Dogerina
 * @since 16-07-2015
 */
public class ProAlcher extends ProScript implements AlcherConstants {

    private static final Filter<WidgetItem> NATURE_FILTER = (i -> i.getName().contains("rune"));
    private static final Filter<WidgetItem> OTHER_FILTER = (i -> !NATURE_FILTER.accept(i));
    private final StopWatch stopWatch;
    private int startExp = -1;

    public ProAlcher() {
        this.stopWatch = new StopWatch(0);
    }

    @Override
    public String getTitle() {
        return "ProAlcher v1.00";
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        data.put(RUNTIME_KEY, stopWatch.toElapsedString());
        int expGained = Skills.getExperience(Skill.MAGIC) - startExp;
        data.put(EXP_KEY, expGained);
        int hourlyExp = stopWatch.getHourlyRate(expGained);
        data.put(EXP_PH_KEY, hourlyExp);
        int expToLvl = Skills.getExperienceAt(Skills.getLevel(Skill.MAGIC) + 1) - Skills.getExperience(Skill.MAGIC);
        data.put(EXP_TL_KEY, expToLvl);
        data.put(TTL_KEY, Skills.getLevel(Skill.MAGIC) == 99 ? "Maxed!"
                : hourlyExp > 0 ? StopWatch.format(expToLvl * 360 / hourlyExp * 10000) : "00:00:00");
        data.put(ALCHS_KEY, expGained / (Skills.getLevel(Skill.MAGIC) < 55 ? 31 : 65));
        data.put(ALCHS_PH_KEY, stopWatch.getHourlyRate(expGained / (Skills.getLevel(Skill.MAGIC) < 55 ? 31 : 65)));
        data.put(ALCHS_TL, expToLvl / (Skills.getLevel(Skill.MAGIC) < 55 ? 31 : 65));
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn()) {
            return 1200;
        }
        if (startExp == -1) {
            startExp = Skills.getExperience(Skill.MAGIC);
        }
        WidgetItem runes = Inventory.getFirst(NATURE_FILTER);
        WidgetItem other = Inventory.getFirst(OTHER_FILTER);
        if (runes != null && other != null) {
            if (Skills.getCurrentLevel(Skill.MAGIC) < 55) {
                Client.processAction(new SelectableSpellButtonAction(14286862), "", "");
            } else {
                Client.processAction(new SelectableSpellButtonAction(14286883), "", "");
            }
            Client.processAction(new TableAction(ActionOpcodes.SPELL_ON_ITEM, other.getId(), other.getIndex(), 9764864), "", "");
        }
        return 600;
    }
}
