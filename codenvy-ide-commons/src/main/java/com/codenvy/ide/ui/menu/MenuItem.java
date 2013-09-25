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

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 *          <p/>
 *          Menu item interface which represents buttons on Menu Bar and item in the Popup Menu.
 */

public interface MenuItem extends Item {

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
    public Item addItem(String title, Command command);

    /**
     * Create new MenuItem with specified parameters and add it to the list of children.
     *
     * @param image
     *         - image as ImageResource
     * @param title
     *         - title of menu item
     * @return new instance of MenuItem
     */
    public Item addItem(ImageResource image, String title);

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
     *
     * @param item
     *         - item the new menu item
     */
    public void addItem(Item item);

    /**
     * Get children of this menu item, by title.
     * If this item not contains children with this title, return null.
     *
     * @param title
     *         the children title.
     * @return the children menu item.
     */
    public MenuItem getChildren(String title);

    /**
     * Set command which will be executed when item will be selected.
     *
     * @param command
     *         - command to execute
     */
    public void setCommand(Command command);

    /**
     * Set Hot Key
     *
     * @param hotKey
     *         - Hot Key value
     */
    public void setHotKey(String hotKey);

    /**
     * Set title
     *
     * @param title
     *         - new title of menu item
     */
    public void setTitle(String title);

}
