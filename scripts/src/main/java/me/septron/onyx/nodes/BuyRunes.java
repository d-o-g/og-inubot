package me.septron.onyx.nodes;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Inventory;
import me.septron.framework.Node;
import me.septron.onyx.Pepperjack;

/**
 * @author Septron
 * @since July 31, 2015
 */
public class BuyRunes implements Node {

	@Override
	public boolean validate() {
		return Game.isLoggedIn() && Inventory.getCount("Chaos rune") < (Pepperjack.ONYX_PER_TRIP * 28889) && Inventory.getCount("Coins") > 10000;
	}

	@Override
	public String status() {
		return "Buying runes";
	}

	@Override
	public int execute() {
		return 0;
	}
}
