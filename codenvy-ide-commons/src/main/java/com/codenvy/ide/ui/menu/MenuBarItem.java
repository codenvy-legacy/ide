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
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.JsonStringMap;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 *          <p/>
 *          Menu bar is implementation of Menu interface and represents a visual component.
 */
public class MenuBarItem implements MenuItem, ItemSelectedHandler, UpdateItemEnablingCallback {

    /**
     * Working variable:
     * is need to store hovered or normal state.
     */
    boolean hovered = false;
    /**
     * Working variable:
     * is need to store pressed state.
     */
    boolean pressed = false;
    /** Map of children */
    private JsonStringMap<Item> children = Collections.createStringMap();
    /** Command which will be called just after menu ber item will be selected. */
    private Command command;
    /** Visual element which is table cell. */
    private Element element;
    /** Enabled or disabled state */
    private boolean enabled = true;
    private boolean             hasVisibleItems;
    /** Hot Key associated with this item. */
    private String              hotKey;
    private ImageResource       image;
    /**
     *
     */
    private ItemSelectedHandler itemSelectedHandler;
    /**
     * Working variable:
     * is needs to store opened Popup menu.
     */
    private PopupMenu           popupMenu;
    /** Selected state. */
    private boolean             selected;
    /** Title of Menu Bar Item */
    private String              title;
    /** Visibility state. */
    private boolean             visible;

    /**
     * @param image
     *         - image as ImageResource
     * @param title
     *         - title
     * @param element
     *         - working element ( it must be cell of table )
     * @param callback
     *         - callBack for notifying Menu when menu item is selected and is need to close all pupups
     */
    public MenuBarItem(ImageResource image, String title, Element element, ItemSelectedHandler callback) {
        this.image = image;
        this.title = title;
        this.element = element;
        this.itemSelectedHandler = callback;
    }


    /** {@inheritDoc} */
    public MenuItem addItem(String title) {
        return addItem(null, title, null);
    }

    /** {@inheritDoc} */
    public Item addItem(String title, Command command) {
        return addItem(null, title, command);
    }

    /** {@inheritDoc} */
    @Override
    public Item addItem(ImageResource image, String title) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void addItem(Item item) {
    }

    /** {@inheritDoc} */
    public MenuItem addItem(ImageResource image, String title, Command command) {
        PopupMenuItem item = new PopupMenuItem(image, title, command);
        item.setUpdateItemEnablingCallback(this);
        children.put(title, item);

        hasVisibleItems = hasVisibleItems(children);
        updateEnabledState();

        return item;
    }

    /** Close opened Popup Menu. */
    public void closePopupMenu() {
        popupMenu.closePopup();
    }

    /** {@inheritDoc} */
    public Command getCommand() {
        return command;
    }

    /** {@inheritDoc} */
    public void setCommand(Command command) {
        this.command = command;
    }

    /** {@inheritDoc} */
    public String getHotKey() {
        return hotKey;
    }

    /** {@inheritDoc} */
    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

    /** {@inheritDoc} */
    public ImageResource getImage() {
        return image;
    }

    /** {@inheritDoc} */
    public void setImage(ImageResource image) {
        this.image = image;
    }

    /** {@inheritDoc} */
    public Array<Item> getItems() {
        return children.getValues();
    }

    @Override
    public MenuItem getChildren(String title) {
        return (MenuItem)children.get(title);
    }

    /** {@inheritDoc} */
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc} */
    public void setTitle(String title) {
        this.title = title;
    }

    private boolean hasVisibleItems(JsonStringMap<Item> items) {
        Array<String> keys = items.getKeys();
        for (String key : keys.asIterable()) {
            Item item = items.get(key);
            if (item.getTitle() == null) {
                continue;
            }

            if (item.isVisible()) {
                return true;
            }

        }


        return false;
    }

    /** {@inheritDoc} */
    public boolean isEnabled() {
        return enabled;
    }

    /** {@inheritDoc} */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        updateEnabledState();
    }

    /** {@inheritDoc} */
    public boolean isSelected() {
        return selected;
    }

    /** {@inheritDoc} */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /** {@inheritDoc} */
    public boolean isVisible() {
        return visible;
    }

    /** {@inheritDoc} */
    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    /** {@inheritDoc} */
    public void onMenuItemSelected(Item Item) {
        setNormalState();
        itemSelectedHandler.onMenuItemSelected(Item);
    }

    /** Mouse Down handler */
    public boolean onMouseDown() {
        if (enabled && hasVisibleItems) {
            element.setClassName(MenuBar.resources.menuCss().menuBarItemSelected());
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
            element.setClassName(MenuBar.resources.menuCss().menuBarItem());
        } else {
            element.setClassName(MenuBar.resources.menuCss().menuBarItemDisabled());
        }
    }

    /** Mouse Over Handler */
    public void onMouseOver() {
        if (pressed) {
            return;
        }

        if (enabled && hasVisibleItems) {
            element.setClassName(MenuBar.resources.menuCss().menuBarItemOver());
            hovered = true;
        }
    }

    /** {@inheritDoc} */
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
        popupMenu = new PopupMenu(children.getValues(), menuLockLayer, this, "topmenu/" + title);
        menuLockLayer.add(popupMenu, x, y);
    }

    /** Reset visual state of Menu Bar Item to default. */
    public void setNormalState() {
        pressed = false;
        element.setClassName(MenuBar.resources.menuCss().menuBarItem());
    }

    private void updateEnabledState() {
        pressed = false;
        if (enabled && hasVisibleItems) {
            element.setClassName(MenuBar.resources.menuCss().menuBarItem());
        } else {
            element.setClassName(MenuBar.resources.menuCss().menuBarItemDisabled());
        }

    }

}
