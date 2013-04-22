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

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 *          <p/>
 *          Menu item interface which represents buttons on Menu Bar and item in the Popup Menu.
 */

public abstract class MenuItem {

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param title
     *         - title of menu item
     * @return new instance of menu item, which is already added to children list
     */
    public abstract MenuItem addItem(String title);

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param title
     *         - title of menu item
     * @param command
     *         - associated command
     * @return new instance of MenuItem
     */
    public abstract MenuItem addItem(String title, Command command);

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param icon
     *         - icon as HTML image. Image must be prepared like "<img src='....'" />
     * @param title
     *         - title of menu item
     * @return new instance of MenuItem
     */
    public abstract MenuItem addItem(String icon, String title);

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param icon
     *         - icon as HTML image. Image must be prepared like "<img src='....'" />
     * @param title
     *         - title of menu item
     * @param command
     *         - asociated command
     * @return new instance of MenuItem
     */
    public abstract MenuItem addItem(String icon, String title, Command command);

    /**
     * Get command
     *
     * @return command
     */
    public abstract Command getCommand();

    /**
     * Get Hot Key value
     *
     * @return hot key as String
     */
    public abstract String getHotKey();

    /**
     * Get icon.
     *
     * @return - icon
     */
    public abstract String getIcon();

    /**
     * Get list of children.
     *
     * @return list of children
     */
    public abstract List<MenuItem> getItems();

    /**
     * Get menu title
     *
     * @return title of menu item
     */
    public abstract String getTitle();

    /**
     * Get is enabled
     *
     * @return enabled state
     */
    public abstract boolean isEnabled();

    /**
     * Get is selected
     *
     * @return true or false - selected state
     */
    public abstract boolean isSelected();

    /**
     * Get is visible
     *
     * @return - true, false
     */
    public abstract boolean isVisible();

    /**
     * Set command which will be executed when item will be selected.
     *
     * @param command
     *         - command to execute
     */
    public abstract void setCommand(Command command);

    /**
     * Set is enabled
     *
     * @param enabled
     *         - true or false
     */
    public abstract void setEnabled(boolean enabled);

    /**
     * Set Hot Key
     *
     * @param hotKey
     *         - Hot Key value
     */
    public abstract void setHotKey(String hotKey);

    /**
     * Set icon as HTML image. HTML must be like below
     * "<img src='....'" /> or "<img src='....' / style='width:16px; height:16px;'>"
     *
     * @param icon
     */
    public abstract void setIcon(String icon);

    /**
     * Set is selected
     *
     * @param selected
     *         - selected state (true / false)
     */
    public abstract void setSelected(boolean selected);

    /**
     * Set title
     *
     * @param title
     *         - new title of menu item
     */
    public abstract void setTitle(String title);

    /**
     * set is visible
     *
     * @param visible
     *         - true, false
     */
    public abstract void setVisible(boolean visible);

}
