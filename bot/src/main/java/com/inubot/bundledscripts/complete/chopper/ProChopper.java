package com.inubot.bundledscripts.complete.chopper;

import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Movement;
import com.inubot.api.methods.traversal.Path.Option;
import com.inubot.api.methods.traversal.graph.WebPath;
import com.inubot.api.methods.traversal.graph.data.WebBank;
import com.inubot.api.oldschool.GameObject;
import com.inubot.api.oldschool.Tile;
import com.inubot.api.oldschool.event.MessageEvent;
import com.inubot.api.oldschool.event.MessageEvent.Type;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.bundledscripts.proframework.ProScript;
import com.inubot.script.Manifest;

import java.util.Map;

@Manifest(name = "ProChopper BETA", developer = "blitz", desc = "Progressive chopping!")
public class ProChopper extends ProScript {

    private final ChopperController controller;

    public ProChopper() {
        this.controller = new ChopperController(new ChopperView(), new ChopperModel());
    }

    @Override
    public boolean setup() {
        controller.getView().display();
        while (true) {
            Time.sleep(800);
            if (controller.getView().isDisposable()) {
                break;
            }
        }
        controller.getView().dispose();
        return true;
    }

    @Override
    public void getPaintData(Map<String, Object> data) {
        data.put("Logs chopped", controller.getModel().getLogsChopped());
    }

    @Override
    public int loop() {
        for (Progression p : controller.getModel().getProgressionType().getProgressFlow()) {
            if (p.canProgress()) {
                Location location = Location.getClosestWithTree(p.next);
                if (Inventory.isFull()) {
                    WebBank b = Movement.getWeb().getNearestBank(t -> t.getType() != WebBank.Type.DEPOSIT_BOX);
                    if (b.getLocation().distance() > 10) {
                        try {
                            WebPath wp = Movement.getWeb().findPathToBank(b);
                            wp.step(Option.TOGGLE_RUN);
                        } catch (Exception e) {
                            Movement.walkTo(b.getLocation());
                        }
                    } else if (!Bank.isOpen()) {
                        Bank.open();
                    } else {
                        Bank.depositAllExcept(new IdFilter<>(Axe.getIds()));
                    }
                } else if (Players.getLocal().getAnimation() == -1) {
                    if (location.getTreeArea().contains(Players.getLocal())) {
                        GameObject tree = GameObjects.getNearest(p.next.getName());
                        if (tree != null) {
                            tree.processAction("Chop-down");
                        }
                    } else {
                        Tile dest = location.getTreeArea().getStart().derive(location.getTreeArea().getWidth() / 2, 0);
                        try {
                            WebPath wp = WebPath.build(dest);
                            wp.step(Option.TOGGLE_RUN);
                        } catch (Exception e) {
                            Movement.walkTo(dest);
                        }
                    }
                }
            }
        }
        return 900;
    }

    @Override
    public void messageReceived(MessageEvent e) {
        if (e.getType() != Type.PLAYER && e.getText().toLowerCase().contains("logs")) {
            controller.getModel().incrementLogsChopped();
        }
    }
}
