package me.septron.onyx.nodes;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Players;
import com.inubot.api.oldschool.Tile;
import me.septron.framework.Node;
import me.septron.onyx.Pepperjack;

/**
 * @author Septron
 * @since July 31, 2015
 */
public class SellRunes implements Node {

	@Override
	public boolean validate() {
		return Game.isLoggedIn() && Inventory.getCount("Chaos rune") < (Pepperjack.ONYX_PER_TRIP * 28889)
				|| (Players.getLocal().distance(Pepperjack.TOKTZ_CAVE) < 300 && Inventory.contains("Chaos rune"));
	}

	@Override
	public String status() {
		return "Selling runes";
	}

	@Override
	public int execute() {
		return 0;
	}
}
