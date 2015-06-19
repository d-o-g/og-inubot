package builder.map.thread;

import builder.map.Mainframe;
import builder.map.RSMap;
import builder.map.cache.ArchiveKeys;
import builder.map.cache.DataArchive;
import builder.map.render.sprites.Sprite;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;

public class MapScheduler extends Applet
        implements Runnable, MouseListener, MouseMotionListener, KeyListener, FocusListener, WindowListener,
MouseWheelListener {

    Frame parent;

    public MapScheduler() {
        exitCode = 0;
        refreshPause = 20;
        minimal_delta = 1;
        frameTimes = new long[10];
        fps = 0;
        printInfo = false;
        drawString = new Sprite[6];
        exit = true;
        fillRect = true;
        getFontMetrics = 0;
        anInt99 = 0;
        mouse_x = 0;
        mouse_y = 0;
        getGraphics = 0;
        getKeyChar = 0;
        getKeyCode = 0;
        getX = 0L;
        pressKeyID = 0;
        mousePressLocX = 0;
        mousePressLocY = 0;
        getY = 0L;
        anIntArray105 = new int[128];
        isMetaDown = new int[128];
        repaint = 0;
        setColor = 0;
    }

    public final void stop() {
        if (exitCode >= 0)
            exitCode = 4000 / refreshPause;
    }

    public void render() {
    }

    public final void renderLoadingBar(int percentage, String text) {
        while (graphics == null) {
            graphics = getDisplayedComponent().getGraphics();
            try {
                getDisplayedComponent().repaint();
            } catch (Exception ignored) {
            }
            try {
                Thread.sleep(1000L);
            } catch (Exception ignored) {
            }
        }

        Font font = new Font(DataArchive.getValue( ArchiveKeys.FONT_TYPE_NAME), 0, 13);
        FontMetrics fontmetrics = getDisplayedComponent().getFontMetrics(font);
        if (exit) {
            graphics.setColor(Color.black);
            graphics.fillRect(0, 0, with, height);
            exit = false;
        }
        Color color = new Color(140, 17, 17);
        int y = height / 2 - 18;
        graphics.setColor(color);
        graphics.drawRect(with / 2 - 152, y, 304, 34);
        graphics.fillRect(with / 2 - 150, y + 2, percentage * 3, 30);
        graphics.setColor(Color.black);
        graphics.fillRect((with / 2 - 150) + percentage * 3, y + 2, 300 - percentage * 3, 30);
        graphics.setFont(font);
        graphics.setColor(Color.white);
        graphics.drawString(text, (with - fontmetrics.stringWidth(text)) / 2, y + 22);
    }

    public final void mouseReleased(MouseEvent mouseevent) {
        getFontMetrics = 0;
        anInt99 = 0;
    }

    public final void keyPressed(KeyEvent keyevent) {
        getFontMetrics = 0;
        int i = keyevent.getKeyCode();
        int i_3_ = keyevent.getKeyChar();
        if (i_3_ < 30)
            i_3_ = 0;
        if (i == 37)
            i_3_ = 1;
        if (i == 39)
            i_3_ = 2;
        if (i == 38)
            i_3_ = 3;
        if (i == 40)
            i_3_ = 4;
        if (i == 17)
            i_3_ = 5;
        if (i == 8)
            i_3_ = 8;
        if (i == 127)
            i_3_ = 8;
        if (i == 9)
            i_3_ = 9;
        if (i == 10)
            i_3_ = 10;
        if (i >= 112 && i <= 123)
            i_3_ = (1008 + i) - 112;
        if (i == 36)
            i_3_ = 1000;
        if (i == 35)
            i_3_ = 1001;
        if (i == 33)
            i_3_ = 1002;
        if (i == 34)
            i_3_ = 1003;
        if (i_3_ > 0 && i_3_ < 128)
            anIntArray105[i_3_] = 1;
        if (i_3_ > 4) {
            isMetaDown[setColor] = i_3_;
            setColor = setColor + 1 & 0x7f;
        }
    }

    public final void addThread(Runnable runnable, int priority) {
        Thread thread = new Thread(runnable);
        thread.start();
        thread.setPriority(priority);
        System.out.println("Load");
    }

    public final void windowClosing(WindowEvent windowevent) {
        destroy();
    }

    public final void close() {
        exitCode = -2;
        dispose();
        if (mainFrame != null) {
            try {
                Thread.sleep(1000L);
            } catch (Exception ignored) {
            }
            try {
                System.exit(0);
            } catch (Throwable ignored) {
            }
        }
    }

    public final void update(Graphics graphics) {
        if (this.graphics == null)
            this.graphics = graphics;
        exit = true;
        render();
    }

    public final void mouseEntered(MouseEvent mouseevent1) {
    }

    public final void mouseExited(MouseEvent mouseevent) {
        getFontMetrics = 0;
        mouse_x = -1;
        mouse_y = -1;
    }

    public final void windowOpened(WindowEvent windowevent1) {
    }

    public final void windowDeiconified(WindowEvent windowevent1) {
    }

    public final void windowActivated(WindowEvent windowevent1) {
    }

    public void loadMap() {
    }

    public final void start() {
        if (exitCode >= 0)
            exitCode = 0;
    }

    public final void load(Frame parent, int with, int height) {
        this.parent = parent;
        this.with = with;
        this.height = height;
        graphics = getDisplayedComponent().getGraphics();
        main_overlay = new RenderingEngine(this.with, this.height, getDisplayedComponent());
        addThread(this, 1);
    }

    public final int method42() {
        int i = -1;
        if (setColor != repaint) {
            i = isMetaDown[repaint];
            repaint = repaint + 1 & 0x7f;
        }
        return i;
    }

    public void paint() {


    }

    public final Component getDisplayedComponent() {
        if (parent != null)
            return parent;
        else
            return this;
    }

    public final void mouseClicked(MouseEvent mouseevent1) {
    }

    public final void mousePressed(MouseEvent mouseevent) {
        int mx = mouseevent.getX();
        int my = mouseevent.getY();
        if (mainFrame != null) {
            mx -= 4;
            my -= 22;
        }
        getFontMetrics = 0;
        getKeyChar = mx;
        getKeyCode = my;
        getX = System.currentTimeMillis();
        if (mouseevent.isMetaDown()) {
            getGraphics = 2;
            anInt99 = 2;
        } else {
            getGraphics = 1;
            anInt99 = 1;
        }
    }

    public final void mouseDragged(MouseEvent mouseevent) {
        int i = mouseevent.getX();
        int i_6_ = mouseevent.getY();
        if (mainFrame != null) {
            i -= 4;
            i_6_ -= 22;
        }
        getFontMetrics = 0;
        mouse_x = i;
        mouse_y = i_6_;
    }

    public final void start(int with, int height) {
        this.with = with;
        this.height = height;
        graphics = getDisplayedComponent().getGraphics();
        main_overlay = new RenderingEngine(this.with, this.height, getDisplayedComponent());
        addThread(this, 1);
    }

    public final void mouseMoved(MouseEvent mouseevent) {
        int mouse_x = mouseevent.getX();
        int mouse_y = mouseevent.getY();
        if (mainFrame != null) {
            mouse_x -= 4;
            mouse_y -= 22;
        }
        getFontMetrics = 0;
        this.mouse_x = mouse_x;
        this.mouse_y = mouse_y;
    }

    public final void keyTyped(KeyEvent keyevent1) {
    }

    public final void windowDeactivated(WindowEvent windowevent1) {
    }

    public final void paint(Graphics graphics) {
        if (this.graphics == null)
            this.graphics = graphics;
        exit = true;
        render();
    }

    public final void destroy() {
        exitCode = -1;
        try {
            Thread.sleep(5000L);
        } catch (Exception exception) {
        }
        if (exitCode == -1)
            close();
    }

    public void dispose() {
    }

    public void processEvents() {
    }

    public final void focusLost(FocusEvent focusevent) {
        fillRect = false;
        for (int i = 0; i < 128; i++)
            anIntArray105[i] = 0;

    }

    public final void keyReleased(KeyEvent keyevent) {
        getFontMetrics = 0;
        int i = keyevent.getKeyCode();
        char c = keyevent.getKeyChar();
        if (c < '\036')
            c = '\0';
        if (i == 37)
            c = '\001';
        if (i == 39)
            c = '\002';
        if (i == 38)
            c = '\003';
        if (i == 40)
            c = '\004';
        if (i == 17)
            c = '\005';
        if (i == 8)
            c = '\b';
        if (i == 127)
            c = '\b';
        if (i == 9)
            c = '\t';
        if (i == 10)
            c = '\n';
        if (c > 0 && c < '\200')
            anIntArray105[c] = 0;
    }

    public final void windowClosed(WindowEvent windowevent1) {
    }

    public final void run() {
        getDisplayedComponent().addMouseListener(this);
        getDisplayedComponent().addMouseMotionListener(this);
        getDisplayedComponent().addKeyListener(this);
        getDisplayedComponent().addFocusListener(this);
        getDisplayedComponent().addMouseWheelListener(this);
        if (mainFrame != null)
            mainFrame.addWindowListener(this);
        renderLoadingBar(0, DataArchive.getValue( 730 ));
        System.out.println("Loading map...");
        loadMap();
        System.out.println("Map loaded!");
        int i = 0;
        int ratio = 256;
        int deltaTime = 1;
        int count = 0;
        int failed_frames = 0;
        for (int i_13_ = 0; i_13_ < 10; i_13_++)
            frameTimes[i_13_] = System.currentTimeMillis();

        while (exitCode >= 0) {
            if (exitCode > 0) {
                exitCode--;
                if (exitCode == 0) {
                    close();
                    return;
                }
            }

            int i_14_ = ratio;
            int i_15_ = deltaTime;
            ratio = 300;
            deltaTime = 1;
            long frame_time = System.currentTimeMillis();
            if (frameTimes[i] == 0L) {
                ratio = i_14_;
                deltaTime = i_15_;
            } else if (frame_time > frameTimes[i])
                ratio = (int) ((long) (2560 * refreshPause) / (frame_time - frameTimes[i]));
            if (ratio < 25)
                ratio = 25;
            if (ratio > 256) {
                ratio = 256;
                deltaTime = (int) ((long) refreshPause - (frame_time - frameTimes[i]) / 10L);
            }
            if (deltaTime > refreshPause)
                deltaTime = refreshPause;
            frameTimes[i] = frame_time;
            i = (i + 1) % 10;
            if (deltaTime > 1) {
                for (int i_16_ = 0; i_16_ < 10; i_16_++)
                    if (frameTimes[i_16_] != 0L)
                        frameTimes[i_16_] += deltaTime;

            }
            if (deltaTime < minimal_delta)
                deltaTime = minimal_delta;
            try {
                Thread.sleep(deltaTime);
            } catch (InterruptedException interruptedexception) {
                failed_frames++;
            }
            for (; count < 256; count += ratio) {

                pressKeyID = getGraphics;
                mousePressLocX = getKeyChar;
                mousePressLocY = getKeyCode;

                getY = getX;
                getGraphics = 0;
                processEvents();
                repaint = setColor;
            }

            count &= 0xff;
            if (refreshPause > 0)
                fps = (1000 * ratio) / (refreshPause * 256);


            paint();



            if (printInfo) {
                System.out.println((new StringBuilder()).append(DataArchive.getValue(741)).append(frame_time).toString());
                for (int i_17_ = 0; i_17_ < 10; i_17_++) {
                    int i_18_ = ((i - i_17_ - 1) + 20) % 10;
                    System.out.println((new StringBuilder()).append(DataArchive.getValue(748)).append(i_18_).append(DataArchive.getValue(753)).append(frameTimes[i_18_]).toString());
                }

                System.out.println((new StringBuilder()).append(DataArchive.getValue(755)).append(fps).append(DataArchive.getValue(760)).append(ratio).append(DataArchive.getValue(768)).append(count).toString());
                System.out.println((new StringBuilder()).append(DataArchive.getValue(776)).append(deltaTime).append(DataArchive.getValue(781)).append(refreshPause).append(DataArchive.getValue(791)).append(minimal_delta).toString());
                System.out.println((new StringBuilder()).append(DataArchive.getValue(800)).append(failed_frames).append(DataArchive.getValue(807)).append(i).toString());
                printInfo = false;
                failed_frames = 0;
            }
        }
        if (exitCode == -1)
            close();
    }

    public final void focusGained(FocusEvent focusevent) {
        fillRect = true;
        exit = true;
        render();
    }

    public final void windowIconified(WindowEvent windowevent1) {
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if(e.getWheelRotation() == -1) {   //up
            RSMap map = (RSMap) this;
            if(map.destinationScale < 24) map.destinationScale++;
        } else if(e.getWheelRotation() == 1) {  //Down
            RSMap map = (RSMap) this;
            if(map.destinationScale > 2) map.destinationScale--;
        }
    }

    private static final long serialVersionUID = 0xdb9dc1e88f447a28L;
    public int exitCode;
    public int refreshPause;
    public int minimal_delta;
    public long frameTimes[];
    public int fps;
    public boolean printInfo;
    public int with;
    public int height;
    public Graphics graphics;
    public RenderingEngine main_overlay;
    public Sprite drawString[];
    public Mainframe mainFrame;
    public boolean exit;
    public boolean fillRect;
    public int getFontMetrics;
    public int anInt99;
    public int mouse_x;
    public int mouse_y;
    public int getGraphics;
    public int getKeyChar;
    public int getKeyCode;
    public long getX;
    public int pressKeyID;
    public int mousePressLocX;
    public int mousePressLocY;
    public long getY;
    public int anIntArray105[];
    public int isMetaDown[];
    public int repaint;
    public int setColor;


}
