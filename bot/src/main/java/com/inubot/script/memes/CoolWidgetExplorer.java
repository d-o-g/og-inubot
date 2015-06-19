/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.memes;

import com.inubot.RuneDream;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.util.AWTUtil;
import com.inubot.api.util.Paintable;
import com.inubot.script.Script;
import com.inubot.RuneDream;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.util.filter.Filter;
import com.inubot.script.Script;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.*;
import java.util.List;

/**
 * @author unsigned
 * @since 10-05-2015
 */
public class CoolWidgetExplorer extends Script implements Paintable, MouseMotionListener {

    private static final Font BEST_FONT = new Font("Myriad Pro", Font.PLAIN, 14);
    private static final Filter<Widget> VISIBILITY_FILTER = Widget::isVisible;

    private final Map<String, Object> details = new LinkedHashMap<>();
    private final List<Widget> hovered = Collections.synchronizedList(new LinkedList<>());

    private Widget getHovered() {
        return hovered.isEmpty() ? null : hovered.get(hovered.size() - 1);
    }

    @Override
    public int loop() {
        return 600;
    }

    private Widget[] getAll(Filter<Widget> filter) {
        List<Widget> widgets = new ArrayList<>();
        for (Widget widget : Interfaces.getWidgets(filter)) {
            if (widget == null || !filter.accept(widget))
                continue;
            widgets.add(widget);
            for (Widget child : widget.getChildren()) {
                if (child == null || !filter.accept(child))
                    continue;
                widgets.add(child);
            }
        }
        return widgets.toArray(new Widget[widgets.size()]);
    }

    @Override
    public void render(Graphics2D g) {
        Widget curr = getHovered();
        if (curr == null)
            return;
        details.put("Index", curr.getIndex());
        details.put("UID", curr.getId());
        details.put("ParentIndex", curr.getId() << 16);
        details.put("ParentUID", curr.getParentHash());
        details.put("BoundsArrayIndex", curr.getBoundsArrayIndex());
        details.put("Visible", curr.isVisible());
        details.put("ItemID", curr.getItemId());
        details.put("ItemQuantity", curr.getItemQuantity());
        details.put("TableIDs", Arrays.toString(curr.getItemIds()));
        details.put("TableQuantities", Arrays.toString(curr.getItemQuantities()));
        details.put("Insets", curr.getInsetX() + ", " + curr.getInsetY());
        details.put("Bounds", curr.getBounds());
        details.put("Type", curr.getType());
        details.put("TextureID", curr.getTextureId());
        details.put("Text", curr.getText());
        details.put("Actions", Arrays.toString(curr.getActions()));
        details.put("-------------------------*", "*-------------------------");
        details.put("HoveredWidgetCount", hovered.size());
        Rectangle bounds = curr.getBounds();
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
        g.setFont(BEST_FONT);
        int y = 10;
        for (Map.Entry<String, Object> entry : details.entrySet())
            AWTUtil.drawBoldedString(g, entry.getKey() + ": " + entry.getValue(), 15, y += 15, Color.CYAN);
    }

    @Override
    public boolean setup() {
        RuneDream.getInstance().getCanvas().addMouseMotionListener(this);
        return true;
    }

    @Override
    public void onFinish() {
        RuneDream.getInstance().getCanvas().removeMouseMotionListener(this);
    }

    @Override
    public void mouseDragged(MouseEvent e) {

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        hovered.clear();
        for (Widget widget : getAll(VISIBILITY_FILTER)) {
            if (widget.getBounds().contains(e.getPoint()))
                hovered.add(widget);
        }
    }
}

