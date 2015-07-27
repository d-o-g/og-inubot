package me.septron.framework.painting;

import com.inubot.api.util.AWTUtil;

import java.awt.*;

/**
 * @author Septron
 * @since July 26, 2015
 */
public class Dropper {

	private class Drop {

		private long a = System.currentTimeMillis();
		private long b = System.currentTimeMillis();

		private double y = 220, alpha = 255;

		private int gain;

		public Drop(int gain) {
			this.gain = gain;
		}

		private long delta() {
			long time = System.currentTimeMillis();
			return time - (b = time);
		}

		public boolean render(Graphics2D graphics) {
			if ((System.currentTimeMillis() - a) > 1000)
				a = System.currentTimeMillis();

			long delta = delta();
			alpha -= delta;
			y -= delta;

			String text = String.format("+%s", gain);

			AWTUtil.drawBoldedString(graphics, text, graphics.getFontMetrics().stringWidth(text), (int) y);

			if(y <= 38) {
				return false;
			}
			return true;
		}
	}
}
