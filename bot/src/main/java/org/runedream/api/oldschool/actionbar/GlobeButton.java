/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package org.runedream.api.oldschool.actionbar;

import org.runedream.CtrlBind;
import org.runedream.api.methods.ActionBar;

import javax.swing.*;
import java.awt.*;

/**
 * @author unsigned
 * @since 17-05-2015
 */
public class GlobeButton extends ActionNode {

    private static final ImageIcon GLOBE_IMAGE;
    private static final ImageIcon GLOBE_HOVER_IMAGE;
    private static final Rectangle GLOBE;

    static {
        GLOBE_IMAGE = new ImageIcon(ActionBar.class.getClassLoader().getResource("res/globe.png"));
        GLOBE_HOVER_IMAGE = new ImageIcon(ActionBar.class.getClassLoader().getResource("res/globe_hover.png"));
        GLOBE = new Rectangle(482, 309, 26, 25);
    }

    public GlobeButton() {
        super(GLOBE);
    }

    @Override
    public void render(Graphics2D g) {
        if (isHovered()) {
            g.drawImage(GLOBE_HOVER_IMAGE.getImage(), 482, 309, null);
        } else {
            g.drawImage(GLOBE_IMAGE.getImage(), 482, 309, null);
        }
    }

    @Override
    public void select(AWTEvent e) {
        CtrlBind.MAP_VIEW.onActivation();
    }
}
