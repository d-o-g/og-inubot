package com.inubot.api.methods.exchange;

import com.inubot.api.exceptions.GrandExchangeClosedException;
import com.inubot.api.methods.*;
import com.inubot.api.methods.traversal.Path.Option;
import com.inubot.api.methods.traversal.graph.WebPath;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.GameObject.Landmark;
import com.inubot.api.oldschool.action.tree.Action;
import com.inubot.api.util.CacheLoader;
import com.inubot.api.util.Random;
import com.inubot.client.natives.oldschool.RSGrandExchangeOffer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mad on 7/26/15.
 */
public class GrandExchange {

    private static final Tile LOCATION = new Tile(3164, 3487);

    /**
     *
     * @param collection
     * @return
     */
    public static boolean open(boolean collection) {
        if (isOpen()) {
            return true;
        }
        GameObject booth = GameObjects.getNearest(Landmark.GRAND_EXCHANGE); //"Grand exchange booth"
        if (booth != null) {
            booth.processAction(collection ? "Collect" : "Exchange");
            return true;
        } else {
            WebPath path = WebPath.build(LOCATION);
            path.step(Option.TOGGLE_RUN); //assume called in loop, so only do 1 action each call..
            return false;
        }
    }

    public static boolean open() {
        return open(false);
    }

    public static boolean isMainScreenOpen() {
        InterfaceComponent c = Interfaces.getComponent(465, 2);
        if (c == null) {
            return false;
        }
        InterfaceComponent child = c.getComponent(widget -> widget.getIndex() == 1);
        return child != null && child.getText() != null && child.getText().equals("Grand Exchange");
    }

    public static boolean isOpen() {
        return isSlotOpen() || isMainScreenOpen();
    }

    public static boolean placeOffer(OfferType offerType, String name, int quantity) {
        return new PlaceOffer(offerType, name, quantity).place();
    }

    public static boolean placeOffer(OfferType offerType, String name, int quantity, int price) {
        return new PlaceOffer(offerType, name, quantity, price).place();
    }

    public static boolean collectAll() {
        if (!isMainScreenOpen() || !isOpen()) {
            throw new GrandExchangeClosedException();
        }
        Client.processAction(Action.valueOf(57, 1, 0, 30474245), "Collect to inventory,", "");
        return true;
    }

    public static boolean collectAll(CollectType collectType) {
        if (!isMainScreenOpen() || !isOpen()) {
            throw new GrandExchangeClosedException();
        }

        switch (collectType) {
            case BANK:
                Client.processAction(Action.valueOf(57, 2, 0, 30474245), "Collect to bank,", "");
                break;

            case INVENTORY:
                Client.processAction(Action.valueOf(57, 1, 0, 30474245), "Collect to inventory,", "");
                break;
        }
        return true;
    }

    public static GrandExchangeOffer getOffer(String offer) {
        return new GrandExchangeOffer(offer);
    }

    public static boolean isSlotOpen(final Slot slot) {
        return slot != null && Varps.get(375) == slot.getOpenVarp();
    }

    public static boolean isSlotOpen() {
        return Varps.get(375) > 0;
    }

    public static boolean openRandomSlot() {
        if (GrandExchange.isMainScreenOpen()) {
            if (getEmptySlot() == null) {
                return false;
            }
            Client.processAction(getEmptySlot().getBuyAction(), "Create <col=ff9040>Buy</col> offer,", "");
            return isOpen();
        }
        throw new GrandExchangeClosedException();
    }

    public static boolean isSlotEmpty(Slot slot) {
        return Slot.isEmpty(slot);
    }

    public static boolean openSlot(final Slot slot) {
        if (!isSlotEmpty(slot)) {
            Client.processAction(slot.getOpenAction(), "View offer", "");
        } else {
            Client.processAction(slot.getBuyAction(), "Create <col=ff9040>Buy</col> offer,", "");
        }
        return isSlotOpen(slot);
    }

    public static Slot getEmptySlot() {
        List<Slot> emptySlots = new ArrayList<>();
        for (Slot s : Slot.values()) {
            if (isSlotEmpty(s)) {
                emptySlots.add(s);
            }
        }
        return Random.nextElement(emptySlots);
    }

    public static Slot getSlotWithOffer(String offer) {
        return Slot.getOffer(offer);
    }

    static RSGrandExchangeOffer getGEOffer(String offer) {
        return Slot.getGEOffer(offer);
    }

    public static enum OfferType {
        BUY, SELL
    }

    public static enum CollectType {
        BANK, INVENTORY
    }

    public static enum Slot {
        ONE(Interfaces.getComponent(465, 6),
                Action.valueOf(57, 1, 3, 30474246), //Buy Action
                Action.valueOf(57, 1, 2, 30474246), //Open offer action
                16, false),

        TWO(Interfaces.getComponent(465, 7),
                Action.valueOf(57, 1, 3, 30474247),
                Action.valueOf(57, 1, 2, 30474247), //Open offer action
                32, false),


        THREE(Interfaces.getComponent(465, 8),
                Action.valueOf(57, 1, 3, 30474248),
                Action.valueOf(57, 1, 2, 30474248), //Open offer action
                48, false),

        FOUR(Interfaces.getComponent(465, 9),
                Action.valueOf(57, 1, 3, 30474249),
                Action.valueOf(57, 1, 2, 30474249), //Open offer action
                64, true),

        FIVE(Interfaces.getComponent(465, 10),
                Action.valueOf(57, 1, 3, 30474250),
                Action.valueOf(57, 1, 2, 30474250), //Open offer action
                80, true),

        SIX(Interfaces.getComponent(465, 11),
                Action.valueOf(57, 1, 3, 30474251),
                Action.valueOf(57, 1, 2, 30474251), //Open offer action
                96, true),

        SEVEN(Interfaces.getComponent(465, 12),
                Action.valueOf(57, 1, 3, 30474252),
                Action.valueOf(57, 1, 2, 30474252), //Open offer action
                112, true),

        EIGHT(Interfaces.getComponent(465, 13),
                Action.valueOf(57, 1, 3, 30474253),
                Action.valueOf(57, 1, 2, 30474253), //Open offer action
                128, true);

        private final InterfaceComponent parent;
        private final Action buyAction;
        private final Action openAction;
        private final int openVarp;
        private final boolean members;

        Slot(InterfaceComponent parent, Action buyAction, Action openAction, int openVarp, boolean members) {
            this.parent = parent;
            this.buyAction = buyAction;
            this.openAction = openAction;
            this.openVarp = openVarp;
            this.members = members;
        }

        private static boolean isEmpty(Slot slot) {
            return currentOffers()[slot.ordinal()].getItemId() == 0;
        }

        private static RSGrandExchangeOffer[] currentOffers() {
            return Game.getClient().getGrandExchangeOffers();
        }

        static RSGrandExchangeOffer getGEOffer(final String offer) {
            if (getOffer(offer) == null) {
                return null;
            }
            return currentOffers()[getOffer(offer).ordinal()];
        }

        static Slot getOffer(final String offer) {
            for (int i = 0; i < Slot.values().length; i++) {
                if (currentOffers()[i].getItemId() == CacheLoader.itemIdFor(offer)) {
                    return Slot.values()[i];
                }
            }
            return null;
        }

        public InterfaceComponent getParent() {
            return parent;
        }

        public Action getBuyAction() {
            return buyAction;
        }

        public Action getOpenAction() {
            return openAction;
        }

        public int getOpenVarp() {
            return openVarp;
        }

        public boolean isMembers() {
            return members;
        }

    }

    private static class PlaceOffer implements Offer {

        private OfferType offerType;
        private String name;
        private int quantity;
        private int price;

        private PlaceOffer(OfferType offerType, String name, int quantity, int price) {
            this.name = name;
            this.quantity = quantity;
            this.price = price;
            this.offerType = offerType;
        }

        public PlaceOffer(final OfferType offerType, final String name, final int quantity) {
            this.offerType = offerType;
            this.name = name;
            this.quantity = quantity;
        }

        public boolean place() {
            if (price > 0) {
                return place(offerType, name, quantity, price);
            }
            return place(offerType, name, quantity);
        }
    }

}
