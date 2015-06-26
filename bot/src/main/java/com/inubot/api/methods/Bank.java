/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.oldschool.Widget;
import com.inubot.api.oldschool.WidgetItem;
import com.inubot.api.exceptions.BankClosedException;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.oldschool.action.tree.WidgetAction;
import com.inubot.api.util.Time;
import com.inubot.api.util.filter.Filter;
import com.inubot.api.util.filter.IdFilter;
import com.inubot.api.util.filter.NameFilter;
import com.inubot.client.natives.RSVarpBit;
import com.inubot.client.natives.RSWidget;
import com.inubot.api.oldschool.GameObject;

import java.util.*;

/**
 * @author unsigned
 * @since 21-04-2015
 */
public class Bank {

    public static final int BANK_PARENT = 12;
    public static final int SLOT_CONTAINER = 6;
    private static final int NUM_TABS = 9;
    private static final int WITHDRAW_MODE_VARP = 115;
    private static final int REARRANGE_MODE_VARP = 304;

    public static final RSVarpBit BIT_OPEN_TAB_INDEX;
    public static final RSVarpBit BIT_TAB_DISPLAY;
    public static final RSVarpBit BIT_TAB_1;
    public static final RSVarpBit BIT_TAB_2;
    public static final RSVarpBit BIT_TAB_3;
    public static final RSVarpBit BIT_TAB_4;
    public static final RSVarpBit BIT_TAB_5;
    public static final RSVarpBit BIT_TAB_6;
    public static final RSVarpBit BIT_TAB_7;
    public static final RSVarpBit BIT_TAB_8;
    public static final RSVarpBit BIT_TAB_9;

    /**
     * The EnumSet of all non-main tabs.
     */
    private static final EnumSet<Tab> MINOR_TABS
            = EnumSet.range(Tab.TAB_1, Tab.TAB_9);

    static {
        BIT_OPEN_TAB_INDEX = RSVarpBit.get(4150); // 115 [2,5]
        BIT_TAB_DISPLAY = RSVarpBit.get(4170); // 867 [30,31]
        BIT_TAB_1 = RSVarpBit.get(4171); // 867 [0,9]
        BIT_TAB_2 = RSVarpBit.get(4172); // 867 [10,19]
        BIT_TAB_3 = RSVarpBit.get(4173); // 867 [20,29]
        //-----------------------------------------------
        BIT_TAB_4 = RSVarpBit.get(4174); // 1052 [0,9]
        BIT_TAB_5 = RSVarpBit.get(4175); // 1052 [10,19]
        BIT_TAB_6 = RSVarpBit.get(4176); // 1052 [20,29]
        //-----------------------------------------------
        BIT_TAB_7 = RSVarpBit.get(4177); // 1053 [0,9]
        BIT_TAB_8 = RSVarpBit.get(4178); // 1053 [10,19]
        BIT_TAB_9 = RSVarpBit.get(4179); // 1053 [20,29]
    }

    public static boolean isOpen() {
        Widget component = Interfaces.getWidget(BANK_PARENT, 0);
        return component != null && component.isVisible();
    }

    public static WidgetItem[] getItems(Filter<WidgetItem> filter) {
        if (!isOpen())
            return new WidgetItem[0];
        Widget[] children = Interfaces.widgetsFor(BANK_PARENT);
        Widget container = children[12];
        if (container != null) {
            List<WidgetItem> items = new ArrayList<>();
            Widget[] slots = container.getChildren();
            for (Widget slot : slots) {
                int id = slot.getItemId();
                if (id > 0) {
                    WidgetItem item = new WidgetItem(slot, slot.getIndex());
                    if (!filter.accept(item))
                        continue;
                    items.add(item);
                }
            }
            return items.toArray(new WidgetItem[items.size()]);
        }
        return new WidgetItem[0];
    }

    public static TabDisplay getTabDisplay(int v) {
        return TabDisplay.values()[v];
    }

    public static TabDisplay getTabDisplay() {
        int v = BIT_TAB_DISPLAY.getValue();
        return TabDisplay.values()[v];
    }

    public static WidgetItem[] getItems() {
        return getItems(Filter.always());
    }

    public static WidgetItem getFirst(Filter<WidgetItem> filter) {
        for (WidgetItem item : getItems()) {
            if (filter.accept(item))
                return item;
        }
        return null;
    }

    public static void close() {
        if (Bank.isOpen()) {
            Widget w = Interfaces.getWidget(12, 3).getChild(c -> c.getIndex() == 11);
            if (w != null)
                Client.processAction(new WidgetAction(ActionOpcodes.WIDGET_ACTION, 0, w.getIndex(), w.getId()), "Close", "");
        }
    }

    private static boolean isMainOpen() {
        return getOpenTab() == Tab.MAIN_TAB;
    }

    /**
     * Determines if the bank is closed. The logic is if the bank is not open,
     * then it is closed {!{@link #isOpen()}}
     *
     * @return True If and only if the bank is not open.
     */
    public static boolean isClosed() {
        return !isOpen();
    }

    /**
     * Determines the bank tab ID that is currently in focus, or will
     * be in focus next time the bank is open.
     * <p>
     * This means is justified by internal client settings. One
     * should note that this value does not reset to any default
     * value when the bank is closed. When the bank is closed the
     * the tab that was open when the bank is closed will be the
     * same tab that is in focus when the bank is re-opened. This
     * observation has yet to be proven, but is observed in revision
     * 70.
     *
     * @return The tab that is or will be in focus currently, or
     * the next time the bank is opened.
     */
    private static int getOpenTabIndex() {
        return BIT_OPEN_TAB_INDEX.getValue();
    }

    /**
     * Determines the {@link Tab} that is currently in focus. This
     * method requires the bank to be open. In the case that the bank
     * was closed, null will be returned. One should also note that
     * the tab that was in focus when the bank is closed will be the
     * same tab that will be in focus next time the bank is open; it's
     * observed that it does not reset to any default value. As to
     * clarify the purpose of this method, it was decided to return null
     * in the case that the bank was closed, though it's never null.
     *
     * @return The tab that is currently in focus. Null if the bank
     * is closed.
     */
    public static Tab getOpenTab() {
        if (isClosed()) return null;
        return Tab.get(getOpenTabIndex());
    }

    /**
     * If the bank is closed, the tab that was last in focus/open is observed
     * to be the same tab that will be opened next time the bank is opened.
     * For clarification purposes the method {@link #getOpenTab()} returns null
     * in the case that the bank was closed, when in actuality it does not reset
     * to any default or invalid value. This method allows the user to predict
     * that tab that will be open next time the bank is open.
     *
     * @return The tab that is currently in focus, or will be in focus
     * next time the bank is opened.
     */
    public static Tab getTabFuture() {
        return Tab.get(getOpenTabIndex());
    }

    /**
     * Determines the next non-main tab that does not occupies at least
     * one item, and thus is collapsed, and can be used to open/create a
     * tab. This performs a linear check from tab 0 to the rest of the tabs,
     * and returns the first tab to be {@link Bank.Tab#isCollapsed()}
     * One should note that the main tab can never be collapsed, and will
     * never be a returned value.
     *
     * @return The first occurrence of a collapsed tab.
     */
    public static Tab getNextCollapsedTab() {
        for (Tab tab : MINOR_TABS) {
            if (tab.isCollapsed()) return tab;
        }
        return null;
    }

    public static void depositInventory() {
        Widget[] children = Interfaces.widgetsFor(BANK_PARENT);
        Widget button = children[27];
        if (button != null)
            button.processAction("Deposit inventory");
    }

    public static void open() {
        Processable p = GameObjects.getNearest(GameObject.Landmark.BANK);
        if (p == null)
            p = Npcs.getNearest(new NameFilter<>("Banker", "Emerald Benedict"));
        if (p == null)
            return;
        p.processAction("Bank");
    }

    public static void depositAllExcept(Filter<WidgetItem> filter) {
        depositAll(Filter.not(filter));
    }

    public static void depositAll(Filter<WidgetItem> filter) {
        if (!isOpen())
            throw new BankClosedException();
        for (WidgetItem item : Inventory.getItems(filter)) {
            if (item != null && item.getDefinition() != null)
                item.processAction("Deposit-All");
        }
    }

    public static WithdrawMode getWithdrawMode() {
        return Varps.getBoolean(WITHDRAW_MODE_VARP) ? WithdrawMode.NOTE : WithdrawMode.ITEM;
    }

    public static RearrangeMode getRearrangeMode() {
        return Varps.get(REARRANGE_MODE_VARP) == 2000 ? RearrangeMode.SWAP : RearrangeMode.INSERT;
    }

    public static int getCount() {
        return ItemTables.getBank().length;
    }

    public static int getCount(Filter<ItemTables.Entry> filter) {
        int count = 0;
        for (ItemTables.Entry entry : ItemTables.getBank()) {
            if (filter.accept(entry))
                count++;
        }
        return count;
    }

    public static int getCountExcept(Filter<ItemTables.Entry> filter) {
        return getCount(Filter.not(filter));
    }

    /**
     * Determines if the bank contains any items. This method
     * requires the bank to be open. If the bank was closed
     * when this method is called, it'll throw an exception to
     * signify a invalid state. One should ensure that the bank
     * is open before evaluating this logic.
     *
     * @return True if the bank does not occupy at least one item,
     * False if the bank occupies at least one item.
     * @throws BankClosedException If the bank was closed.
     * @see #getCount()
     */
    public static boolean isEmpty() {
        if (isClosed())
            throw new BankClosedException();
        return getCount() == 0;
    }

    public static boolean withdraw(int id, int amount) {
        if (!isOpen())
            throw new BankClosedException();
        WidgetItem item = getFirst(w -> w.getId() == id && w.getQuantity() >= amount);
        if (item != null) {
            if (getWithdrawMode() == WithdrawMode.ITEM && amount >= 28 - Inventory.getCount()) {
                item.processAction("Withdraw-All"); //TODO hook stackable
                return true;
            }
            int withdrawn = 0;
            while (withdrawn != amount && getFirst(new IdFilter<>(item.getId())) != null) {
                int remaining = amount - withdrawn;
                if (remaining >= 10) {
                    item.processAction("Withdraw-10");
                    withdrawn += 10;
                } else if (remaining >= 5) {
                    item.processAction("Withdraw-5");
                    withdrawn += 5;
                } else if (remaining >= 1) {
                    item.processAction("Withdraw-1");
                    withdrawn++;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Determines the maximum number of items that can be stored
     * within the bank. This method requires the bank to be open.
     * If the bank was closed when this method is called, a default
     * value of -1 will be returned. The capacity of the bank will
     * always be greater than or equal to the total number of items within
     * the bank. In the case that the total items within the bank
     * is equal to the capacity, the bank is full, and can no longer
     * store any more items. In the case that the total number of
     * items within the bank is less than the capacity, then the
     * number of free spaces that can be used to store a new item
     * is equal to the capacity minus the total number of items
     * within the bank (capacity - item_count).
     *
     * @return The maximum number of items that can be stored within the bank
     * @see #getCount()
     * @see #isEmpty()
     * @see #isFull()
     */
    //NOTE: This value can not be assumed constant
    public static int getCapacity() { //TODO Any better way?
        if (!isOpen()) return -1;
        Widget count_text = Interfaces.getWidget(12, 5);
        return Integer.valueOf(count_text.getText());
    }

    /**
     * Determines if the bank has reach it's capacity, and
     * can no longer store any more-new-items. This method
     * requires the bank to be open. If the bank is closed
     * by the this method is called, an exception will be
     * thrown to signify a invalid state. One should ensure
     * that the bank is open before evaluating this logic.
     *
     * @return True if the bank is full, and can no longer
     * store any more-new-items, if the bank is open.
     * @throws BankClosedException If
     *                             the bank was closed when this method was called.
     */
    public static boolean isFull() {
        if (isClosed())
            throw new BankClosedException();
        return getCount() == getCapacity();
    }

    /**
     * Determines the maximum number of -new- items that can be
     * stored within the bank. This method requires the bank
     * to be open. If the bank is closed when this method is
     * called, a default value of -1 will be returned. Any
     * -valid- returned value will be less than or equal to
     * the capacity of the bank. In the case that the a -valid-
     * return value is equal to the capacity of the bank, then
     * the bank is empty. In the case that a -valid- return value
     * is equal to 0 the bank is full.
     *
     * @return The maximum number of -new- items that can be
     * stored within the bank, if the bank is open.
     * -1 if the bank is closed.
     * @see #getCount()
     * @see #getCapacity()
     */
    public static int getFreeSpace() {
        if (Bank.isClosed()) return -1;
        return getCapacity() - getCount();
    }

    public static boolean canStoreXItems(int num) {
        return getFreeSpace() >= num;
    }

    public static boolean isSettingsOpen() {
        // Can tell if the main tab panel is not displayed, or the texture of the button
        if (isClosed()) return false;
        Widget settings_panel = Interfaces.getWidget(12, 7);
        if (settings_panel == null) return false; //Should never happen?
        return !settings_panel.isHidden();
    }

    /**
     * The possible 'TabDisplay' options of the bank.
     * This controls the displayed image on all of the tabs dividers
     */
    public static enum TabDisplay {
        // Order is with respect of the corresponding varp value, do not change.
        /**
         * The first item of the tab is displayed within the divider of the tab *
         */
        FIRST_ITEM,
        /**
         * The tab index is displayed within the divider of the tab *
         */
        DIGIT,
        /**
         * The tab index in roman numerals is displayed within the divider of the tab *
         */
        ROMAN
    }

    public static enum WithdrawMode {
        ITEM, NOTE
    }

    public static enum RearrangeMode {
        SWAP, INSERT
    }

    public static enum Tab { // Pointers for the possible bank tabs

        // Order is with respect to the index varp value, do not change

        MAIN_TAB(null), // TAB_0
        TAB_1(BIT_TAB_1),
        TAB_2(BIT_TAB_2),
        TAB_3(BIT_TAB_3),
        TAB_4(BIT_TAB_4),
        TAB_5(BIT_TAB_5),
        TAB_6(BIT_TAB_6),
        TAB_7(BIT_TAB_7),
        TAB_8(BIT_TAB_8),
        TAB_9(BIT_TAB_9);

        private final RSVarpBit varpBit;

        private Tab(RSVarpBit varpBit) {
            this.varpBit = varpBit;
        }

        public static Tab get(int tab) {
            return Tab.values()[tab];
        }

        public static Tab getOpen() {
            for (final Tab tab : Tab.values()) {
                if (tab.isOpen()) {
                    return tab;
                }
            }
            return null;
        }

        /**
         * Calculates the total number of items within this tab.
         *
         * @return The total number of items within this tab.
         */
        public int getCount() { //TODO requires the bank to be open

            if (this != MAIN_TAB) // Natural count
                return varpBit.getValue();

            // Main Tab is special case and is equal to
            // the total items of the bank minus the sum
            // of the other 9 tabs.
            int sum = 0;
            for (final Tab tab : MINOR_TABS) {
                sum += tab.getCount();
            }

            return Bank.getItems().length - sum;

        }

        /**
         * Determines if this current tab is in focus. This function
         * requires the bank to be open. If the bank is closed then
         * this function will return false.
         *
         * @return True if this bank tab is in focus, and the bank is open.
         * False otherwise.
         * @see #getOpenTabIndex()
         */
        public boolean isOpen() {
            if (Bank.isClosed()) return false;
            return Bank.getOpenTabIndex() == getIndex();
        }

        /**
         * Determines if this tab is closed and thus does not have
         * focus. The logic is if this tab is not open, then its closed.
         *
         * @return True if this bank tab is not open.
         * @see #isOpen()
         */
        public boolean isClosed() {
            return !isOpen();
        }

        public int getIndex() {
            return ordinal();
        }

        /**
         * Determines if this tab exists, meaning it's currently
         * in use, and occupies at least one time(not empty).
         * The MainTab is a special case to this logic, for it
         * can never be collapsed.
         *
         * @return True if the tab exists, false otherwise
         */
        public boolean isCollapsed() {
            if (this == MAIN_TAB) return false;
            return getCount() == 0;
        }

        /**
         * Determines if this tab contains at least one item.
         * This function performs the same logic as {@link #isCollapsed()}
         * but will include the main tab, since the main tab can also be empty.
         * For clarification one should note
         *
         * @return
         */
        public boolean isEmpty() {
            return getCount() == 0;
        }

        /**
         * The Bank container index base for this tab.
         * All indexes of items within this tab will
         * range between [ BaseValue, BaseValue + ItemCount ).
         * <p>
         * Example:
         * For Item 5 of Tab 2 is located within the base value
         * of Tab 2 + 5. Where the resulting index is equal to
         * the index within the Banks item container
         * <p>
         * This value is equal to the sum of the precessing
         * tab count. (T1c + T2c + T3c + ... + Tc(I-1))
         * <p>
         * The MainTab is special case for this logic, for
         * it's items are stored at the end of container,
         * despite it being the 0'th tab (internally).
         */
        public int getContainerBaseIndex() {

            // MainTab is located at the end of the container,
            // though its index is the 0'th...
            if (this == MAIN_TAB) { //Special case
                return Bank.getCount() - this.getCount();
            }

            // Summation
            int count = 0;
            for (Tab tab : MINOR_TABS) { // Ensure 1 -> 9 Order
                if (this == tab) break;
                count += tab.getCount();
            }

            return count;

        }

        /**
         * Returns the dividing widget of this tab within
         * the tab bar of the bank. This widget displays
         * the first item within the tab, and is what you
         * would use to open this tab. It shall have a
         * valid item id and quantity equal to that of
         * the first item within the tab. It currently
         * proves that despite the display mode of the
         * tab, the first item within the tab [ID+Quantity]
         * is still set.
         *
         * @return The dividing widget header of this tab.
         * @see {@link Bank#getTabDisplay()}
         */
        public Widget getTab() {
            return Interfaces.getWidget(12, 8).getChildren()[10 + getIndex()];
        }

        public Widget getDivider() { // Gets the divider located at the bottom of the tab
            if (Bank.isClosed()) return null;
            if (!isMainOpen()) return null;
            if (isCollapsed()) return null;
            //... The divider should now be active
            int base_index = getCapacity() + getIndex();
            return Interfaces.getWidget(12, 10).getChildren()[base_index];
        }

        public Widget getRemote() { // When searching the widget to open the respected tab
            if (Bank.isClosed()) return null;
            int base_index = getCapacity() + NUM_TABS + getIndex() - 1;
            return Interfaces.getWidget(12, 10).getChildren()[base_index];
        }

        public Widget getDropRegion() { // The empty region at the end of each tab where you can drop an item to add to the tab.
            // This region is only updated if their is a gap/space of items at the end of the tab (lower,right).
            int base_index = getCapacity() + NUM_TABS * 2 + getIndex() - 1;
            return Interfaces.getWidget(12, 10).getChildren()[base_index]; //TODO return null if there is no gap
        }

        /**
         * Calculates the container index of a specific relative index,
         * relative to this tab. The returned value can be used to define
         * the index within the banks item container where the specified item
         * is located within the container widget. This index can also define the
         * child index within the container widget where the widget child is
         * indexed.
         * <p>
         * For clarification purposes the provided relative index is
         * forced to be within the item range of the tab,
         * unless the returned value will be that of a different tab.
         * If the relative index is out of range, then -1 will be returned
         * as the returned value. The relative index must be greater
         * than or equal zero and less than the total number of item
         * within the tab in order to be within the range of items
         * within this tab (0 <= i < {@link #getCount()}). The returned
         * value will be the index within the banks item containers
         * which you can lookup the specified item.
         *
         * @param relative_index The index of the item, within this tab
         *                       which must be (0 <= 0 < {@link #getCount()})
         * @return The index of the item within the container widget,
         * that the relative item (within this tab) is located.
         * @see #getContainerBaseIndex()
         */
        public int getItemIndex(int relative_index) {
            if (relative_index < 0 || relative_index > getCount()) return -1;
            final int base_index = getContainerBaseIndex();
            return base_index + relative_index;
        }

        /**
         * Opens this tab. This function will return true if and
         * only if this tab was successfully opened. This function
         * will interact with the divider of the tab provided by {@link #getTab()}.
         * <p>
         * This methods requires this tab to be interactable. In the cases that
         * this tab can not be interacted upon, or this tab is collapsed,
         * this function will immediately return false. In the case that the
         * tab is already open this function will immediately return true.
         *
         * @return True if and only if this tab was successful opened, or was already open.
         * False otherwise.
         * @see #isOpen()
         * @see #isCollapsed()
         * @see #getTab()
         */
        public boolean open() {
            if (isOpen()) return true;
            if (isCollapsed()) return false; // Can't open a collapsed tab
            // Now we can try an open the tab...
            Widget divider = getTab();
            if (divider == null) return false;
            divider.processAction("View tab"); //TODO ensure constant action string?
            Time.sleep(500, 800);
            // ^ Though pending the event dispatched by the divider is what
            // we're polling for, the events are dispatched within the
            // same thread as the game engine; ether way will work.
            //-----------------------------------------------------------
            // ^ The open tab varpbit is set internally by a rs-event, no
            // need to poll a server response to ensure a valid interaction
            return false;
        }

        public WidgetItem getItem(int relative_index) {
            final int idx = getItemIndex(relative_index);
            if (idx == -1) return null;
            return new WidgetItem(Interfaces.getWidget(12, 10).getChildren()[idx], idx);
        }

        public int[] getItemIds(int[] dest, int pos, int length) {
            final Widget container = Interfaces.getWidget(12, 10);
            if (container == null) return new int[0];
            final int num_items = getCount();
            final int base = getContainerBaseIndex();
            final RSWidget[] items = container.getRaw().getChildren();
            final int lim = base + num_items;
            for (int i = base, k = 0; k < length && i < lim; i++)
                dest[pos + k++] = items[i].getItemId();
            return dest;
        }

        public int[] getItemQuantities(int[] dest, int pos, int length) {
            final Widget container = Interfaces.getWidget(12, 10);
            if (container == null) return new int[0];
            final int num_items = getCount();
            final int base = getContainerBaseIndex();
            final RSWidget[] items = container.getRaw().getChildren();
            final int lim = base + num_items;
            for (int i = base, k = 0; k < length && i < lim; i++)
                dest[pos + k++] = items[i].getItemAmount();
            return dest;
        }

        public WidgetItem[] getItems() {
            final Widget container = Interfaces.getWidget(12, 10);
            if (container == null)
                return new WidgetItem[0];
            final int num_items = getCount();
            final int base = getContainerBaseIndex();
            final int lim = base + num_items;
            final WidgetItem[] dest = new WidgetItem[num_items];
            for (int i = base, k = 0; i < lim; i++)
                dest[k++] = new WidgetItem(container.getChildren()[i], i);
            return dest;
        }

        public int[] getItemIds() {
            final int count = getCount();
            final int[] dest = new int[count];
            return getItemIds(dest, 0, count);
        }

        public int[] getItemQuantities() {
            final int count = getCount();
            final int[] dest = new int[count];
            return getItemQuantities(dest, 0, count);
        }

        public String toString() {
            return name() + "(Open=" + isOpen() + ",Count=" + getCount() + ",Collapsed=" + isCollapsed() + ",Base=" + getContainerBaseIndex() + ")";
        }
    }
}
