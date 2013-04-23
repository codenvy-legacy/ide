/**
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 *
 */

package com.codenvy.ide.ui.menu;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Element;

/**
 * Created by The eXo Platform SAS .
 *
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
    private JsonStringMap<MenuItem> children = JsonCollections.createStringMap();
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
    public MenuItem addItem(String title, Command command) {
        return addItem(null, title, command);
    }

    /** {@inheritDoc} */
    @Override
    public MenuItem addItem(ImageResource image, String title) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void addItem(MenuItem item) {
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
    public JsonArray<MenuItem> getItems() {
        return children.getValues();
    }

    @Override
    public MenuItem getChildren(String title) {
        return children.get(title);
    }

    /** {@inheritDoc} */
    public String getTitle() {
        return title;
    }

    /** {@inheritDoc} */
    public void setTitle(String title) {
        this.title = title;
    }

    private boolean hasVisibleItems(JsonStringMap<MenuItem> items) {
        JsonArray<String> keys = items.getKeys();
        for (String key : keys.asIterable()) {
            MenuItem item = items.get(key);
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
    public void onMenuItemSelected(MenuItem menuItem) {
        setNormalState();
        itemSelectedHandler.onMenuItemSelected(menuItem);
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
