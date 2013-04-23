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
import com.codenvy.ide.json.JsonStringMap;
import com.google.gwt.resources.client.ImageResource;
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

public interface MenuItem {

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param title
     *         - title of menu item
     * @return new instance of menu item, which is already added to children list
     */
    public MenuItem addItem(String title);

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param title
     *         - title of menu item
     * @param command
     *         - associated command
     * @return new instance of MenuItem
     */
    public MenuItem addItem(String title, Command command);

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param image
     *         - image as ImageResource
     * @param title
     *         - title of menu item
     * @return new instance of MenuItem
     */
    public MenuItem addItem(ImageResource image, String title);

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param image
     *         - image as ImageResource
     * @param title
     *         - title of menu item
     * @param command
     *         - associated command
     * @return new instance of MenuItem
     */
    public MenuItem addItem(ImageResource image, String title, Command command);

    /**
     * Add new MenuItem
     * @param item
     *         - item the new menu item
     */
    public void addItem(MenuItem item);

    /**
     * Get command
     *
     * @return command
     */
    public Command getCommand();

    /**
     * Get Hot Key value
     *
     * @return hot key as String
     */
    public String getHotKey();

    /**
     * Get image.
     *
     * @return - image
     */
    public ImageResource getImage();

    /**
     * Get map of children.
     *
     * @return list of children
     */
    public JsonArray<MenuItem> getItems();

    /**
     * Get children of this menu item, by title.
     * If this item not contains children with this title, return null.
     * @param title the children title.
     * @return  the children menu item.
     */
    public MenuItem getChildren(String title);

    /**
     * Get menu title
     *
     * @return title of menu item
     */
    public String getTitle();

    /**
     * Get is enabled
     *
     * @return enabled state
     */
    public boolean isEnabled();

    /**
     * Get is selected
     *
     * @return true or false - selected state
     */
    public boolean isSelected();

    /**
     * Get is visible
     *
     * @return - true, false
     */
    public boolean isVisible();

    /**
     * Set command which will be executed when item will be selected.
     *
     * @param command
     *         - command to execute
     */
    public void setCommand(Command command);

    /**
     * Set is enabled
     *
     * @param enabled
     *         - true or false
     */
    public void setEnabled(boolean enabled);

    /**
     * Set Hot Key
     *
     * @param hotKey
     *         - Hot Key value
     */
    public void setHotKey(String hotKey);

    /**
     * Set is selected
     *
     * @param selected
     *         - selected state (true / false)
     */
    public void setSelected(boolean selected);

    /**
     * Set title
     *
     * @param title
     *         - new title of menu item
     */
    public void setTitle(String title);

    /**
     * set is visible
     *
     * @param visible
     *         - true, false
     */
    public void setVisible(boolean visible);

}
