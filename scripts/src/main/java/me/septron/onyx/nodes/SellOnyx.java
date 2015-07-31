package me.septron.onyx.nodes;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.exchange.GrandExchange;
import me.septron.framework.Node;

/**
 * @author Septron
 * @since July 31, 2015
 */
public class SellOnyx implements Node {

	private boolean selling() {
		if (GrandExchange.isOpen()) {
			if (GrandExchange.getSlotWithOffer("Uncut onyx") != null) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean validate() {
		return Game.isLoggedIn() && ((Inventory.contains("Uncut onyx") && Inventory.getCount("Tokkul") < 260000) || selling());
	}

	@Override
	public String status() {
		return "Selling onyx";
	}

	@Override
	public int execute() {
		return 0;
	}
}
