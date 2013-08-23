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
package org.exoplatform.ide.client.hotkeys.show;

import org.exoplatform.gwtframework.ui.client.command.Control;

/**
 * Item for displaying in hot keys list grid.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: HotKeyItem.java May 10, 2012 2:20:50 PM azatsarynnyy $
 */
public class HotKeyItem {

    /** Items hot key. */
    private String hotKey;

    /** The control. */
    private Control command;

    /** Field for group item. */
    private boolean isGroup = false;

    /** Item's title. */
    private String title;

    /** Item's group name. */
    private String group;

    /**
     * @param command
     *         the control
     * @param hotkey
     *         the hot key
     * @param group
     *         item's group
     */
    public HotKeyItem(Control command, String hotkey, String group) {
        this.command = command;
        this.hotKey = hotkey;
        this.group = group;
    }

    /**
     * @param title
     *         item's title
     * @param hotkey
     *         the hot key
     * @param isGroup
     *         for group item
     * @param group
     *         item's group name
     */
    public HotKeyItem(String title, String hotkey, boolean isGroup, String group) {
        this.title = title;
        this.hotKey = hotkey;
        this.isGroup = isGroup;
        this.group = group;
    }

    /**
     * Returns item's group.
     *
     * @return the group
     */
    public String getGroup() {
        return group;
    }

    /**
     * Returns true if item is group.
     *
     * @return true if item is group
     */
    public boolean isGroup() {
        return isGroup;
    }

    /**
     * Returns shortcut title.
     *
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the hot key.
     *
     * @return the hot key
     */
    public String getHotKey() {
        return hotKey;
    }

    /**
     * Returns the control.
     *
     * @return the control
     */
    public Control getCommand() {
        return command;
    }

}
