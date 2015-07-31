package me.septron.framework.painting;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Septron
 * @since July 25, 2015
 */
public class Painter {

	private final AlphaComposite background = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, .75f);
	private final AlphaComposite foreground = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f);

	private final DecimalFormat format = new DecimalFormat("###,###,###");

	private final Font title = new Font("Helvetica", Font.PLAIN, 16);
	private final Font stats = new Font("Helvetica", Font.PLAIN, 12);

	private final List<Tracker> trackers = new ArrayList<>();

	private final String name;

	public Painter(String name) {
		this.name = name;
	}

	public String format(double i) {
		return this.format.format(i);
	}

	public void submit(Tracker tracker) {
		trackers.add(tracker);
	}

	public void render(Graphics2D g) {
		final FontMetrics fm = g.getFontMetrics(title);
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);

		g.setFont(title);

		g.setColor(Color.BLACK);
		g.setComposite(background);
		g.fillRect(2, 25, 180, 50 + (trackers.size() * 5));

		g.setComposite(foreground);
		g.setColor(Color.GREEN);
		g.drawRect(5, 25, 180, 50 + (trackers.size() * 5));

		final int width = fm.stringWidth(name);
		g.drawString(name, 95 - ((width / 2)), 45);
		g.drawLine(15, 50, 175, 50);

		g.setFont(stats);

		for (int i = 0; i < trackers.size(); i++) {
			Tracker tracker = trackers.get(i);
			g.drawString(tracker.get(), 15, 70 + (i * 20));
		}
	}
}
