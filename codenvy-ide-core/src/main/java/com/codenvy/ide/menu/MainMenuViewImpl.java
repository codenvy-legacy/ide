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

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.ui.menu.MenuBar;
import com.codenvy.ide.ui.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;


/**
 * Implements {@link MainMenuView} using standard GWT Menu Widgets
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class MainMenuViewImpl extends Composite implements MainMenuView {
    private static MainMenuUiBinder uiBinder = GWT.create(MainMenuUiBinder.class);
    /** Map storing Path and corresponding menu item */
    private final JsonStringMap<MenuItem> menuItems;
    private final Resources               resources;
    /** Parent menu bar */
    @UiField
    MenuBar parentMenuBar;

    /** Create new {@link MainMenuViewImpl} */
    @Inject
    public MainMenuViewImpl(Resources resources) {
        initWidget(uiBinder.createAndBindUi(this));
//        parentMenuBar.addStyleName(resources.menuCSS().menuHorizontal());
        this.menuItems = JsonCollections.createStringMap();
        this.resources = resources;
    }

    /** {@inheritDoc} */
    @Override
    public void setVisible(String path, boolean visible) {
        if (menuItems.containsKey(path)) {
            menuItems.get(path).setVisible(visible);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setEnabled(String path, boolean enabled) {
        if (menuItems.containsKey(path)) {
            menuItems.get(path).setEnabled(enabled);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void setSelected(String path, boolean selected) {
        if (menuItems.containsKey(path)) {
            menuItems.get(path).setSelected(selected);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled) {
        addMainMenuItem(path, command, visible, enabled);
    }

    /**
     * Create Main Menu item.
     *
     * @param path
     * @param command
     * @param visible
     * @param enabled
     * @return
     */
    private MenuItem addMainMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled) {
        MenuPath menuPath = new MenuPath(path);
        if (menuPath.getSize() == 1) {
            MenuItem item = parentMenuBar.addItem(menuPath.getPathElementAt(0));
            menuItems.put(path, item);
            return item;
        }
        MenuItem item = parentMenuBar.getItems().get(menuPath.getPathElementAt(0));
        if (item == null) {
            item = parentMenuBar.addItem(menuPath.getPathElementAt(0));
        }
        // Recursively get destination menu bar
        MenuItem dstMenuBar = getOrCreateParentMenuBar(menuPath, menuPath.getSize() - 1, item);
        // create new item
        MenuItem newItem = dstMenuBar.addItem(command.getIcon(), menuPath.getPathElementAt(menuPath.getSize() - 1), command);
        dstMenuBar.addItem(newItem);

        newItem.setVisible(visible);
        newItem.setEnabled(enabled);
        // store item in the map
        menuItems.put(path, newItem);

        return newItem;
    }

    /**
     * Recursively find corresponding menu bar or create new if nothing found
     *
     * @param menuPath
     * @param depth
     * @param item
     * @return
     */
    private MenuItem getOrCreateParentMenuBar(MenuPath menuPath, int depth, MenuItem item) {
        int i = 1;
        while (i < depth) {
            MenuItem children = item.getChildren(menuPath.getPathElementAt(i));
            if (children == null) {
                children = item.addItem(menuPath.getPathElementAt(i));
            }
            i++;
            item = children;
        }
        return item;
    }

    /** {@inheritDoc} */
    @Override
    public void addMenuItem(String path, ExtendedCommand command, boolean visible, boolean enabled, boolean selected) {
        MenuItem newItem = addMainMenuItem(path, command, visible, enabled);
        newItem.setSelected(selected);
    }

    /** {@inheritDoc} */
    @Override
    public void setDelegate(ActionDelegate delegate) {
        // ok
        // there are no events for now
    }

    interface MainMenuUiBinder extends UiBinder<Widget, MainMenuViewImpl> {
    }
}