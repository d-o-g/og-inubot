package me.septron;

import com.inubot.api.methods.Skills;
import com.inubot.api.oldschool.Skill;
import com.inubot.script.Manifest;
import me.septron.framework.Goal;
import me.septron.framework.GoalEvent;
import me.septron.framework.SScript;
import me.septron.framework.painting.Tracker;

/**
 * @author Septron
 * @since July 24, 2015
 */
@Manifest(name = "Pepperjack", developer = "Septron", desc = "Does tutorial island!")
public class Pepperjack extends SScript {

	private int start_xp = Skills.getExperience(Skill.ATTACK);

	@Override
	public boolean setup() {


		painter.submit(new Tracker() {
			@Override
			public String get() {
				return "";
			}
		});

		provide(new Goal(Skill.WOODCUTTING, 15, new GoalEvent() {

			@Override
			public void onGoalReached() {

			}
		}));
		return true;
	}

	@Override
	public int loop() {
		return 0;
	}

	@Override
	public String name() {
		return "Pepperjack";
	}
}
