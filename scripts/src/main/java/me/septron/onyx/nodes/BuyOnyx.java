package me.septron.onyx.nodes;

import com.inubot.api.methods.Game;
import com.inubot.api.methods.Inventory;
import com.inubot.api.methods.Players;
import me.septron.framework.Node;
import me.septron.onyx.Pepperjack;

/**
 * @author Septron
 * @since July 31, 2015
 */
public class BuyOnyx implements Node {
	@Override
	public boolean validate() {
		return Game.isLoggedIn() && Inventory.getCount("Tokkul") > 260000 && !Inventory.contains("Chaos rune");
	}

	@Override
	public String status() {
		return "Buying onyx";
	}

	@Override
	public int execute() {
		if (Players.getLocal().distance(Pepperjack.TOKTZ_CAVE) < 300) {
			if (Pepperjack.ONYX_SHOP.contains(Players.getLocal())) {
				if (true) { // TODO: Check if shop is open
					//TODO: Get the item Uncut onyx
					//TODO: if (onyx != null)
						//TODO: if (onyx.stack > 0)
							//TODO: Buy onyx
						//TODO: else hop world
				} else {
					//TODO: Trade TzHaar-Hur-Lek
				}
			}
		} else {
			//TODO: Walk to the shop
		}
		return 600;
	}
}
