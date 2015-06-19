package com.inubot.client;

import com.inubot.api.methods.Client;
import com.inubot.api.util.Paintable;
import com.inubot.api.util.Random;
import com.inubot.api.util.Time;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class GameCanvas extends Canvas {

    public static final int INPUT_MOUSE = 0x2;
    public static final int INPUT_KEYBOARD = 0x4;
    public final List<Paintable> paintables = new LinkedList<>();
    private final BufferedImage raw;
    private final BufferedImage backBuffer;
    public int input = INPUT_MOUSE | INPUT_KEYBOARD;
    public int mouseX = 0, mouseY = 0;

    public GameCanvas() {
        this.raw = new BufferedImage(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height, BufferedImage.TYPE_INT_ARGB);
        this.backBuffer = new BufferedImage(Toolkit.getDefaultToolkit().getScreenSize().width,
                Toolkit.getDefaultToolkit().getScreenSize().height, BufferedImage.TYPE_INT_ARGB);
        requestFocusInWindow();
    }

    private static int getLocation(final char ch) {
        if (ch >= KeyEvent.VK_SHIFT && ch <= KeyEvent.VK_ALT) {
            return Random.nextInt(KeyEvent.KEY_LOCATION_LEFT,
                    KeyEvent.KEY_LOCATION_RIGHT + 1);
        }
        return KeyEvent.KEY_LOCATION_STANDARD;
    }

    private static synchronized char getKeyChar(final char c) {
        return c >= 36 && c <= 40 ? KeyEvent.VK_UNDEFINED : c;
    }

    @Override
    public void processEvent(AWTEvent e) {
        super.processEvent(e);
    }

    @Override
    public Graphics getGraphics() {
        Graphics g = super.getGraphics();
        Graphics2D paint = backBuffer.createGraphics();
        paint.drawImage(raw, 0, 0, null);
        if (!Client.PAINTING)
            return raw.createGraphics();
        paintables.forEach(p -> p.render(paint));
        paint.dispose();
        g.drawImage(backBuffer, 0, 0, null);
        g.dispose();
        return raw.createGraphics();
    }

    private void push(AWTEvent evt) {
        if (!evt.getSource().equals(this)) {
            super.dispatchEvent(evt);
            return;
        }
        if (!evt.getSource().equals("bot")) {
            if (evt instanceof MouseEvent && (input & INPUT_MOUSE) == 0
                    || evt instanceof KeyEvent && (input & INPUT_KEYBOARD) == 0) {
                return;
            }
        }
        super.dispatchEvent(evt);
    }

    private AWTEvent mask(AWTEvent e) {
        e.setSource("bot");
        return e;
    }

    private AWTEvent generateMouseEvent(int type, int x, int y, int button, int timeOffset) {
        return mask(new MouseEvent(this, type, System.currentTimeMillis() + timeOffset, 0, x, y,
                button != MouseEvent.MOUSE_MOVED ? 1 : 0, false, button));
    }

    private AWTEvent generateMouseEvent(int type, int x, int y, int button) {
        return generateMouseEvent(type, x, y, button, 0);
    }

    public void setMouseLocation(Point p) {
        setMouseLocation(p.x, p.y);
    }

    public void setMouseLocation(int x, int y) {
        push(generateMouseEvent(MouseEvent.MOUSE_MOVED, (mouseX = x), (mouseY = y), MouseEvent.NOBUTTON));
    }

    public void pressMouse(boolean left) {
        push(generateMouseEvent(MouseEvent.MOUSE_PRESSED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3));
    }

    public void releaseMouse(boolean left) {
        int offset = Random.nextInt(20, 30);
        push(generateMouseEvent(MouseEvent.MOUSE_RELEASED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
        push(generateMouseEvent(MouseEvent.MOUSE_CLICKED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
    }

    public void clickMouse(boolean left) {
        pressMouse(left);
        releaseMouse(left);
    }

    public void scrollMouse(boolean up) {
        push(new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0,
                mouseX, mouseY, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, up ? -1 : 1));
    }

    private KeyEvent getKeyEvent(final int id, final int modifiers, final int code, final char c, final int location) {
        return (KeyEvent) mask(new KeyEvent(this, id, System.currentTimeMillis(), modifiers, code, c, location));
    }

    public synchronized void sendKey(final char c, int delay) {
        final AWTKeyStroke keystroke = AWTKeyStroke.getAWTKeyStroke(c);
        int keycode = keystroke.getKeyCode();
        if (c >= 'a' && c <= 'z')
            keycode -= 32;
        push(new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, 0, keycode, c, KeyEvent.KEY_LOCATION_STANDARD));
        push(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis() + 10, 0, keycode, c, KeyEvent.KEY_LOCATION_STANDARD));
        push(new KeyEvent(this, KeyEvent.KEY_TYPED, System.currentTimeMillis() + 10, 0, KeyEvent.VK_UNDEFINED, c, KeyEvent.KEY_LOCATION_UNKNOWN));
    }

    private KeyEvent generateKeyEvent(char key, int type, int wait) {
        AWTKeyStroke ks = AWTKeyStroke.getAWTKeyStroke(key);
        int code = ks.getKeyCode();
        if (key >= 'a' && key <= 'z')
            code -= 32;
        return new KeyEvent(this, type, System.currentTimeMillis() + wait, ks.getModifiers(), code, key, KeyEvent.KEY_LOCATION_STANDARD);
    }

    private KeyEvent generateKeyEvent(char key, int type) {
        return generateKeyEvent(key, type, 0);
    }

    public synchronized void sendKeys(final char[] chars, final int delay) {
        for (final char _char : chars) {
            sendKey(_char, delay);
        }
    }

    public synchronized void sendKeys(final char[] chars) {
        for (final char _char : chars) {
            sendKey(_char, 30);
        }
    }

    public synchronized void pressEventKey(final int eventKey, final int millis) {
        push(new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis() + millis, 0, eventKey, (char) eventKey, KeyEvent.KEY_LOCATION_STANDARD));
        push(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis() + millis, 0, eventKey, (char) eventKey, KeyEvent.KEY_LOCATION_STANDARD));
    }

    public synchronized void pressKey(final int keycode) {
        pressKey(keycode, 0);
    }

    public synchronized void pressKey(final int keycode, final int delay) {
        int mask = 0;
        switch (keycode) {
            case KeyEvent.VK_SHIFT: {
                mask = KeyEvent.SHIFT_MASK;
                break;
            }
            case KeyEvent.VK_ALT: {
                mask = KeyEvent.ALT_MASK;
                break;
            }
            case KeyEvent.VK_CONTROL: {
                mask = KeyEvent.CTRL_MASK;
                break;
            }
        }
        push(new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), mask, keycode, KeyEvent.CHAR_UNDEFINED));
        Time.sleep(delay);
    }

    public synchronized void pressEnter() {
        pressEventKey(KeyEvent.VK_ENTER, 20);
    }

    /**
     * @param text       The text to send.
     * @param pressEnter <tt>true</tt> to press enter
     * @param minDelay   The minimum time in milliseconds to wait between sending a character
     * @param maxDelay   The maximum time in milliseconds to wait before sending a character
     */
    public synchronized void sendText(final String text, final boolean pressEnter, final int minDelay, final int maxDelay) {
        final char[] chars = text.toCharArray();
        for (final char element : chars) {
            final int delay = Random.nextInt(minDelay, maxDelay);
            sendKey(element, delay > 0 ? delay : 1);
        }
        if (pressEnter)
            pressEnter();
    }

    /**
     * @param text       The text to send.
     * @param pressEnter <tt>true</tt> to press enter
     */
    public synchronized void sendText(final String text, final boolean pressEnter) {
        sendText(text, pressEnter, 30, 80);
    }

    public synchronized void releaseKey(final char c) {
        releaseKey(c, 0, 0);
    }

    public synchronized void releaseKey(final char ch, final int delay, final int mask) {
        releaseKey(ch, ch, delay, mask);
    }

    public synchronized void releaseKey(final int keycode) {
        int mod = 0;
        switch (keycode) {
            case KeyEvent.VK_SHIFT: {
                mod = KeyEvent.SHIFT_MASK;
                break;
            }
            case KeyEvent.VK_ALT: {
                mod = KeyEvent.ALT_MASK;
                break;
            }
            case KeyEvent.VK_CONTROL: {
                mod = KeyEvent.CTRL_MASK;
                break;
            }
        }
        KeyEvent event = new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), mod, keycode, KeyEvent.CHAR_UNDEFINED);
        push(event);
    }

    public synchronized void releaseKey(final char ch, final int code, final int delay, final int mask) {
        push(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay, mask, code, getKeyChar(ch), getLocation(ch)));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}

