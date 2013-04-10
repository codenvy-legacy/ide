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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.DataResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * Resources of menu.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface MenuResources extends ClientBundle {
    public interface ToolbarCSS extends CssResource {
        String checkedItem();

        String uncheckedItem();

        String menuHorizontal();

        String menuVertical();

        String itemIcon();

        String itemTitle();

        String hotKey();

        String itemContainer();

        String toolbarHorizontal();
    }

    @Source({"com/codenvy/ide/menu/Menu.css", "com/codenvy/ide/common/constants.css", "com/codenvy/ide/api/ui/style.css"})
    ToolbarCSS menuCSS();

    @Source("toolbar-background.png")
    @ImageResource.ImageOptions(repeatStyle= ImageResource.RepeatStyle.Horizontal)
    ImageResource toolbarBackground();

    @Source("com/codenvy/ide/menu/check.png")
    DataResource checkIcon();
}