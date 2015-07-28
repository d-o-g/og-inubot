/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.Inubot;
import com.inubot.api.exceptions.ExchangeClosedException;
import com.inubot.api.methods.traversal.Path.Option;
import com.inubot.api.methods.traversal.graph.WebPath;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.GameObject.Landmark;
import com.inubot.api.util.Time;
import com.inubot.client.natives.oldschool.RSGrandExchangeOffer;

public class GrandExchange {

    private static final Tile LOCATION = new Tile(3164, 3487);
    private static final int PARENT = 465;
    private static final int BASE_SLOT = 6;
    //slots are [465][6-13]
    //buttons are children of [465][n] (grandchildren)
    private static final int ITEM_INDEX = 18; //[465][n].children[18] = item image widget
    private static final int RESULTS_PARENT = 162;
    private static final int RESULTS_CONTAINER = 38;

    public static void open(boolean collection) {
        if (isOpen())
            return;
        GameObject booth = GameObjects.getNearest(Landmark.GRAND_EXCHANGE); //"Grand exchange booth"
        if (booth != null) {
            booth.processAction(collection ? "Collect" : "Exchange");
        } else {
            WebPath path = WebPath.build(LOCATION);
            path.step(Option.TOGGLE_RUN); //assume called in loop, so only do 1 action each call..
        }
    }

    public static RSGrandExchangeOffer[] getOffers() {
        return Game.getClient().getGrandExchangeOffers();
    }

    public static void open() {
        open(false);
    }

    /**
     * @param slot the slot number, 0-7 inclusive
     * @return <b>true</b> if the slot contains an offer, <b>false</b> otherwise
     */
    public static boolean containsOffer(int slot) {
        Widget slotWidget = Interfaces.getWidget(PARENT, BASE_SLOT + slot);
        if (slotWidget != null && slotWidget.getChildren().length > ITEM_INDEX) {
            Widget slotItem = slotWidget.getChildren()[ITEM_INDEX];
            if (slotItem.getItemId() != -1)
                return true;
        }
        return false;
    }

    /**
     * throws an {@link com.inubot.api.exceptions.ExchangeClosedException} if the exchange interface was closed
     *
     * @return -1 if no slot is available, otherwise the first slot with no offer
     */
    public static int getFirstAvailableSlot() {
        if (!isOpen())
            throw new ExchangeClosedException();
        for (int slot = 0; slot < 7; slot++) {
            if (!containsOffer(slot))
                return slot;
        }
        return -1;
    }

    public static Widget getFirstAvailableSlotWidget() {
        int first = getFirstAvailableSlot();
        if (first == -1)
            return null;
        return Interfaces.getWidget(PARENT, BASE_SLOT + first);
    }

    public static boolean createSellOffer() {
        Widget widget = getFirstAvailableSlotWidget();
        if (widget != null) {
            widget = widget.getChild(t -> t.containsAction("Create Sell offer"));
            if (widget != null) {
                widget.processAction("Create Sell offer");
                Time.sleep(400); //wait for new interface to open...
                return true;
            }
        }
        return false;
    }

    private static boolean createBuyOffer() {
        Widget widget = getFirstAvailableSlotWidget();
        if (widget != null) {
            widget = widget.getChild(t -> t.containsAction("Create Buy offer"));
            if (widget != null) {
                widget.processAction("Create Buy offer");
                Time.sleep(400); //wait for new interface to open...
                return true;
            }
        }
        return false;
    }

    /**
     * throws an {@link com.inubot.api.exceptions.ExchangeClosedException} if the exchange interface was closed
     *
     * @param name the item name
     * @return <b>true</b> if success, <b>false</b> otherwise
     */
    public static boolean placeBuyOffer(String name, int quantity, int price) {
        if (!isOpen())
            throw new ExchangeClosedException();
        if (!createBuyOffer())
            return false;
        for (char c : name.toCharArray()) {
            Inubot.getInstance().getCanvas().sendKey(c, 20);
            Inubot.getInstance().getCanvas().pressEnter();
        }
        Time.sleep(800);
        Widget results = Interfaces.getWidget(RESULTS_PARENT, RESULTS_CONTAINER);
        if (results != null) {
            Widget match = results.getChild(t -> name.equals(t.getText()));
            if (match != null) {
                Mouse.setLocation(match.getX(), match.getY());
                Mouse.click(true);
                Time.sleep(400);
                int currPrice = getCurrentItemPrice();
                int currQuantity = -1; //TODO
                if (currPrice == price && currQuantity == quantity) {
                    //click submit
                    return true;
                } else {
                    //correct the price/quantity
                }
                return true;
            }
        }
        return false;
    }

    private static Widget getPriceAccumulator() {
        return null; //TODO
    }

    private static Widget getPriceDecumulator() {
        return null; //TODO
    }

    private static int getCurrentItemPrice() {
        Widget widget = Interfaces.getWidget(PARENT, 23);
        if (widget != null) {
            widget = widget.getChild(t -> t.getText() != null && t.getText().contains("coins"));
            if (widget != null) {
                //TODO get price from text
            }
        }
        return -1;
    }

    public static boolean isOpen() {
        return Interfaces.validate(PARENT);
    }
}
