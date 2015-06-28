package builder.map;


import builder.map.cache.ArchiveKeys;
import builder.map.cache.DataArchive;
import builder.map.thread.MapScheduler;

import java.awt.*;

public class Mainframe extends Frame {

    public Mainframe(MapScheduler applet_sub1, int with, int height) {
        applet = applet_sub1;
        setTitle( DataArchive.getValue( ArchiveKeys.TITTLE_NAME ));
        setResizable(false);
        show();
        toFront();
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
