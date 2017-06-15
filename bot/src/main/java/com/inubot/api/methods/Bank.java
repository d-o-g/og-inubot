/*
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public
 * License as published by the Free Software Foundation; either
 * version 2 of the license, or (at your option) any later version.
 */
package com.inubot.api.methods;

import com.inubot.api.exceptions.BankClosedException;
import com.inubot.api.oldschool.*;
import com.inubot.api.oldschool.action.ActionOpcodes;
import com.inubot.api.oldschool.action.Processable;
import com.inubot.api.oldschool.action.tree.InterfaceComponentAction;
import com.inubot.api.util.*;
import com.inubot.api.util.filter.*;
import com.inubot.client.natives.oldschool.RSVarpBit;
import com.inubot.client.natives.oldschool.RSInterfaceComponent;

import java.awt.event.KeyEvent;
import java.util.*;

public class Bank {

    public static final int BANK_PARENT = 12;
    public static final int SLOT_CONTAINER = 12;
    public static final VarpBit BIT_OPEN_TAB_INDEX;
    public static final VarpBit BIT_TAB_DISPLAY;
    public static final VarpBit BIT_TAB_1;
    public static final VarpBit BIT_TAB_2;
    public static final VarpBit BIT_TAB_3;
    public static final VarpBit BIT_TAB_4;
    public static final VarpBit BIT_TAB_5;
    public static final VarpBit BIT_TAB_6;
    public static final VarpBit BIT_TAB_7;
    public static final VarpBit BIT_TAB_8;
    public static final VarpBit BIT_TAB_9;
    private static final int NUM_TABS = 9;
    private static final int WITHDRAW_MODE_VARP = 115;
    private static final int REARRANGE_MODE_VARP = 304;
    /**
     * The EnumSet of all non-main tabs.
     */
    private static final EnumSet<Tab> MINOR_TABS
            = EnumSet.range(Tab.TAB_1, Tab.TAB_9);

    static {
        BIT_OPEN_TAB_INDEX = VarpBit.get(4150); // 115 [2,5]
        BIT_TAB_DISPLAY = VarpBit.get(4170); // 867 [30,31]
        BIT_TAB_1 = VarpBit.get(4171); // 867 [0,9]
        BIT_TAB_2 = VarpBit.get(4172); // 867 [10,19]
        BIT_TAB_3 = VarpBit.get(4173); // 867 [20,29]
        //-----------------------------------------------
        BIT_TAB_4 = VarpBit.get(4174); // 1052 [0,9]
        BIT_TAB_5 = VarpBit.get(4175); // 1052 [10,19]
        BIT_TAB_6 = VarpBit.get(4176); // 1052 [20,29]
        //-----------------------------------------------
        BIT_TAB_7 = VarpBit.get(4177); // 1053 [0,9]
        BIT_TAB_8 = VarpBit.get(4178); // 1053 [10,19]
        BIT_TAB_9 = VarpBit.get(4179); // 1053 [20,29]
    }

    /**
     * @return <b>true</b> if and only if the bank is open and rendered
     */
    public static boolean isOpen() {
        InterfaceComponent component = Interfaces.getComponent(BANK_PARENT, 0);
        return component != null && component.isVisible();
    }

    /**
     * @param filter The {@link com.inubot.api.util.filter.Filter} which should be used to select the elements
     * @return An array of {@link Item}'s in the bank
     * that were accepted by the given {@link com.inubot.api.util.filter.Filter}
     */
    public static Item[] getItems(Filter<Item> filter) {
        if (!isOpen())
            return new Item[0];
        InterfaceComponent[] children = Interfaces.componentsFor(BANK_PARENT);
        InterfaceComponent container = children[12];
        if (container != null) {
            List<Item> items = new ArrayList<>();
            InterfaceComponent[] slots = container.getComponents();
            for (InterfaceComponent slot : slots) {
                int id = slot.getItemId();
                if (id > 0) {
                    Item item = new Item(slot, slot.getIndex());
                    if (!filter.accept(item))
                        continue;
                    items.add(item);
                }
            }
            return items.toArray(new Item[items.size()]);
        }
        return new Item[0];
    }

    /**
     * @return The current Bank {@link Bank.TabDisplay} type
     */
    public static TabDisplay getTabDisplay() {
        int v = BIT_TAB_DISPLAY.getValue();
        return TabDisplay.values()[v];
    }

    /**
     * @return All the {@link Item}'s in the bank
     */
    public static Item[] getItems() {
        return getItems(Filter.always());
    }

    /**
     * @param filter the {@link com.inubot.api.util.filter.Filter} to select the elements
     * @return the first {@link Item} selected by the {@link com.inubot.api.util.filter.Filter}
     */
    public static Item getFirst(Filter<Item> filter) {
        for (Item item : getItems()) {
            if (filter.accept(item))
                return item;
        }
        return null;
    }

    /**
     * @param ids
     * @return Gets the first {@link Item} with the given ids
     */
    public static Item getFirst(int... ids) {
        return getFirst(new IdFilter<Item>(ids));
    }

    /**
     * @param names
     * @return Gets the first {@link Item} with the given names
     */
    public static Item getFirst(String... names) {
        return getFirst(new NameFilter<Item>(names));
    }

    /**
     * Selects the close button of the bank if it is open
     */
    public static boolean close() {
        if (Bank.isOpen()) {
            InterfaceComponent w = Interfaces.getComponent(12, 3).getComponent(c -> c.getIndex() == 11);
            if (w != null) {
                Client.processAction(new InterfaceComponentAction(ActionOpcodes.COMPONENT_ACTION, 0, w.getIndex(), w.getId()), "Close", "");
                return true;
            }
        }
        return false;
    }

    /**
     * @return <b>true</b> if the current {@link Bank.Tab} is the main bank tab
     */
    public static boolean isMainTabOpen() {
        return getOpenTab() == Tab.MAIN_TAB;
    }

    /**
     * Determines if the bank is closed. The logic is if the bank is not open,
     * then it is closed {!{@link #isOpen()}}
     *
     * @return <b>true</b> If and only if the bank is not open.
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
            if (tab.isCollapsed())
                return tab;
        }
        return null;
    }

    /**
     * Clicks the deposit inventory button in the bank
     */
    public static void depositInventory() {
        InterfaceComponent[] children = Interfaces.componentsFor(BANK_PARENT);
        InterfaceComponent button = children[29];
        if (button != null)
            button.processAction(57, "Deposit inventory");
    }

    /**
     * Attempts to open a bank if there is one available in the current loaded region.
     *
     * @see com.inubot.api.methods.traversal.graph.data.WebBank The {@link com.inubot.api.methods.traversal.graph.data.WebBank}
     * enum provides sufficient data to traverse to a close bank via the web, if there is no bank in the loaded
     * region then this should be used instead
     * @see com.inubot.api.methods.traversal.graph.Web Use the methods in {@link com.inubot.api.methods.traversal.graph.Web}
     * that return a {@link com.inubot.api.methods.traversal.graph.data.WebBank}
     * @see com.inubot.api.methods.traversal.Movement Use {@link com.inubot.api.methods.traversal.Movement#getWeb}
     * to access the {@link com.inubot.api.methods.traversal.graph.Web}
     */
    public static void open() {
        Processable p = GameObjects.getNearest(t -> t.containsAction("Bank") && "Bank booth".equals(t.getName()));
        if (p == null)
            p = GameObjects.getNearest(GameObject.Landmark.BANK);
        if (p == null)
            p = Npcs.getNearest(n -> n.containsAction("Bank"));
        if (p == null)
            return;
        p.processAction("Bank");
    }

    /**
     * Deposits all items into the bank that are not accepted by the filter
     *
     * @param filter The {@link com.inubot.api.util.filter.Filter} which will be used to select the elements
     */
    public static void depositAllExcept(Filter<Item> filter) {
        depositAll(Filter.not(filter));
    }

    public static void depositAllExcept(int... ids) {
        depositAllExcept(new IdFilter<Item>(ids));
    }

    public static void depositAllExcept(String... names) {
        depositAllExcept(new NameFilter<Item>(names));
    }

    /**
     * Deposits all items into the bank that are accepted by the filter
     *
     * @param filter The {@link com.inubot.api.util.filter.Filter} which will be used to select the elements
     * @see #depositInventory - should be used instead of this method if the entire inventory
     * is needed to be deposited
     */
    public static void depositAll(Filter<Item> filter) {
        if (!isOpen())
            throw new BankClosedException();
        Item[] items = Inventory.getItems(filter);
        if (items.length == Inventory.getCount()) {
            depositInventory();
            return;
        }
        for (Item item : items) {
            if (item != null && item.getDefinition() != null) {
                item.processAction(57, "Deposit-All");
            }
        }
    }

    public static void depositAll(String... names) {
        depositAll(new NameFilter<Item>(names));
    }

    public static void depositAll(int... ids) {
        depositAll(new IdFilter<Item>(ids));
    }

    /**
     * @return The current bank {@link Bank.WithdrawMode} state
     */
    public static WithdrawMode getWithdrawMode() {
        return Varps.getBoolean(WITHDRAW_MODE_VARP) ? WithdrawMode.NOTE : WithdrawMode.ITEM;
    }

    /**
     * @return The current bank {@link Bank.RearrangeMode} state
     */
    public static RearrangeMode getRearrangeMode() {
        return Varps.get(REARRANGE_MODE_VARP) == 2000 ? RearrangeMode.SWAP : RearrangeMode.INSERT;
    }

    /**
     * This method is <b>not</b> the same as Bank.getItems().length in the sense
     * that it uses the clients item cache to access the local item data.
     *
     * @return The current number of items in the bank
     */
    public static int getCount() {
        return ItemTables.getBank().length;
    }

    /**
     * @param filter the {@link com.inubot.api.util.filter.Filter} which should be used to select the elements
     * @return the number of items in the bank accepted by the filter
     * @see #getCount
     */
    public static int getCount(Filter<ItemTables.Entry> filter) {
        int count = 0;
        for (ItemTables.Entry entry : ItemTables.getBank()) {
            if (filter.accept(entry))
                count++;
        }
        return count;
    }

    /**
     * @param filter the {@link com.inubot.api.util.filter.Filter} which should be used to select the elements
     * @return the number of items in the bank that were rejected by the filter
     * @see #getCount
     */
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
     * @return <b>true</b> if the bank does not occupy at least one item,
     * <b>false</b> if the bank occupies at least one item.
     * @throws BankClosedException If the bank was closed.
     * @see #getCount()
     */
    public static boolean isEmpty() {
        if (isClosed())
            throw new BankClosedException();
        return getCount() == 0;
    }

    public static boolean withdrawAll(int id) {
        Item item = Bank.getFirst(new IdFilter<>(id));
        if (item != null) {
            item.processAction("Withdraw-All");
            return true;
        }
        return false;
    }

    public static boolean withdrawAll(String name) {
        Item item = Bank.getFirst(new NameFilter<>(name));
        if (item != null) {
            item.processAction("Withdraw-All");
            return true;
        }
        return false;
    }

    public static boolean withdraw(int id, int amount) {
        if (!isOpen())
            throw new BankClosedException();
        if (amount == 28)
            return withdrawAll(id);
        Item item = getFirst(w -> w.getId() == id && w.getQuantity() >= amount);
        if (item != null) {
            if (item.containsAction("Withdraw-" + amount)) {
                item.processAction("Withdraw-" + amount);
                return true;
            }
            item.processAction("Withdraw-X");
            if (Time.await(() -> !Interfaces.getComponent(162, 32).isExplicitlyHidden(), 1500)) {
                Time.sleep(1000);
                for (char c : String.valueOf(amount).toCharArray()) {
                    Game.getCanvas().pressKey(c, 200);
                    Game.getCanvas().releaseKey(c);
                    Game.getCanvas().dispatch(new KeyEvent(Game.getCanvas(), KeyEvent.KEY_TYPED, System.currentTimeMillis() + 10, 0, KeyEvent.VK_UNDEFINED, c, KeyEvent.KEY_LOCATION_UNKNOWN));
                }
                Game.getCanvas().pressEnter();
                return true;
            }
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
     * <br>
     * <b>NOTE: This value can not be assumed constant</b>
     *
     * @return The maximum number of items that can be stored within the bank
     * @see #getCount()
     * @see #isEmpty()
     * @see #isFull()
     */
    public static int getCapacity() { //TODO Any better way?
        if (!isOpen())
            return -1;
        InterfaceComponent textInterfaceComponent = Interfaces.getComponent(12, 5);
        return Integer.valueOf(textInterfaceComponent.getText());
    }

    /**
     * Determines if the bank has reach it's capacity, and
     * can no longer store any more-new-items. This method
     * requires the bank to be open. If the bank is closed
     * by the this method is called, an exception will be
     * thrown to signify a invalid state. One should ensure
     * that the bank is open before evaluating this logic.
     *
     * @return <b>true</b> if the bank is full, and can no longer
     * store any more-new-items, if the bank is open. <b>false</b> otherwise
     * @throws BankClosedException If the bank was closed when this method was called.
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
        InterfaceComponent settings_panel = Interfaces.getComponent(12, 7);
        return settings_panel != null && !settings_panel.isHidden();
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

    /**
     * Pointers for the possible bank tabs. Order is with respect to the index varp value, do not change
     */
    public static enum Tab {

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
            for (Tab tab : MINOR_TABS)
                sum += tab.getCount();
            return Bank.getItems().length - sum;

        }

        /**
         * Determines if this current tab is in focus. This function
         * requires the bank to be open. If the bank is closed then
         * this function will return false.
         *
         * @return <b>true</b> if this bank tab is in focus, and the bank is open.
         * <b>false</b> otherwise.
         * @see #getOpenTabIndex()
         */
        public boolean isOpen() {
            return !Bank.isClosed() && Bank.getOpenTabIndex() == getIndex();
        }

        /**
         * Determines if this tab is closed and thus does not have
         * focus. The logic is if this tab is not open, then its closed.
         *
         * @return <b>true</b> if this bank tab is not open.
         * @see Bank#isOpen
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
         * @return <b>true</b> if the tab exists, <b>false</b> otherwise
         */
        public boolean isCollapsed() {
            return this != MAIN_TAB && getCount() == 0;
        }

        /**
         * Determines if this tab contains no items.
         * This function performs the same logic as {@link #isCollapsed()}
         * but will include the main tab, since the main tab can also be empty.
         * For clarification one should note
         *
         * @return <b>true</b> if the tab does not contain any items, <b>false</b> otherwise
         */
        public boolean isEmpty() {
            return getCount() == 0;
        }

        /**
         * The Bank container index base for this tab.
         * All indexes of items within this tab will
         * range between [ BaseValue, BaseValue + ItemCount ).
         * <p>
         * Example: Item 5 of Tab 2 is located within the base value
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
                if (this == tab)
                    break;
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
         * @see #getTabDisplay
         */
        public InterfaceComponent getTab() {
            return Interfaces.getComponent(12, 8).getComponents()[10 + getIndex()];
        }

        public InterfaceComponent getDivider() { // Gets the divider located at the bottom of the tab
            if (Bank.isClosed()) return null;
            if (!isMainTabOpen()) return null;
            if (isCollapsed()) return null;
            //... The divider should now be active
            int base_index = getCapacity() + getIndex();
            return Interfaces.getComponent(12, 10).getComponents()[base_index];
        }

        public InterfaceComponent getRemote() { // When searching the widget to open the respected tab
            if (Bank.isClosed())
                return null;
            int baseIdx = getCapacity() + NUM_TABS + getIndex() - 1;
            return Interfaces.getComponent(12, 10).getComponents()[baseIdx];
        }

        /**
         * @return The empty region at the end of each tab where you can drop an item to add to the tab.
         * This region is only updated if their is a gap/space of items at the end of the tab (lower,right).
         */
        public InterfaceComponent getDropRegion() {
            int baseIdx = getCapacity() + NUM_TABS * 2 + getIndex() - 1;
            return Interfaces.getComponent(12, 10).getComponents()[baseIdx]; //TODO return null if there is no gap
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
         * @param relativeIndex The index of the item, within this tab
         *                      which must be (0 <= 0 < {@link #getCount()})
         * @return The index of the item within the container widget,
         * that the relative item (within this tab) is located.
         * @see #getContainerBaseIndex
         */
        public int getItemIndex(int relativeIndex) {
            if (relativeIndex < 0 || relativeIndex > getCount()) return -1;
            int base = getContainerBaseIndex();
            return base + relativeIndex;
        }

        /**
         * Opens this tab. This function will return true if and
         * only if this tab was successfully opened. This function
         * will interact with the divider of the tab provided by {@link #getTab()}.
         * <p>
         * This methods requires this tab to be interactable. In the cases that
         * this tab can not be interacted upon, or this tab is collapsed,
         * this function will immediatelyQueue return false. In the case that the
         * tab is already open this function will immediatelyQueue return true.
         *
         * @return <b>true</b> if and only if this tab was successful opened, or was already open.
         * <b>false</b> otherwise.
         * @see Bank#isOpen
         * @see Tab#isCollapsed
         * @see Tab#getTab
         */
        public boolean open() {
            if (isOpen()) return true;
            if (isCollapsed()) return false; // Can't open a collapsed tab
            // Now we can try an open the tab...
            InterfaceComponent divider = getTab();
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

        public Item getItem(int relativeIndex) {
            final int idx = getItemIndex(relativeIndex);
            if (idx == -1)
                return null;
            return new Item(Interfaces.getComponent(12, 10).getComponents()[idx], idx);
        }

        public int[] getItemIds(int[] dest, int pos, int length) {
            final InterfaceComponent container = Interfaces.getComponent(12, 10);
            if (container == null) return new int[0];
            final int num_items = getCount();
            final int base = getContainerBaseIndex();
            final RSInterfaceComponent[] items = container.getRaw().getChildren();
            final int lim = base + num_items;
            for (int i = base, k = 0; k < length && i < lim; i++)
                dest[pos + k++] = items[i].getItemId();
            return dest;
        }

        public int[] getItemQuantities(int[] dest, int pos, int length) {
            final InterfaceComponent container = Interfaces.getComponent(12, 10);
            if (container == null) return new int[0];
            final int num_items = getCount();
            final int base = getContainerBaseIndex();
            final RSInterfaceComponent[] items = container.getRaw().getChildren();
            final int lim = base + num_items;
            for (int i = base, k = 0; k < length && i < lim; i++)
                dest[pos + k++] = items[i].getItemAmount();
            return dest;
        }

        public Item[] getItems() {
            final InterfaceComponent container = Interfaces.getComponent(12, 10);
            if (container == null)
                return new Item[0];
            final int num_items = getCount();
            final int base = getContainerBaseIndex();
            final int lim = base + num_items;
            final Item[] dest = new Item[num_items];
            for (int i = base, k = 0; i < lim; i++)
                dest[k++] = new Item(container.getComponents()[i], i);
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
