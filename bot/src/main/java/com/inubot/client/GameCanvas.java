package com.inubot.client;

import com.inubot.api.methods.*;
import com.inubot.api.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;

public class GameCanvas extends Canvas {

    public static final int INPUT_MOUSE = 0x2;
    public static final int INPUT_KEYBOARD = 0x4;
    public static final List<Paintable> paintables = new LinkedList<>();
    public int input = INPUT_MOUSE | INPUT_KEYBOARD;
    public int mouseX = 0, mouseY = 0;

    public GameCanvas() {
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
    public void addFocusListener(FocusListener listener) {
        super.addFocusListener(new FocusProxy(listener));
    }

    @Override
    public void processEvent(AWTEvent e) {
        super.processEvent(e);
    }

    public void dispatch(AWTEvent evt) {
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
        dispatch(generateMouseEvent(MouseEvent.MOUSE_MOVED, (mouseX = x), (mouseY = y), MouseEvent.NOBUTTON));
    }

    public void pressMouse(boolean left) {
        dispatch(generateMouseEvent(MouseEvent.MOUSE_PRESSED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3));
    }

    public void releaseMouse(boolean left) {
        int offset = Random.nextInt(20, 30);
        dispatch(generateMouseEvent(MouseEvent.MOUSE_RELEASED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
        dispatch(generateMouseEvent(MouseEvent.MOUSE_CLICKED, mouseX, mouseY,
                left ? MouseEvent.BUTTON1 : MouseEvent.BUTTON3, offset));
    }

    public void clickMouse(boolean left) {
        pressMouse(left);
        releaseMouse(left);
    }

    public void scrollMouse(boolean up) {
        dispatch(new MouseWheelEvent(this, MouseEvent.MOUSE_WHEEL, System.currentTimeMillis(), 0,
                mouseX, mouseY, 0, false, MouseWheelEvent.WHEEL_UNIT_SCROLL, 1, up ? -1 : 1));
    }

    private KeyEvent getKeyEvent(final int id, final int modifiers, final int code, final char c, final int location) {
        return (KeyEvent) mask(new KeyEvent(this, id, System.currentTimeMillis(), modifiers, code, c, location));
    }

    public synchronized void sendKey(char c, int delay) {
        AWTKeyStroke keystroke = AWTKeyStroke.getAWTKeyStroke(c);
        int keycode = keystroke.getKeyCode();
        if (c >= 'a' && c <= 'z') {
            keycode -= 32;
        }
        dispatch(new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay, 0, keycode, c, KeyEvent.KEY_LOCATION_STANDARD));
        dispatch(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis() + 10, 0, keycode, c, KeyEvent.KEY_LOCATION_STANDARD));
        dispatch(new KeyEvent(this, KeyEvent.KEY_TYPED, System.currentTimeMillis() + 10, 0, KeyEvent.VK_UNDEFINED, c, KeyEvent.KEY_LOCATION_UNKNOWN));
    }

    private KeyEvent generateKeyEvent(char key, int type, int wait) {
        AWTKeyStroke ks = AWTKeyStroke.getAWTKeyStroke(key);
        int code = ks.getKeyCode();
        if (key >= 'a' && key <= 'z') {
            code -= 32;
        }
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

    public synchronized void sendKeys(char[] chars) {
        for (char _char : chars) {
            sendKey(_char, 30);
        }
    }

    public synchronized void pressEventKey(int eventKey, int millis) {
        dispatch(new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis() + millis, 0, eventKey, (char) eventKey, KeyEvent.KEY_LOCATION_STANDARD));
        dispatch(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis() + millis, 0, eventKey, (char) eventKey, KeyEvent.KEY_LOCATION_STANDARD));
    }

    public synchronized void pressKey(int keycode) {
        pressKey(keycode, 0);
    }

    public synchronized void pressKey(int keycode, int delay) {
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
        dispatch(new KeyEvent(this, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), mask, keycode, KeyEvent.CHAR_UNDEFINED));
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
    public synchronized void sendText(String text, boolean pressEnter, int minDelay, int maxDelay) {
        char[] chars = text.toCharArray();
        for (char element : chars) {
            int delay = Random.nextInt(minDelay, maxDelay);
            sendKey(element, delay > 0 ? delay : 1);
        }
        if (pressEnter) {
            pressEnter();
        }
    }

    /**
     * @param text       The text to send.
     * @param pressEnter <tt>true</tt> to press enter
     */
    public synchronized void sendText(String text, boolean pressEnter) {
        sendText(text, pressEnter, 30, 80);
    }

    public synchronized void releaseKey(char c) {
        releaseKey(c, 0, 0);
    }

    public synchronized void releaseKey(char ch, int delay, int mask) {
        releaseKey(ch, ch, delay, mask);
    }

    public synchronized void releaseKey(int keycode) {
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
        dispatch(event);
    }

    public synchronized void releaseKey(char ch, int code, int delay, int mask) {
        dispatch(new KeyEvent(this, KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay, mask, code, getKeyChar(ch), getLocation(ch)));
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }


    /**
     * @author Dogerina
     *         Wraps the original FocusListener into a FocusProxy.
     *         This class is a proxy {@link java.awt.event.FocusListener} for receiving focus events on a component.
     *         The clients {@link java.awt.event.FocusListener} is replaced with this one, and
     *         this {@link java.awt.event.FocusListener} is registered to the component when
     *         {@link java.awt.Component#addFocusListener(java.awt.event.FocusListener)} is invoked.
     * @see java.awt.event.FocusListener
     * @see java.awt.event.FocusEvent
     */
    final class FocusProxy implements FocusListener {

        private final FocusListener original;

        public FocusProxy(FocusListener original) {
            this.original = original;
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (!Game.getClient().asApplet().hasFocus()) {
                getOriginal().focusGained(e);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            //do nothing, we never lose focus
        }

        public FocusListener getOriginal() {
            return original;
        }

        /**
         * Dispatches a focusLost event to the component
         *
         * @param e The {@link java.awt.event.FocusEvent} to dispatch
         */
        public void loseFocus(FocusEvent e) {
            getOriginal().focusLost(e);
        }

        /**
         * Dispatches a focusLost event to the src component with the given event id
         *
         * @param src The source component
         * @param id  The id of the {@link java.awt.event.FocusEvent}
         */
        public void loseFocus(Component src, int id) {
            loseFocus(new FocusEvent(src, id));
        }

        /**
         * Dispatches a focusLost event to the component with the given event id
         *
         * @param id The id of the {@link java.awt.event.FocusEvent}
         */
        public void loseFocus(int id) {
            loseFocus(new FocusEvent(Game.getCanvas(), id));
        }

        /**
         * Dispatches a focusGained event to the component
         *
         * @param e The {@link java.awt.event.FocusEvent} to dispatch
         */
        public void gainFocus(FocusEvent e) {
            getOriginal().focusGained(e);
        }

        /**
         * Dispatches a focusGained event to the src component with the given event id
         *
         * @param src The source component
         * @param id  The id of the {@link java.awt.event.FocusEvent}
         */
        public void gainFocus(Component src, int id) {
            gainFocus(new FocusEvent(src, id));
        }

        /**
         * Dispatches a focusGained event to the component with the given event id
         *
         * @param id The id of the {@link java.awt.event.FocusEvent}
         */
        public void gainFocus(int id) {
            gainFocus(new FocusEvent(Game.getCanvas(), id));
        }
    }
}
