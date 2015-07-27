package me.septron.framework;

import com.inubot.api.methods.Skills;
import com.inubot.api.util.Paintable;
import com.inubot.script.Script;
import me.septron.framework.painting.Painter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Septron
 * @since July 25, 2015
 */
public abstract class SScript extends Script implements Paintable {

	protected Painter painter;

	private final List<Goal> goals;

	public abstract String name();

	public SScript() {
		this.goals = new ArrayList<>();
		this.painter = new Painter(name());
	}

	public void provide(Goal... goals) {
		for(Goal goal : goals) {
			if(!this.goals.contains(goal))
				this.goals.add(goal);
		}
	}

	public void remove(Goal... goals) {
		for(Goal goal : goals) {
			if(this.goals.contains(goal))
				this.goals.remove(goal);
		}
	}

	public boolean check() {
		Iterator<Goal> iterator = this.goals.iterator();
		while(iterator.hasNext()) {
			Goal goal = iterator.next();
			if(goal.target >= Skills.getCurrentLevel(goal.skill)) {
				goal.event.onGoalReached();
				this.remove(goal);
				return true;
			}
		}
		return false;
	}

	@Override
	public void render(Graphics2D g) {
		painter.render(g);
	}
}
