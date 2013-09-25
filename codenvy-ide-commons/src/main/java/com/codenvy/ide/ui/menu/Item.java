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
