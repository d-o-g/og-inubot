package com.inubot.api.methods.exchange;

import com.inubot.api.methods.*;
import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.util.CacheLoader;
import com.inubot.api.methods.exchange.GrandExchange.OfferType;

interface Offer {

    default boolean place(OfferType offerType, String name, int quantity, int price) {
        if (GrandExchange.getEmptySlot() == null) {
            return false;
        }
        if (offerType == OfferType.BUY && !GrandExchange.isSlotOpen()) {
            GrandExchange.openRandomSlot();
        }
        if (!isItemAcceptable(name)) {
            changeItem(offerType, name);
        }
        if (isItemAcceptable(name) && !isPriceAcceptable(price)) {
            changePrice(price);
        }
        if (isItemAcceptable(name) && isPriceAcceptable(price) && !isQuantityAcceptable(quantity)) {
            changeQuantity(quantity);
        }
        if (isItemAcceptable(name) && isPriceAcceptable(price) && isQuantityAcceptable(quantity)) {
            confirmOffer();
        }
        return new GrandExchangeOffer(name).exists();
    }

    default boolean place(OfferType offerType, String name, int quantity) {
        if (GrandExchange.getEmptySlot() == null) {
            return false;
        }
        if (offerType == OfferType.BUY && !GrandExchange.isSlotOpen()) {
            GrandExchange.openRandomSlot();
        }
        if (!isItemAcceptable(name)) {
            changeItem(offerType, name);
        }
        if (isItemAcceptable(name) && !isQuantityAcceptable(quantity)) {
            changeQuantity(quantity);
        }
        if (isItemAcceptable(name) && isQuantityAcceptable(quantity)) {
            confirmOffer();
        }
        return new GrandExchangeOffer(name).exists();
    }


    default boolean isItemAcceptable(String name) {
        return Varps.get(1151) == CacheLoader.itemIdFor(name);
    }

    default boolean isPriceAcceptable(int price) {
        return Varps.get(1043) == price;
    }

    default boolean isQuantityAcceptable(int quantity) {
        Widget c = Interfaces.getWidget(465, 23);
        if (c == null) {
            return false;
        }
        Widget child = c.getChild(widget -> widget.getIndex() == 32);
        if (child == null || child.getText() == null) {
            return false;
        }
        String amt = child.getText().replace(",", "");
        return Integer.parseInt(amt) == quantity;
    }

    default Widget getCorrectBuyItem(Widget widgets, String name) {
        for (Widget s : widgets.getChildren()) {
            if (s.getText().equals(name)) {
                return s;
            }
        }
        return null;
    }

    default Widget getInventoryItem(String item) {
        Widget c = Interfaces.getWidget(467, 0);
        if (c == null) {
            return null;
        }
        for (Widget child : c.getChildren()) {
            if (child.getItemId() == CacheLoader.itemIdFor(item) + 1) {
                return child;
            }
        }
        return null;
    }


    default boolean changeItem(OfferType offerType, final String name) {
        switch (offerType) {
            case BUY:
                Widget enterItemBox = Interfaces.getWidget(162, 33);
                if (enterItemBox != null && !enterItemBox.isHidden()) {
                    Widget c = Interfaces.getWidget(162, 33);
                    if (c.getText().contains(name)) {
                        Widget item = getCorrectBuyItem(Interfaces.getWidget(162, 38), name);
                        if (item == null) {
                            return false;
                        }
                        Mouse.setLocation((int) item.getBounds().getX(), (int) item.getBounds().getY());
                        Mouse.click(true);
                    } else Game.getCanvas().sendText(name, false);
                } else Client.processAction(Action.valueOf(57, 1, 0, 30474263), "Choose item", "");
                break;

            case SELL:
                Widget item = getInventoryItem(name);
                if (item == null) {
                    return false;
                }
                int y = (int) item.getBounds().getCenterY();
                int x = (int) item.getBounds().getCenterX();
                Mouse.setLocation(x, y);
                Mouse.click(true);
                break;
        }
        return isItemAcceptable(name);
    }

    default boolean changeQuantity(int quantity) {
        if (Interfaces.getWidget(162, 32).isHidden()) {
            Client.processAction(Action.valueOf(57, 1, 7, 30474263), "Enter quantity", "");
        } else Game.getCanvas().sendText(String.valueOf(quantity), true, 50, 100);
        return isQuantityAcceptable(quantity);
    }

    default boolean changePrice(int price) {
        if (Interfaces.getWidget(162, 32).isHidden()) {
            Client.processAction(Action.valueOf(57, 1, 12, 30474263), "Enter price", "");
        } else Game.getCanvas().sendText(String.valueOf(price), true, 50, 100);
        return isPriceAcceptable(price);
    }

    default boolean confirmOffer() {
        Client.processAction(Action.valueOf(57, 1, -1, 30474266), "Confirm", "");
        return GrandExchange.isMainScreenOpen();
    }

}