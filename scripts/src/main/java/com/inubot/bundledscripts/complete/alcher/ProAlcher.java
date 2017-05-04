package com.inubot.bundledscripts.complete.alcher;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Skill;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.tree.SelectableSpellButtonAction;
import com.inubot.api.oldschool.action.tree.TableAction;
import com.inubot.api.util.StopWatch;
import com.inubot.api.util.filter.Filter;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

@Manifest(
        name = "ProAlcher",
        developer = "blitz",
        desc = "Alchs for decent magic experience. Start with only runes and the item to alch in your inventory"
)
public class ProAlcher extends ProScript implements AlcherConstants {

    private static final Filter<WidgetItem> NATURE_FILTER = (i -> i.getName().contains("rune"));
    private static final Filter<WidgetItem> OTHER_FILTER = (i -> !NATURE_FILTER.accept(i) && !i.getName().contains("Coins"));

    @Override
    public void getPaintData(Map<String, Object> data) {
        int expGained = getTrackedSkill(Skill.MAGIC).getGainedExperience();
        int hourlyExp = getStopWatch().getHourlyRate(expGained);
        int expToLvl = Skills.getExperienceAt(Skills.getLevel(Skill.MAGIC) + 1) - Skills.getExperience(Skill.MAGIC);
        data.put(EXP_TL_KEY, expToLvl);
        data.put(TTL_KEY, Skills.getLevel(Skill.MAGIC) == 99 ? "Maxed!"
                : hourlyExp > 0 ? StopWatch.format(expToLvl * 360 / hourlyExp * 10000) : "00:00:00");
        data.put(ALCHS_KEY, expGained / (Skills.getLevel(Skill.MAGIC) < 55 ? 31 : 65));
        data.put(ALCHS_PH_KEY, getStopWatch().getHourlyRate(expGained / (Skills.getLevel(Skill.MAGIC) < 55 ? 31 : 65)));
        data.put(ALCHS_TL, expToLvl / (Skills.getLevel(Skill.MAGIC) < 55 ? 31 : 65));
    }

    @Override
    public int loop() {
        if (!Game.isLoggedIn()) {
            return 1200;
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

    @Override
    public boolean setup() {
        Client.setWidgetRendering(false);
        return true;
    }

    @Override
    public void onFinish() {
        Client.setWidgetRendering(true);
    }
}
