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

import com.google.gwt.user.client.Command;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
public class PopupMenuItem extends MenuItem {

    /** Icon as HTML Image */
    private String icon;

    /** Title of this item */
    private String title;

    /** Visibility state */
    private boolean visible = true;

    /** Selected state. If selected is true, special check image will be displayed near item's icon */
    private boolean selected = false;

    /** Hot Key is associated with this item */
    private String hotKey;

    /** Enabled state. True as default. */
    private boolean enabled = true;

    /** List of children. */
    private List<MenuItem> menuItems = new ArrayList<MenuItem>();

    /** Command which will be executed when this item will be selected. */
    private Command command;

    private UpdateItemEnablingCallback updateItemEnablingCallback;

    private Map<String, String> attributes = new HashMap<String, String>();

    /**
     * Create PopupMenuItem
     *
     * @param title
     *         - title
     */
    public PopupMenuItem(String title) {
        this.title = title;
    }

    /**
     * Create PopupMenuItem
     *
     * @param icon
     *         - icon as HTML image for new item. Image must be prepared like "<img ... />" tag
     * @param title
     *         - title
     */
    public PopupMenuItem(String icon, String title) {
        this.icon = icon;
        this.title = title;
    }

    /**
     * @param icon
     *         - icon as HTML image for new item. Image must be prepared like "<img ... />" tag
     * @param title
     *         - title
     * @param command
     *         - command which will be executed when item will be selected
     */
    public PopupMenuItem(String icon, String title, Command command) {
        this.icon = icon;
        this.title = title;
        this.command = command;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(String) */
    public MenuItem addItem(String title) {
        return addItem(null, title, null);
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(String, String) */
    public MenuItem addItem(String imageHTML, String title) {
        return addItem(imageHTML, title, null);
    }

    /**
     * @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(String,
     *      com.google.gwt.user.client.Command)
     */
    public MenuItem addItem(String title, Command command) {
        return addItem(null, title, command);
    }

    /**
     * @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(String, String,
     *      com.google.gwt.user.client.Command)
     */
    public MenuItem addItem(String imageHTML, String title, Command command) {
        PopupMenuItem item = new PopupMenuItem(imageHTML, title, command);
        menuItems.add(item);
        return item;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setTitle(String) */
    public void setTitle(String title) {
        this.title = title;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getTitle() */
    public String getTitle() {
        return title;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon() {
        return icon;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setVisible(boolean) */
    public void setVisible(boolean visible) {
        this.visible = visible;
        if (updateItemEnablingCallback != null) {
            updateItemEnablingCallback.onUpdateItemEnabling();
        }
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#isVisible() */
    public boolean isVisible() {
        return visible;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setSelected(boolean) */
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#isSelected() */
    public boolean isSelected() {
        return selected;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setEnabled(boolean) */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#isEnabled() */
    public boolean isEnabled() {
        return enabled;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getHotKey() */
    public String getHotKey() {
        return hotKey;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setHotKey(String) */
    public void setHotKey(String hotKey) {
        this.hotKey = hotKey;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getItems() */
    public List<MenuItem> getItems() {
        return menuItems;
    }

    /** Use for dump */
    @Override
    public String toString() {
        String value =
                "Title: " + title + "\r\n" + "Visible: " + visible + "\r\n" + "Enabled: " + enabled + "\r\n" + "HotKey: "
                + hotKey;
        return value;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setCommand(com.google.gwt.user.client.Command) */
    public void setCommand(Command command) {
        this.command = command;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#getCommand() */
    public Command getCommand() {
        return command;
    }

    /** @param updateItemEnablingCallback */
    public void setUpdateItemEnablingCallback(UpdateItemEnablingCallback updateItemEnablingCallback) {
        this.updateItemEnablingCallback = updateItemEnablingCallback;
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }
}
