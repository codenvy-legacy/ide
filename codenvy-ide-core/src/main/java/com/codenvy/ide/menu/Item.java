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
package com.codenvy.ide.menu;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;
import com.codenvy.ide.api.ui.menu.Selectable;
import com.codenvy.ide.util.ImageResourceUtils;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;


/**
 * The class is the general type of menu item. It has additional state (selected/unselected).
 * It provides generate item content.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public class Item extends MenuItem implements Selectable {
    public enum ConteinerType {
        MAIN_MENU, TOOLBAR;
    }

    private boolean isSelected;

    private MenuResources resources;

    /**
     * Create Menu Item.
     *
     * @param path
     * @param hotKey
     * @param toolTip
     * @param command
     * @param selected
     * @param resources
     */
    public Item(MenuPath path, String hotKey, String toolTip, ExtendedCommand command, ConteinerType type,
                MenuResources resources) {
        super(getItem(path, hotKey, command.getIcon(), command, type, resources), true, command);

        this.resources = resources;
        addStyleName(resources.menuCSS().itemContainer());
        setTitle(toolTip);
    }

    /**
     * Create Menu Item.
     *
     * @param path
     * @param icon
     * @param toolTip
     * @param subMenu
     * @param resources
     */
    public Item(MenuPath path, ImageResource icon, String toolTip, MenuBar subMenu, ConteinerType type,
                MenuResources resources) {
        super(getItem(path, null, icon, null, type, resources), true, subMenu);

        this.resources = resources;
        addStyleName(resources.menuCSS().itemContainer());
        setTitle(toolTip);
    }

    /**
     * Generates item content.
     *
     * @param path
     * @param hotKey
     * @param command
     * @param resources
     * @return
     */
    private static String getItem(MenuPath path, String hotKey, ImageResource iconResource, ExtendedCommand command,
                                  ConteinerType type, MenuResources resources) {
        int depth = path.getSize() - 1;

            String image = null;
        if (iconResource != null) {
            image = AbstractImagePrototype.create(iconResource).getHTML();
//            icon = new Image(iconResource);
//            icon.addStyleName(resources.menuCSS().itemIcon());
        }

        String title = path.getPathElementAt(depth);
        String itemContent;
        if (type.equals(ConteinerType.MAIN_MENU)) {

            itemContent =
                    (depth != 0 && image != null ? image : "<div class=\"" + resources.menuCSS().itemIcon() + "\"></div>")
                    + "<span class=\"" + resources.menuCSS().itemTitle() + "\">" + title + "</span>"
                    + (depth != 0 && hotKey != null ? "<span class=\"" + resources.menuCSS().hotKey() + "\">" + hotKey
                                                      + "</span>" : "");
        } else {
            itemContent =
                    (image != null ? image : "<div class=\"" + resources.menuCSS().itemIcon() + "\"></div>")
                    + (depth != 1 ? "<span class=\"" + resources.menuCSS().itemTitle() + "\">" + title + "</span>" : "")
                    + (depth != 1 && hotKey != null ? "<span class=\"" + resources.menuCSS().hotKey() + "\">" + hotKey
                                                      + "</span>" : "");
        }

        return itemContent;
    }

    /** {@inheritDoc} */
    @Override
    public boolean isSelected() {
        return isSelected;
    }

    /** {@inheritDoc} */
    @Override
    public void setSelected(boolean isSelected) {
        this.isSelected = isSelected;

        if (isSelected) {
            this.removeStyleName(resources.menuCSS().uncheckedItem());
            this.addStyleName(resources.menuCSS().checkedItem());
        } else {
            this.removeStyleName(resources.menuCSS().checkedItem());
            this.addStyleName(resources.menuCSS().uncheckedItem());
        }
    }
}