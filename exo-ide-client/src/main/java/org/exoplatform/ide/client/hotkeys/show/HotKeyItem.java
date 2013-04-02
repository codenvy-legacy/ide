/*
 * Copyright (C) 2012 eXo Platform SAS.
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
