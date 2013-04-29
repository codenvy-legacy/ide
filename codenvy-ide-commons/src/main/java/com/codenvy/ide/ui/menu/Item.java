/*
 * Copyright (C) 2013 eXo Platform SAS.
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
 */
package com.codenvy.ide.ui.menu;

import com.codenvy.ide.json.JsonArray;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Command;

/**
 * Base interface for menu and toolbar items
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public interface Item {
    /**
     * Get command
     *
     * @return command
     */
    Command getCommand();

    /**
     * Get Hot Key value
     *
     * @return hot key as String
     */
    String getHotKey();

    /**
     * Get image.
     *
     * @return - image
     */
    ImageResource getImage();

    /**
     * Get map of children.
     *
     * @return list of children
     */
    JsonArray<Item> getItems();

    /**
     * Get menu title
     *
     * @return title of menu item
     */
    String getTitle();

    /**
     * Get is enabled
     *
     * @return enabled state
     */
    boolean isEnabled();

    /**
     * Get is selected
     *
     * @return true or false - selected state
     */
    boolean isSelected();

    /**
     * Get is visible
     *
     * @return - true, false
     */
    boolean isVisible();

    /**
     * Set is enabled
     *
     * @param enabled
     *         - true or false
     */
    void setEnabled(boolean enabled);

    /**
     * Set is selected
     *
     * @param selected
     *         - selected state (true / false)
     */
    void setSelected(boolean selected);

    /**
     * set is visible
     *
     * @param visible
     *         - true, false
     */
    void setVisible(boolean visible);
}
