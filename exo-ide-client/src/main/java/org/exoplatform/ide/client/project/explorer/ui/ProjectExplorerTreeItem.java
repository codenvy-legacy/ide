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
package org.exoplatform.ide.client.project.explorer.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public abstract class ProjectExplorerTreeItem extends TreeItem {
    
    private static final String PREFIX_ID = "navigation-";

    public ProjectExplorerTreeItem(Item item) {
        setUserObject(item);
        render();        
    }
    
    /**
     * Render tree item.
     */
    protected void render()
    {
        Grid grid = new Grid(1, 2);
        grid.setWidth("100%");

        TreeIcon treeNodeIcon = new TreeIcon(getItemIcon());
        // treeNodeIcon.setWidth("16px");
        treeNodeIcon.setHeight("16px");
        grid.setWidget(0, 0, treeNodeIcon);
        // Label l = new Label(text, false);
        HTMLPanel l = new HTMLPanel("div", getItemTitle());
        l.setStyleName("ide-Tree-label");
        grid.setWidget(0, 1, l);

        grid.getCellFormatter().setWidth(0, 0, "16px");
        grid.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
        grid.getCellFormatter().setWidth(0, 1, "100%");
        grid.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
        DOM.setStyleAttribute(grid.getElement(), "display", "block");
        setWidget(grid);

        Item item = (Item)getUserObject();
        getElement().setId(PREFIX_ID + Utils.md5(item.getPath()));

        List<Item> items = getItems();
        if (!getState() && items != null && !items.isEmpty())
        {
            if (getChildCount() == 0)
            {
                addItem("");
            }
        }
        else if (!getState() && items != null && items.isEmpty())
        {
            removeItems();
        }
    }

    /**
     * Get item icon.
     * 
     * @return
     */
    protected abstract ImageResource getItemIcon();

    /**
     * Get item title.
     * 
     * @return
     */
    protected abstract String getItemTitle();
    
    /**
     * Get item children.
     * 
     * @return
     */
    public abstract List<Item> getItems();
    
    /**
     * Refresh tree item.
     * 
     * @param expand
     */
    public abstract void refresh(boolean expand);
    
}
