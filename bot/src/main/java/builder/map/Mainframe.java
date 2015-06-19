package builder.map;


import builder.map.thread.MapScheduler;

import javax.swing.*;
import java.awt.*;

public class Mainframe extends JFrame {

    public Mainframe(MapScheduler applet_sub1, int with, int height) {
        applet = applet_sub1;
        setTitle("ImpSoft MapViewer PRO");
        setResizable(false);
        show();
        toFront();
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        resize(with + 8, height + 28);
    }

    public final void paint(Graphics graphics) {
        applet.paint(graphics);
    }

    public final void update(Graphics graphics) {
        applet.update(graphics);
    }

    public final Graphics getGraphics() {
        Graphics graphics = super.getGraphics();
        graphics.translate(4, 24);
        return graphics;
    }

    public MapScheduler applet;
}
