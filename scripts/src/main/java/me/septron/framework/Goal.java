package me.septron.framework;

import com.inubot.api.oldschool.Skill;

/**
 * @author Septron
 * @since July 25, 2015
 */
public class Goal {

	public final Skill skill;
	public final int target;

	public final GoalEvent event;

	public Goal(Skill skill, int target, GoalEvent event) {
		this.skill = skill;
		this.target = target;
		this.event = event;
	}
}