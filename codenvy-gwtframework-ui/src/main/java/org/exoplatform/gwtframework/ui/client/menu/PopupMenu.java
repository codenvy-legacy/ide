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

package org.exoplatform.gwtframework.ui.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.Style.Visibility;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.user.client.*;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * <p/>
 * PopupMenu is visual component represents all known Popup Menu.
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PopupMenu extends Composite {

    private interface Images {

        String CHECK = GWT.getModuleBaseURL() + "ui/popup/check.gif";

        String SUBMENU = GWT.getModuleBaseURL() + "ui/popup/submenu.gif";

        String BLANK = GWT.getModuleBaseURL() + "ui/blank.gif";

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
        PopupMenuItem menuItem = (PopupMenuItem)menuItems.get(itemIndex);
        if (!menuItem.isEnabled()) {
            return false;
        }

        return true;
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

    /** List of Menu Items. */
    private List<MenuItem> menuItems;

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

    public PopupMenu(List<MenuItem> menuItems, MenuLockLayer lockLayer, ItemSelectedHandler itemSelectedCallback) {
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
    public PopupMenu(List<MenuItem> menuItems, MenuLockLayer lockLayer, ItemSelectedHandler itemSelectedCallback,
                     String itemIdPrefix) {
        this.menuItems = new ArrayList<MenuItem>();
        this.itemIdPrefix = itemIdPrefix;

      /*
       * show only visible items and delimiters
       */
        for (MenuItem item : menuItems) {
            if (item.getTitle() == null) {
                this.menuItems.add(item);
                continue;
            }

            if (item.isVisible()) {
                this.menuItems.add(item);
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

        popupMenuPanel.setStyleName(PopupMenuStyle.MENU_MAIN);

        hasCheckedItems = hasCheckedItems();

        redraw();
    }

    private void filterMenuItems() {
      /*
       * remove delimiters from start
       */
        while (menuItems.size() > 0 && menuItems.get(0).getTitle() == null) {
            menuItems.remove(0);
        }

      /*
       * remove delimiters from end
       */
        while (menuItems.size() > 0 && menuItems.get(menuItems.size() - 1).getTitle() == null) {
            menuItems.remove(menuItems.size() - 1);
        }

      /*
       * avoid sequence of two delimiters
       */
        boolean found = false;
        int index = 0;
        while (index < menuItems.size()) {
            if (menuItems.get(index).getTitle() == null) {
                if (found) {
                    menuItems.remove(index);
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
        table.setStyleName(PopupMenuStyle.MENU_TABLE);
        table.setCellPadding(0);
        table.setCellSpacing(0);
        DOM.setElementAttribute(table.getElement(), "border", "0");

        for (int i = 0; i < menuItems.size(); i++) {
            PopupMenuItem menuItem = (PopupMenuItem)menuItems.get(i);

            if (menuItem.getTitle() == null) {
                table.getFlexCellFormatter().setColSpan(i, 0, hasCheckedItems ? 5 : 4);
                //table.setHTML(i, 0, "<nobr><hr noshade=\"noshade\" style=\"color:#BBBBBB;\" size=\"1\"></nobr>");
                table.setHTML(i, 0, "<div style=\"width:100%; height:1px; background: #C0C0C0;\"></div>");
                table.getCellFormatter().setStyleName(i, 0, PopupMenuStyle.DELIMITER);
            } else {
                table.setHTML(i, 0, menuItem.getIcon());
                table.getCellFormatter().setStyleName(i, 0,
                                                      menuItem.isEnabled() ? PopupMenuStyle.ICON_FIELD
                                                                           : PopupMenuStyle.ICON_FIELD_DISABLED);

                int work = 1;

                if (hasCheckedItems) {
                    String checkImage;
                    if (menuItem.isSelected()) {
                        //checkImage = ExoStyle.getEXoStyleURL() + "popupMenu/check.gif";
                        checkImage = Images.CHECK;
                    } else {
                        checkImage = Images.BLANK;
                    }

                    table.setHTML(i, work, "<img src=\"" + checkImage + "\">");
                    table.getCellFormatter().setStyleName(i, work,
                                                          menuItem.isEnabled() ? PopupMenuStyle.CHECK_FIELD
                                                                               : PopupMenuStyle.CHECK_FIELD_DISABLED);
                    work++;
                }

                table.setHTML(i, work, "<nobr id=\"" + idPrefix + menuItem.getTitle() + "\">" + menuItem.getTitle()
                                       + "</nobr>");
                table.getCellFormatter().setStyleName(i, work,
                                                      menuItem.isEnabled() ? PopupMenuStyle.TITLE_FIELD
                                                                           : PopupMenuStyle.TITLE_FIELD_DISABLED);

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
                                                      menuItem.isEnabled() ? PopupMenuStyle.KEY_FIELD : PopupMenuStyle.KEY_FIELD_DISABLED);

                work++;

                if (menuItem.getItems().size() == 0) {
                    table.setHTML(i, work, "<img src=\"" + Images.BLANK + "\" class=\"" + PopupMenuStyle.SUBMENU_IMAGE
                                           + "\" />");
                    table.getCellFormatter().setStyleName(i, work,
                                                          menuItem.isEnabled() ? PopupMenuStyle.SUBMENU_FIELD
                                                                               : PopupMenuStyle.SUBMENU_FIELD_DISABLED);
                } else {
                    table.setHTML(i, work, "<img src=\"" + Images.SUBMENU + "\" class=\"" + PopupMenuStyle.SUBMENU_IMAGE
                                           + "\" />");
                    table.getCellFormatter().setStyleName(i, work,
                                                          menuItem.isEnabled() ? PopupMenuStyle.SUBMENU_FIELD
                                                                               : PopupMenuStyle.SUBMENU_FIELD_DISABLED);
                }

                work++;

                DOM.setElementAttribute(table.getRowFormatter().getElement(i), "item-index", "" + i);
                DOM.setElementAttribute(table.getRowFormatter().getElement(i), "item-enabled", "" + menuItem.isEnabled());
            }

            Element row = table.getRowFormatter().getElement(i);
            for (Map.Entry<String, String> attrEntry : menuItem.getAttributes().entrySet()) {
                row.setAttribute(attrEntry.getKey(), attrEntry.getValue());
            }
        }

        popupMenuPanel.add(table);
    }

    /** @return true when at list one item from list of menu items has selected state. */
    private boolean hasCheckedItems() {
        for (int i = 0; i < menuItems.size(); i++) {
            PopupMenuItem menuItem = (PopupMenuItem)menuItems.get(i);
            if (menuItem.isSelected()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Handling MouseOut event.
     *
     * @param tr
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

            iconTD.setClassName(PopupMenuStyle.ICON_FIELD);
            checkTD.setClassName(PopupMenuStyle.CHECK_FIELD);
            titleTD.setClassName(PopupMenuStyle.TITLE_FIELD);
            hotKeyTD.setClassName(PopupMenuStyle.KEY_FIELD);
            submenuTD.setClassName(PopupMenuStyle.SUBMENU_FIELD);
        } else {
            Element iconTD = DOM.getChild(row, 0);
            Element titleTD = DOM.getChild(row, 1);
            Element hotKeyTD = DOM.getChild(row, 2);
            Element submenuTD = DOM.getChild(row, 3);

            iconTD.setClassName(PopupMenuStyle.ICON_FIELD);
            titleTD.setClassName(PopupMenuStyle.TITLE_FIELD);
            hotKeyTD.setClassName(PopupMenuStyle.KEY_FIELD);
            submenuTD.setClassName(PopupMenuStyle.SUBMENU_FIELD);
        }
    }

    private void setStyleHovered(Element tr) {
        if (hasCheckedItems) {
            Element iconTD = DOM.getChild(tr, 0);
            Element checkTD = DOM.getChild(tr, 1);
            Element titleTD = DOM.getChild(tr, 2);
            Element hotKeyTD = DOM.getChild(tr, 3);
            Element submenuTD = DOM.getChild(tr, 4);

            iconTD.setClassName(PopupMenuStyle.ICON_FIELD_OVER);
            checkTD.setClassName(PopupMenuStyle.CHECK_FIELD_OVER);
            titleTD.setClassName(PopupMenuStyle.TITLE_FIELD_OVER);
            hotKeyTD.setClassName(PopupMenuStyle.KEY_FIELD_OVER);
            submenuTD.setClassName(PopupMenuStyle.SUBMENU_FIELD_OVER);
        } else {
            Element iconTD = DOM.getChild(tr, 0);
            Element titleTD = DOM.getChild(tr, 1);
            Element hotKeyTD = DOM.getChild(tr, 2);
            Element submenuTD = DOM.getChild(tr, 3);

            iconTD.setClassName(PopupMenuStyle.ICON_FIELD_OVER);
            titleTD.setClassName(PopupMenuStyle.TITLE_FIELD_OVER);
            hotKeyTD.setClassName(PopupMenuStyle.KEY_FIELD_OVER);
            submenuTD.setClassName(PopupMenuStyle.SUBMENU_FIELD_OVER);
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
        PopupMenuItem menuItem = (PopupMenuItem)menuItems.get(itemIndex);
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
        PopupMenuItem menuItem = (PopupMenuItem)menuItems.get(itemIndex);
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
        PopupMenuItem menuItem = (PopupMenuItem)menuItems.get(itemIndex);

        if (openedSubPopup != null) {
            openedSubPopup.closePopup();
        }

        String idPrefix = itemIdPrefix;
        if (idPrefix != null) {
            idPrefix += "/" + menuItem.getTitle();
        }
        openedSubPopup = new PopupMenu(menuItem.getItems(), lockLayer, itemSelectedCallback, idPrefix);

        final int HORIZONTAL_OFFSET = 3;
        final int VERTIVAL_OFFSET = 1;

//      final int left = getAbsoluteLeft() + getOffsetWidth() - HORIZONTAL_OFFSET;
//      final int top = tableRowElement.getAbsoluteTop() - lockLayer.getTopOffset() - VERTIVAL_OFFSET;

        openedSubPopup.getElement().getStyle().setVisibility(Visibility.HIDDEN);
//      lockLayer.add(openedSubPopup, left, top);
        lockLayer.add(openedSubPopup, 0, 0);

        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                int left = getAbsoluteLeft() + getOffsetWidth() - HORIZONTAL_OFFSET;
                int top = tableRowElement.getAbsoluteTop() - lockLayer.getTopOffset() - VERTIVAL_OFFSET;

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
                        top = tableRowElement.getAbsoluteTop() - openedSubPopup.getOffsetHeight() + VERTIVAL_OFFSET;
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

}
