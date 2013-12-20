/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */

package com.codenvy.ide.ui.menu;

import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * MenuBar is visual component, represents top menu.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class MenuBar extends Composite implements ItemSelectedHandler, CloseMenuHandler {

    static final MenuResources resources = GWT.create(MenuResources.class);

    static {
        resources.menuCss().ensureInjected();
    }

    /**
     * This is visual component.
     * Uses for handling mouse events on MenuBar.
     */
    private class MenuBarTable extends FlexTable {

        public MenuBarTable() {
            sinkEvents(Event.ONMOUSEOVER | Event.ONMOUSEOUT | Event.ONMOUSEDOWN);
        }

        @Override
        public void onBrowserEvent(Event event) {
            Element td = getEventTargetCell(event);
            MenuBarItem item = menuBarItems.get(td);

            if (item == null) {
                return;
            }

            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    if (selectedMenuBarItem != null && item != selectedMenuBarItem) {
                        if (item.onMouseDown()) {
                            openPopupMenu(item);
                        }
                    }

                    item.onMouseOver();
                    break;

                case Event.ONMOUSEOUT:
                    item.onMouseOut();
                    break;

                case Event.ONMOUSEDOWN:
                    if (item == selectedMenuBarItem) {
                        if (lockLayer != null) {
                            lockLayer.close();
                        }
                        return;
                    }

                    if (item.onMouseDown()) {
                        openPopupMenu(item);
                    }
                    break;

                default:
                    break;
            }

        }
    }

    /** Panel, which contains top menu. */
    private AbsolutePanel absolutePanel;

    /** Lock layer for displaying popup menus. */
    private MenuLockLayer lockLayer;

    /** List Menu Bar items. */
    private Map<Element, MenuBarItem> menuBarItems = new LinkedHashMap<Element, MenuBarItem>();

    /** Store selected Menu Bar item. */
    private MenuBarItem selectedMenuBarItem;

    /** Working table, cells of which are contains element of Menu. */
    private MenuBarTable table;

    /** Create MenuBar. */
    public MenuBar() {
        absolutePanel = new AbsolutePanel();
        initWidget(absolutePanel);
        absolutePanel.setStyleName(resources.menuCss().menuBar());

        table = new MenuBarTable();
        table.setStyleName(resources.menuCss().menuBarTable());
        table.setCellPadding(0);
        table.setCellSpacing(0);
        DOM.setElementAttribute(table.getElement(), "border", "0");
        absolutePanel.add(table);
    }

    /**
     * Create and add new item in menu.
     *
     * @param title
     *         title of new item
     * @return new instance of MenuBarItem which extends MenuItem
     */
    public MenuItem addItem(String title) {
        return addItem(null, title);
    }

    /**
     * Create and add new item in menu.
     *
     * @param title
     *         title of new item
     * @param command
     *         command, which will be executed when menu item will be selected
     * @return new instance of MenuBarItem which extends MenuItem
     */
    public Item addItem(String title, Command command) {
        return addItem(null, title, command);
    }

    /**
     * Create and add new item in menu.
     *
     * @param image
     *         item's icon which must be represented as HTML image. Image must be prepared like "<img ... />" tag
     * @param title
     *         title of new item
     * @return new instance of MenuBarItem which extends MenuItem
     */
    public MenuItem addItem(ImageResource image, String title) {
        return addItem(image, title, null);
    }

    /**
     * Create and add new item in menu.
     *
     * @param image
     *         item's icon which must be represented as HTML image. Image must be prepared like "<img ... />" tag
     * @param title
     *         title of new item
     * @param command
     *         command, which will be executed when menu item will be selected
     * @return new instance of MenuBarItem which extends MenuItem
     */
    public MenuItem addItem(ImageResource image, String title, Command command) {
        table.setText(0, menuBarItems.size(), title);
        Element element = table.getCellFormatter().getElement(0, menuBarItems.size());
        MenuBarItem item = new MenuBarItem(image, title, element, this);

        item.onMouseOut();
        menuBarItems.put(element, item);
        return item;
    }

    /**
     * Get list of items.
     *
     * @return list of items
     */
    public StringMap<Item> getItems() {
        StringMap<Item> items = Collections.createStringMap();

        for (MenuBarItem item : menuBarItems.values()) {
            items.put(item.getTitle(), item);
        }

        return items;
    }

    /** Handle closing of all popup windows. */
    public void onCloseMenu() {
        selectedMenuBarItem.setNormalState();
        selectedMenuBarItem = null;
        lockLayer = null;
    }

    /** Handle selection of Menu Item. */
    public void onMenuItemSelected(Item Item) {
        if (Item instanceof MenuBarItem) {
            MenuBarItem item = (MenuBarItem)Item;
            if (selectedMenuBarItem != null && selectedMenuBarItem != Item) {
                selectedMenuBarItem.setNormalState();
                selectedMenuBarItem.closePopupMenu();
            }

            selectedMenuBarItem = item;
        } else if (Item instanceof PopupMenuItem) {
            lockLayer.close();
            lockLayer = null;
        }
    }

    /**
     * Open Popup Menu.
     *
     * @param item
     *         - popup menu item.
     */
    public void openPopupMenu(MenuBarItem item) {
        if (lockLayer == null) {
            int top = getAbsoluteTop() + getOffsetHeight();
            lockLayer = new MenuLockLayer(this, top);
        }

        item.openPopupMenu(lockLayer);
    }

    private String toString(Item Item, int depth) {
        String prefix = "";
        for (int i = 0; i < depth; i++) {
            prefix += "        ";
        }

        String str = "";
        if (Item.getTitle() == null) {
            str += prefix + "-------------------------------\r\n";
        } else {
            str += prefix + "[ " + Item.getTitle() + " ]\r\n";
        }

        for (Item childIten : Item.getItems().asIterable()) {
            str += toString(childIten, depth + 1);
        }

        return str;
    }

    @Override
    public String toString() {
        String str = "";

        for (Item Item : menuBarItems.values()) {
            str += toString(Item, 0);
        }

        return str;
    }

}
