package me.septron.onyx;

import com.inubot.api.oldschool.Area;
import com.inubot.api.oldschool.Tile;
import com.inubot.script.Manifest;
import me.septron.framework.Node;
import me.septron.framework.SScript;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Septron
 * @since July 24, 2015
 */
@Manifest(name = "Pepperjack", developer = "Septron", desc = "")
public class Pepperjack extends SScript {

	private final List<Node> accepted = new ArrayList<>();
	private final List<Node> nodes = new ArrayList<>();

	public static final Area GRAND_EXCHANGE = new Area(new Tile(0, 0, 0), new Tile(0, 0, 0));
	public static final Area AURUBYS_SHOP = new Area(new Tile(0, 0, 0), new Tile(0, 0, 0));
	public static final Area ONYX_SHOP = new Area(new Tile(0, 0, 0), new Tile(0, 0, 0));
	public static final Area RUNE_SHOP = new Area(new Tile(0, 0, 0), new Tile(0, 0, 0));

	public static final Tile VARROCK_CENTER = new Tile(3212,3429,0);
	public static final Tile TOKTZ_CAVE = new Tile(2480, 5175, 0);

	public static final int ONYX_PER_TRIP = 1;

	private Node node = null;

	@Override
	public boolean setup() {
		super.painter.submit(() -> {
			String status = "Status: ";
			if (node != null)
				return status + node.status();
			return status + "UNKNOWN";
		});
		return true;
	}

	private int handle() {
		Node selected = null;
		for (Node node : accepted) {
			if (selected == null)
				selected = node;
			else
			if (selected.priority() < node.priority())
				selected = node;
		}
		assert selected != null;
		return selected.execute();
	}

	@Override
	public int loop() {
		if (nodes.size() == 0)
			return -1;
		accepted.addAll(
				nodes.stream().filter(
						Node::validate
				).collect(Collectors.toList())
		);
		if (accepted.size() > 0) {
			return handle();
		}
		return 600;
	}

	@Override
	public String name() {
		return "Pepperjack";
	}
}
