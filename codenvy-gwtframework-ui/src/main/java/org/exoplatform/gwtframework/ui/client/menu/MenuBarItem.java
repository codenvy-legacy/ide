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

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 *          <p/>
 *          Menu bar is implementation of Menu interface and represents a visual component.
 */
public class MenuBarItem extends MenuItem implements ItemSelectedHandler, UpdateItemEnablingCallback {

    /** List of children */
    private List<MenuItem> children = new ArrayList<MenuItem>();

    /** Command which will be called just after menu ber item will be selected. */
    private Command command;

    /** Visual element which is table cell. */
    private Element element;

    /** Enabled or disabled state */
    private boolean enabled = true;

    private boolean hasVisibleItems;

    /** Hot Key associated with this item. */
    private String hotKey;

    /**
     * Working variable:
     * is need to store hovered or normal state.
     */
    boolean hovered = false;

    /** Icon as String which is represents already builded "<img ... />" tag. */
    private String icon;

    /**
     *
     */
    private ItemSelectedHandler itemSelectedHandler;

    /**
     * Working variable:
     * is needs to store opened Popup menu.
     */
    private PopupMenu popupMenu;

    /**
     * Working variable:
     * is need to store pressed state.
     */
    boolean pressed = false;

    /** Selected state. */
    private boolean selected;

    /** Title of Menu Bar Item */
    private String title;

    /** Visibility state. */
    private boolean visible;

    /**
     * @param icon
     *         - icon as HTML image for new item. Image must be prepared like "<img ... />" tag
     * @param title
     *         - title
     * @param element
     *         - working element ( it must be cell of table )
     * @param callback
     *         - callBack for notifying Menu when menu item is selected and is need to close all pupups
     */
    public MenuBarItem(String icon, String title, Element element, ItemSelectedHandler callback) {
        this.icon = icon;
        this.title = title;
        this.element = element;
        this.itemSelectedHandler = callback;
    }

    public MenuItem addItem(String title) {
        return addItem(null, title, null);
    }

    public MenuItem addItem(String title, Command command) {
        return addItem(null, title, command);
    }

    public MenuItem addItem(String icon, String title) {
        return addItem(icon, title, null);
    }

    public MenuItem addItem(String icon, String title, Command command) {
        PopupMenuItem item = new PopupMenuItem(icon, title, command);
        item.setUpdateItemEnablingCallback(this);
        children.add(item);

        hasVisibleItems = hasVisibleItems(children);
        updateEnabledState();

        return item;
    }

    /** Close opened Popup Menu. */
    public void closePopupMenu() {
        popupMenu.closePopup();
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getCommand() */
    public Command getCommand() {
        return command;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getHotKey() */
    public String getHotKey() {
        return hotKey;
    }

    public String getIcon() {
        return icon;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getItems() */
    public List<MenuItem> getItems() {
        return children;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getTitle() */
    public String getTitle() {
        return title;
    }

    private boolean hasVisibleItems(List<MenuItem> items) {
        for (MenuItem item : children) {
            if (item.getTitle() == null) {
                continue;
            }

            if (item.isVisible()) {
                return true;
            }
        }

        return false;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#isEnabled() */
    public boolean isEnabled() {
        return enabled;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#isSelected() */
    public boolean isSelected() {
        return selected;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#isVisible() */
    public boolean isVisible() {
        return visible;
    }

    /** @see org.exoplatform.gwtframework.ui.client.ItemSelectedHandler.menu.nn.impl.ItemSelectedCallback#onMenuItemSelected(org
     * .exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem) */
    public void onMenuItemSelected(MenuItem menuItem) {
        setNormalState();
        itemSelectedHandler.onMenuItemSelected(menuItem);
    }

    /** Mouse Down handler */
    public boolean onMouseDown() {
        if (enabled && hasVisibleItems) {
            element.setClassName(MenuBarStyle.ITEM_SELECTED);
            pressed = true;
            itemSelectedHandler.onMenuItemSelected(this);
            return true;
        }

        return false;
    }

    /** Mouse Out Handler */
    public void onMouseOut() {
        if (pressed) {
            return;
        }

        if (enabled && hasVisibleItems) {
            element.setClassName(MenuBarStyle.ITEM);
        } else {
            element.setClassName(MenuBarStyle.ITEM_DISABLED);
        }
    }

    /** Mouse Over Handler */
    public void onMouseOver() {
        if (pressed) {
            return;
        }

        if (enabled && hasVisibleItems) {
            element.setClassName(MenuBarStyle.ITEM_OVER);
            hovered = true;
        }
    }

    public void onUpdateItemEnabling() {
        hasVisibleItems = hasVisibleItems(children);
        updateEnabledState();
    }

    /**
     * Open sub Popup Menu
     *
     * @param menuLockLayer
     *         - lock layer which will receive PopupMenu visual component and
     */
    public void openPopupMenu(MenuLockLayer menuLockLayer) {
        int x = element.getAbsoluteLeft();
        int y = 0;
        popupMenu = new PopupMenu(children, menuLockLayer, this, "topmenu/" + title);
        menuLockLayer.add(popupMenu, x, y);
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setCommand(com.google.gwt.user.client.Command) */
    public void setCommand(Command command) {
        this.command = command;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setEnabled(boolean) */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateEnabledState();
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setHotKey(java.lang.String) */
    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    /** Reset visual state of Menu Bar Item to default. */
    public void setNormalState() {
        pressed = false;
        element.setClassName(MenuBarStyle.ITEM);
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setSelected(boolean) */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setTitle(java.lang.String) */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setVisible(boolean) */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    private void updateEnabledState() {
        pressed = false;
        if (enabled && hasVisibleItems) {
            element.setClassName(MenuBarStyle.ITEM);
        } else {
            element.setClassName(MenuBarStyle.ITEM_DISABLED);
        }

    }

}
