package com.inubot.api.methods.exchange;

import com.inubot.api.exceptions.GrandExchangeClosedException;
import com.inubot.api.methods.Interfaces;
import com.inubot.api.methods.Mouse;
import com.inubot.api.oldschool.InterfaceComponent;
import com.inubot.client.natives.oldschool.RSGrandExchangeOffer;
import com.inubot.api.methods.exchange.GrandExchange.Slot;

/**
 * Created by mad on 7/27/15.
 */
public class GrandExchangeOffer {

    private final String offer;

    public GrandExchangeOffer(String offer) {
        this.offer = offer;
    }

    /**
     * @return Offer exists.
     */
    public Slot getSlot() {
        return Slot.getOffer(offer);
    }

    public boolean exists() {
        return getSlot() != null;
    }

    public boolean isOpen() {
        return GrandExchange.isSlotOpen(getSlot());
    }

    public boolean openSlot() {
        GrandExchange.openSlot(GrandExchange.Slot.getOffer(offer));
        return GrandExchange.isSlotOpen(Slot.getOffer(offer));
    }

    private RSGrandExchangeOffer getInternal() {
        return GrandExchange.getGEOffer(offer);
    }

    public int getState() {
        if (getInternal() == null) {
            return -1;
        }
        return GrandExchange.getGEOffer(offer).getState();
    }

    public int getTransferred() {
        if (getInternal() == null) {
            return -1;
        }
        return GrandExchange.getGEOffer(offer).getTransferred();
    }

    public int getQuantity() {
        if (getInternal() == null) {
            return -1;
        }
        return GrandExchange.getGEOffer(offer).getItemQuantity();
    }

    public int getPrice() {
        if (getInternal() == null) {
            return -1;
        }
        return GrandExchange.getGEOffer(offer).getPrice();
    }

    public int getItemId() {
        if (getInternal() == null) {
            return -1;
        }
        return GrandExchange.getGEOffer(offer).getItemId();
    }

    public int getSpent() {
        if (getInternal() == null) {
            return -1;
        }
        return GrandExchange.getGEOffer(offer).getSpent();
    }


    public boolean abort() {
        if (!GrandExchange.isMainScreenOpen()) {
            throw new GrandExchangeClosedException();
        }
        if (!exists() || exists() && !GrandExchange.isSlotOpen(getSlot())) {
            return false;
        }
        InterfaceComponent c = Interfaces.getComponent(465, 21);
        if (c == null) {
            return false;
        }
        InterfaceComponent child = c.getComponent(w -> w.getIndex() == 0);
        if (child != null && !child.isHidden()) {
            Mouse.setLocation((int) child.getBounds().getCenterX(), (int) child.getBounds().getCenterY());
            Mouse.click(true);
        }
        return isCanceled();
    }


    private boolean hasAction(InterfaceComponent child, String action) {
        if (child == null || child.getActions() == null) {
            return false;
        }
        for (String a : child.getActions()) {
            if (a != null && a.equals(action)) {
                return true;
            }
        }
        return false;
    }

    public boolean collect() {
        if (!isOpen()) {
            return false;
        }
        InterfaceComponent c = Interfaces.getComponent(465, 22);
        if (c == null) {
            return false;
        }
        InterfaceComponent[] child = c.getComponents();
        if (child != null) {
            for (InterfaceComponent children : child) {
                if (hasAction(children, "Collect")) {
                    Mouse.setLocation((int) children.getBounds().getCenterX(), (int) children.getBounds().getCenterY());
                    Mouse.click(true);
                }
            }
        }
        return false;
    }

    public boolean isCanceled() {
        if (getInternal() == null) {
            return false;
        }
        return getState() == 5 && getTransferred() != getQuantity();
    }

    public boolean isCompleted() {
        if (getInternal() == null) {
            return false;
        }
        return getTransferred() == getQuantity();
    }
}
