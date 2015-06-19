/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.bot.ui;

import javax.swing.*;
import java.awt.*;
import java.util.function.BooleanSupplier;

/**
 * @author unsigned
 * @since 27-04-2015
 */
public class NexusTogglableButton extends JButton {

    private final BooleanSupplier condition;

    public NexusTogglableButton(String text) {
        this(text, () -> true);
        super.setFocusable(false);
    }

    public NexusTogglableButton(String text, BooleanSupplier condition) {
        super(text);
        super.setMinimumSize(new Dimension(getWidth() + 20, getHeight())); //resize for square
        this.condition = condition;
        super.setIcon(new ColoredIcon());
        super.addActionListener(e -> {
            setIcon(new ColoredIcon());
        });
    }

    public boolean isToggled() {
        return condition.getAsBoolean();
    }

    private class ColoredIcon implements Icon {

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            g.setColor(isToggled() ? Color.GREEN : Color.RED);
            g.fillRect(x, y, 10, 10);
        }

        @Override
        public int getIconWidth() {
            return 10;
        }

        @Override
        public int getIconHeight() {
            return 10;
        }
    }
}
