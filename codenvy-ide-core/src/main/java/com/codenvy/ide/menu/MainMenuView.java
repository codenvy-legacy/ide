/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.menu;

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;


/** Main Menu View */
public interface MainMenuView extends View<MainMenuView.ActionDelegate> {
    /** Needs for delegate some function into MainMenu view. */
    public interface ActionDelegate {
    }

    /**
     * Set Menu Item by given path visible or invisible.
     *
     * @param path
     *         menuItem path
     * @param visible
     *         state
     */
    void setVisible(String path, boolean visible);

    /**
     * Set Menu Item by given path enabled or disabled.
     *
     * @param path
     *         menuItem path
     * @param enabled
     */
    void setEnabled(String path, boolean enabled);

    /**
     * Sets Menu Item by given path selected or unselected.
     *
     * @param path
     * @param enabled
     */
    void setSelected(String path, boolean enabled);

    /**
     * Add menu item with the following path, icon, command, visible and enabled states
     *
     * @param path
     * @param icon
     * @param command
     * @param visible
     * @param enabled
     */
    void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled);

    /**
     * Adds toggle menu item with the following path, command with expression, visible, enabled
     * and selected states.
     *
     * @param path
     * @param command
     * @param visible
     * @param enabled
     * @param selected
     */
    void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled, boolean selected);
}