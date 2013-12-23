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

import com.codenvy.ide.collections.Array;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;
import java.util.List;

/**
 * PopupMenu is visual component represents all known Popup Menu.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 */

public class PopupMenu extends Composite {

    private static final PopupResources POPUP_RESOURCES = GWT.create(PopupResources.class);

    static {
        POPUP_RESOURCES.popup().ensureInjected();
    }

    /** This table uses for handling mouse events. */
    private class PopupMenuTable extends FlexTable {

        public PopupMenuTable() {
            sinkEvents(Event.ONMOUSEOVER | Event.ONCLICK);
        }

        @Override
        public void onBrowserEvent(Event event) {
            Element td = getEventTargetCell(event);

            if (td == null) {
                return;
            }
            Element tr = DOM.getParent(td);

            String index = DOM.getElementAttribute(tr, "item-index");
            if (index == null || "".equals(index)) {
                return;
            }

            switch (DOM.eventGetType(event)) {
                case Event.ONMOUSEOVER:
                    onRowHovered(tr);
                    break;

                case Event.ONCLICK:
                    onRowClicked(tr);
                    break;
            }

        }
    }

    private boolean isRowEnabled(Element tr) {
        if (tr == null) {
            return false;
        }

        String index = DOM.getElementAttribute(tr, "item-index");
        if (index == null || "".equals(index)) {
            return false;
        }

        String enabled = DOM.getElementAttribute(tr, "item-enabled");
        if (enabled == null || "".equals(enabled) || "false".equals(enabled)) {
            return false;
        }

        int itemIndex = Integer.parseInt(index);
        Item menuItem = items.get(itemIndex);
        return menuItem.isEnabled();

    }

    /** Working variable is needs to indicate when PopupMenu has at list one MenuItem with selected state. */
    private boolean hasCheckedItems;

    /** Callback uses for notify Parent Menu when menu item is selecred. */
    private ItemSelectedHandler itemSelectedCallback;

    /**
     * Lock layer uses as root for displaying this PopupMenu and uses for locking screen and hiding menu when user just clicked outside
     * menu.
     */
    private MenuLockLayer lockLayer;

    /** List of Menu items. */
    private List<Item> items;

    /** Contains opened sub Popup Menu. */
    private PopupMenu openedSubPopup;

    /** Contains HTML element ( <TR> ) which is hovered for the current time. */
    private Element hoveredTR;

    /**
     * Working variable.
     * PopupMenu panel.
     */
    private SimplePanel popupMenuPanel;

    /** Working variable. Special table uses for handling mouse events. */
    private PopupMenuTable table;

    /**
     * Prefix to be appended to the ID for each menu item.
     * This is debug feature.
     */
    private String itemIdPrefix;

    public PopupMenu(Array<Item> menuItems, MenuLockLayer lockLayer, ItemSelectedHandler itemSelectedCallback) {
        this(menuItems, lockLayer, itemSelectedCallback, null);
    }

    /**
     * Create PopupMenu
     *
     * @param menuItems
     *         - list of menu items.
     * @param lockLayer
     *         - lock layer, uses as rot for attaching this popup menu.
     * @param itemSelectedCallback
     *         - callback, uses for notifying parent menu when menu item is selected.
     */
    public PopupMenu(Array<Item> menuItems, MenuLockLayer lockLayer, ItemSelectedHandler itemSelectedCallback,
                     String itemIdPrefix) {
        this.items = new ArrayList<Item>();
        this.itemIdPrefix = itemIdPrefix;

      /*
       * show only visible items and delimiters
       */
        for (Item item : menuItems.asIterable()) {
            if (item.getTitle() == null) {
                this.items.add(item);
                continue;
            }

            if (item.isVisible()) {
                this.items.add(item);
            }
        }

        filterMenuItems();

        this.lockLayer = lockLayer;
        this.itemSelectedCallback = itemSelectedCallback;

        popupMenuPanel = new SimplePanel();
        initWidget(popupMenuPanel);

        popupMenuPanel.addDomHandler(new MouseOutHandler() {
            @Override
            public void onMouseOut(MouseOutEvent event) {
                closeSubPopupTimer.cancel();

                PopupMenu.this.setStyleNormal(hoveredTR);
                hoveredTR = null;
            }
        }, MouseOutEvent.getType());

        popupMenuPanel.setStyleName(POPUP_RESOURCES.popup().popupMenuMain());

        hasCheckedItems = hasCheckedItems();

        redraw();
    }

    private void filterMenuItems() {
      /*
       * remove delimiters from start
       */
        while (items.size() > 0 && items.get(0).getTitle() == null) {
            items.remove(0);
        }

      /*
       * remove delimiters from end
       */
        while (items.size() > 0 && items.get(items.size() - 1).getTitle() == null) {
            items.remove(items.size() - 1);
        }

      /*
       * avoid sequence of two delimiters
       */
        boolean found = false;
        int index = 0;
        while (index < items.size()) {
            if (items.get(index).getTitle() == null) {
                if (found) {
                    items.remove(index);
                    continue;
                }

                found = true;
            } else {
                found = false;
            }

            index++;
        }

    }

    /** Close this Popup Menu. */
    public void closePopup() {
        if (openedSubPopup != null) {
            openedSubPopup.closePopup();
        }
        removeFromParent();
    }

    /** Render Popup Menu component. */
    private void redraw() {
        String idPrefix = itemIdPrefix;
        if (idPrefix == null) {
            idPrefix = "";
        } else {
            idPrefix += "/";
        }

        table = new PopupMenuTable();
        table.setStyleName(POPUP_RESOURCES.popup().popupMenuTable());
        table.setCellPadding(0);
        table.setCellSpacing(0);
        DOM.setElementAttribute(table.getElement(), "border", "0");

        for (int i = 0; i < items.size(); i++) {
            Item menuItem = items.get(i);

            if (menuItem.getTitle() == null) {
                table.getFlexCellFormatter().setColSpan(i, 0, hasCheckedItems ? 5 : 4);
                table.setHTML(i, 0, "<nobr><hr noshade=\"noshade\" style=\"color:#BBBBBB;\" size=\"1\"></nobr>");
                table.getCellFormatter().setStyleName(i, 0, POPUP_RESOURCES.popup().popupMenuDelimiter());
            } else {
                table.setWidget(i, 0, new Image(menuItem.getImage() == null ? POPUP_RESOURCES.blank() : menuItem.getImage()));
                table.getCellFormatter().setStyleName(i, 0,
                                                      menuItem.isEnabled() ? POPUP_RESOURCES.popup().popupMenuIconField()
                                                                           : POPUP_RESOURCES.popup().popupMenuIconFieldDisabled());

                int work = 1;

                if (hasCheckedItems) {
                    String checkImage;
                    if (menuItem.isSelected()) {
                        checkImage = AbstractImagePrototype.create(POPUP_RESOURCES.check()).getHTML();
                    } else {
                        checkImage = AbstractImagePrototype.create(POPUP_RESOURCES.blank()).getHTML();
                    }

                    table.setHTML(i, work, checkImage);
                    table.getCellFormatter().setStyleName(i, work,
                                                          menuItem.isEnabled() ? POPUP_RESOURCES.popup().popupMenuCheckField()
                                                                               : POPUP_RESOURCES.popup().popupMenuCheckFieldDisabled());
                    work++;
                }

                table.setHTML(i, work, "<nobr id=\"" + idPrefix + menuItem.getTitle() + "\">" + menuItem.getTitle()
                                       + "</nobr>");
                table.getCellFormatter().setStyleName(i, work,
                                                      menuItem.isEnabled() ? POPUP_RESOURCES.popup().popupMenuTitleField()
                                                                           : POPUP_RESOURCES.popup().popupMenuTitleFieldDisabled());

                work++;

                String hotKey = menuItem.getHotKey();
                if (hotKey == null) {
                    hotKey = "&nbsp;";
                } else {
                    hotKey =
                            "<nobr><font color=\"" + (menuItem.isEnabled() ? "#000088" : "#AAAAAA") + "\">&nbsp;[" + hotKey
                            + "]&nbsp;</font></nobr>";
                }

                table.setHTML(i, work, hotKey);
                table.getCellFormatter().setStyleName(i, work,
                                                      menuItem.isEnabled() ? POPUP_RESOURCES.popup().popupMenuHotKeyField()
                                                                           : POPUP_RESOURCES.popup().popupMenuHotKeyFieldDisabled());

                work++;

                if (menuItem.getItems().size() == 0) {
                    Image image = new Image(POPUP_RESOURCES.blank());
                    image.setStyleName(POPUP_RESOURCES.popup().popupMenuSubMenuImage());
                    table.setWidget(i, work, image);
                    table.getCellFormatter().setStyleName(i, work,
                                                          menuItem.isEnabled() ? POPUP_RESOURCES.popup().popupMenuSubMenuField()
                                                                               : POPUP_RESOURCES.popup().popupMenuSubMenuFieldDisabled());
                } else {
                    Image image = new Image(POPUP_RESOURCES.subMenu());
                    image.setStyleName(POPUP_RESOURCES.popup().popupMenuSubMenuImage());
                    table.setWidget(i, work, image);
                    table.getCellFormatter().setStyleName(i, work,
                                                          menuItem.isEnabled() ? POPUP_RESOURCES.popup().popupMenuSubMenuField()
                                                                               : POPUP_RESOURCES.popup().popupMenuSubMenuFieldDisabled());
                }

                work++;

                DOM.setElementAttribute(table.getRowFormatter().getElement(i), "item-index", "" + i);
                DOM.setElementAttribute(table.getRowFormatter().getElement(i), "item-enabled", "" + menuItem.isEnabled());
            }

//            Element row = table.getRowFormatter().getElement(i);
//            for (Map.Entry<String, String> attrEntry : menuItem.getAttributes().entrySet()) {
//                row.setAttribute(attrEntry.getKey(), attrEntry.getValue());
//            }
        }

        popupMenuPanel.add(table);
    }

    /** @return true when at list one item from list of menu items has selected state. */
    private boolean hasCheckedItems() {
        for (int i = 0; i < items.size(); i++) {
            Item menuItem = items.get(i);
            if (menuItem.isSelected()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Handling MouseOut event.
     *
     * @param row
     *         - element to be processed.
     */
    protected void setStyleNormal(Element row) {
        if (row == null) {
            return;
        }

        if (hasCheckedItems) {
            Element iconTD = DOM.getChild(row, 0);
            Element checkTD = DOM.getChild(row, 1);
            Element titleTD = DOM.getChild(row, 2);
            Element hotKeyTD = DOM.getChild(row, 3);
            Element submenuTD = DOM.getChild(row, 4);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconField());
            checkTD.setClassName(POPUP_RESOURCES.popup().popupMenuCheckField());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleField());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyField());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuField());
        } else {
            Element iconTD = DOM.getChild(row, 0);
            Element titleTD = DOM.getChild(row, 1);
            Element hotKeyTD = DOM.getChild(row, 2);
            Element submenuTD = DOM.getChild(row, 3);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconField());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleField());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyField());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuField());
        }
    }

    private void setStyleHovered(Element tr) {
        if (hasCheckedItems) {
            Element iconTD = DOM.getChild(tr, 0);
            Element checkTD = DOM.getChild(tr, 1);
            Element titleTD = DOM.getChild(tr, 2);
            Element hotKeyTD = DOM.getChild(tr, 3);
            Element submenuTD = DOM.getChild(tr, 4);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconFieldOver());
            checkTD.setClassName(POPUP_RESOURCES.popup().popupMenuCheckFieldOver());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleFieldOver());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyFieldOver());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuFieldOver());
        } else {
            Element iconTD = DOM.getChild(tr, 0);
            Element titleTD = DOM.getChild(tr, 1);
            Element hotKeyTD = DOM.getChild(tr, 2);
            Element submenuTD = DOM.getChild(tr, 3);

            iconTD.setClassName(POPUP_RESOURCES.popup().popupMenuIconFieldOver());
            titleTD.setClassName(POPUP_RESOURCES.popup().popupMenuTitleFieldOver());
            hotKeyTD.setClassName(POPUP_RESOURCES.popup().popupMenuHotKeyFieldOver());
            submenuTD.setClassName(POPUP_RESOURCES.popup().popupMenuSubMenuFieldOver());
        }
    }

    /**
     * Handling MouseOver event.
     *
     * @param tr
     *         - element to be processed.
     */
    protected void onRowHovered(Element tr) {
        if (tr == hoveredTR) {
            return;
        }

        setStyleNormal(hoveredTR);
        if (!isRowEnabled(tr)) {
            hoveredTR = null;
            return;
        }

        hoveredTR = tr;
        setStyleHovered(tr);

        int itemIndex = Integer.parseInt(DOM.getElementAttribute(tr, "item-index"));
        Item menuItem = items.get(itemIndex);
        openSubPopupTimer.cancel();
        if (menuItem.getItems().size() != 0) {
            openSubPopupTimer.schedule(300);
        } else {
            closeSubPopupTimer.cancel();
            closeSubPopupTimer.schedule(200);
        }
    }

    /**
     * Handle Mouse Click
     *
     * @param tr
     */
    protected void onRowClicked(Element tr) {
        if (!isRowEnabled(tr)) {
            return;
        }

        int itemIndex = Integer.parseInt(DOM.getElementAttribute(tr, "item-index"));
        Item menuItem = items.get(itemIndex);
        if (menuItem.getItems().size() == 0) {
            if (itemSelectedCallback != null) {
                itemSelectedCallback.onMenuItemSelected(menuItem);
            }

            if (menuItem.getCommand() != null) {
                menuItem.getCommand().execute();
            }
        } else {
            openSubPopup(tr);
        }
    }

    private Timer openSubPopupTimer = new Timer() {
        @Override
        public void run() {
            openSubPopup(hoveredTR);
        }
    };

    private void openSubPopup(final Element tableRowElement) {
        if (tableRowElement == null) {
            return;
        }

        int itemIndex = Integer.parseInt(DOM.getElementAttribute(tableRowElement, "item-index"));
        PopupMenuItem menuItem = (PopupMenuItem)items.get(itemIndex);

        if (openedSubPopup != null) {
            openedSubPopup.closePopup();
        }

        String idPrefix = itemIdPrefix;
        if (idPrefix != null) {
            idPrefix += "/" + menuItem.getTitle();
        }
        openedSubPopup = new PopupMenu(menuItem.getItems(), lockLayer, itemSelectedCallback, idPrefix);

        final int HORIZONTAL_OFFSET = 3;
        final int VERTICAL_OFFSET = 1;

        openedSubPopup.getElement().getStyle().setVisibility(Visibility.HIDDEN);
        lockLayer.add(openedSubPopup, 0, 0);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int left = getAbsoluteLeft() + getOffsetWidth() - HORIZONTAL_OFFSET;
                int top = tableRowElement.getAbsoluteTop() - lockLayer.getTopOffset() - VERTICAL_OFFSET;

                if (left + openedSubPopup.getOffsetWidth() > Window.getClientWidth()) {
                    if (left > openedSubPopup.getOffsetWidth()) {
                        left = getAbsoluteLeft() - openedSubPopup.getOffsetWidth() + HORIZONTAL_OFFSET;
                    } else {
                        int diff = left + openedSubPopup.getOffsetWidth() - Window.getClientWidth();
                        left -= diff;
                    }
                }

                if (top + openedSubPopup.getOffsetHeight() > Window.getClientHeight()) {
                    if (top > openedSubPopup.getOffsetHeight()) {
                        top = tableRowElement.getAbsoluteTop() - openedSubPopup.getOffsetHeight() + VERTICAL_OFFSET;
                    } else {
                        int diff = top + openedSubPopup.getOffsetHeight() - Window.getClientHeight();
                        top -= diff;
                    }
                }

                openedSubPopup.getElement().getStyle().setLeft(left, Unit.PX);
                openedSubPopup.getElement().getStyle().setTop(top, Unit.PX);
                openedSubPopup.getElement().getStyle().setVisibility(Visibility.VISIBLE);
            }
        });
    }

    private Timer closeSubPopupTimer = new Timer() {
        @Override
        public void run() {
            if (openedSubPopup != null) {
                openedSubPopup.closePopup();
                openedSubPopup = null;
            }

        }
    };

    interface PopupResources extends ClientBundle {

        @Source({"popup-menu.css", "com/codenvy/ide/api/ui/style.css"})
        Css popup();

        @Source("check.gif")
        ImageResource check();

        @Source("blank.png")
        ImageResource blank();

        @Source("submenu.gif")
        ImageResource subMenu();
    }

    interface Css extends CssResource {

        String popupMenuSubMenuFieldDisabled();

        String popupMenuHotKeyFieldDisabled();

        String popupMenuTitleField();

        String popupMenuIconField();

        String popupMenuDelimiter();

        String popupMenuIconFieldDisabled();

        String popupMenuIconFieldOver();

        String popupMenuCheckFieldOver();

        String popupMenuCheckField();

        String popupMenuTable();

        String popupMenuSubMenuField();

        String popupMenuMain();

        String popupMenuTitleFieldOver();

        String popupMenuTitleFieldDisabled();

        String popupMenuCheckFieldDisabled();

        String popupMenuHotKeyFieldOver();

        String popupMenuSubMenuFieldOver();

        String popupMenuHotKeyField();

        String popupMenuSubMenuImage();
    }
}
