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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
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

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(java.lang.String) */
    public MenuItem addItem(String title) {
        return addItem(null, title, null);
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(java.lang.String, java.lang.String) */
    public MenuItem addItem(String imageHTML, String title) {
        return addItem(imageHTML, title, null);
    }

    /**
     * @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(java.lang.String,
     *      com.google.gwt.user.client.Command)
     */
    public MenuItem addItem(String title, Command command) {
        return addItem(null, title, command);
    }

    /**
     * @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#addItem(java.lang.String, java.lang.String,
     *      com.google.gwt.user.client.Command)
     */
    public MenuItem addItem(String imageHTML, String title, Command command) {
        PopupMenuItem item = new PopupMenuItem(imageHTML, title, command);
        menuItems.add(item);
        return item;
    }

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setTitle(java.lang.String) */
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

    /** @see org.exoplatform.gwtframework.ui.client.extension.menu.nn.api.MenuItem#setHotKey(java.lang.String) */
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
