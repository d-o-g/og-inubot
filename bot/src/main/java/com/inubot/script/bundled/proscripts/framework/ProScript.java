/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.script.bundled.proscripts.framework;

import com.inubot.api.util.Paintable;
import com.inubot.script.Script;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Dogerina
 * @since 16-07-2015
 */
public abstract class ProScript extends Script implements Paintable {

    private final Map<String, Object> paintData;

    private static final int BASE_PAINT = 6;
    private static final int HEIGHT = 20;

    public ProScript() {
        this.paintData = new LinkedHashMap<>();
    }

    public abstract String getTitle();
    public abstract void getPaintData(Map<String, Object> data);

    @Override
    public final void render(Graphics2D graphics) {
        getPaintData(paintData);
        int widest = 0;
        for (Map.Entry<String, Object> entry : paintData.entrySet()) {
            String data = entry.getKey() + ": " + entry.getValue().toString();
            int width = graphics.getFontMetrics().stringWidth(data);
            if (width > widest) {
                widest = width;
            }
        }
        int dataLen = paintData.size() + 1;
        graphics.setColor(Color.GREEN);
        graphics.setStroke(new BasicStroke(3.0f));
        graphics.drawRect(10, 10, widest + BASE_PAINT, BASE_PAINT + (HEIGHT * dataLen));
        graphics.setColor(Color.BLACK);
        graphics.setComposite(AlphaComposite.SrcOver.derive(0.7f));
        graphics.fillRect(11, 11, widest + BASE_PAINT - 1, BASE_PAINT + (HEIGHT * dataLen) - 1);
        graphics.setColor(Color.WHITE);
        graphics.drawString(getTitle(), 13, BASE_PAINT + HEIGHT);
        graphics.setColor(Color.GREEN);
        graphics.drawLine(12, 13 + HEIGHT, widest + BASE_PAINT + 8, 13 + HEIGHT);
        graphics.setColor(Color.WHITE.darker());
        int index = 2;
        for (Map.Entry<String, Object> entry : paintData.entrySet()) {
            String data = entry.getKey() + ": " + entry.getValue().toString();
            graphics.drawString(data, 13, BASE_PAINT + (HEIGHT * index));
            index++;
        }
    }
}
