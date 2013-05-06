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
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    
    private Grid gridWidget; 
    
    /**
     * Render tree item.
     */
    protected void render()
    {
        if (gridWidget == null) {
            gridWidget = new Grid(1, 2);
            gridWidget.setWidth("100%");
            
            gridWidget.getCellFormatter().setWidth(0, 0, "16px");
            gridWidget.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
            gridWidget.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
            gridWidget.getCellFormatter().setWidth(0, 1, "100%");
            gridWidget.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
            DOM.setStyleAttribute(gridWidget.getElement(), "display", "block");
            setWidget(gridWidget);            
        }

        TreeIcon treeNodeIcon = new TreeIcon(getItemIcon());
        treeNodeIcon.setHeight("16px");
        gridWidget.setWidget(0, 0, treeNodeIcon);
        
        HTMLPanel l = new HTMLPanel("div", getItemTitle());
        l.setStyleName("ide-Tree-label");
        gridWidget.setWidget(0, 1, l);

//        gridWidget.getCellFormatter().setWidth(0, 0, "16px");
//        gridWidget.getCellFormatter().setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);
//        gridWidget.getCellFormatter().setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
//        gridWidget.getCellFormatter().setWidth(0, 1, "100%");
//        gridWidget.getCellFormatter().addStyleName(0, 1, "ide-Tree-label");
//        DOM.setStyleAttribute(gridWidget.getElement(), "display", "block");
//        setWidget(gridWidget);

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
    protected ImageResource getItemIcon() {
        return ImageUtil.getIcon(((Item)getUserObject()).getMimeType());
    }
    
    /**
     * Get item title.
     * 
     * @return
     */
    protected String getItemTitle() {
        return ((Item)getUserObject()).getName();
    }
    
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
    
    /**
     * Search and select item.
     * 
     * @param item
     * @return
     */
    public abstract boolean select(Item item);
    
    /**
     * Set additional icons.
     * 
     * @param icons
     */
    public void setIcons(Map<String, Map<TreeIconPosition, ImageResource>> icons) {
        Item item = (Item)getUserObject();
        if (item != null) {
            Map<TreeIconPosition, ImageResource> map = icons.get(item.getId());
            if (map != null) {
                Grid grid = (Grid)getWidget();
                TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
                for (TreeIconPosition position : map.keySet()) {
                    treeIcon.addIcon(position, map.get(position));
                }
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            TreeItem child = getChild(i);
            if (!(child instanceof ProjectExplorerTreeItem))
            {
                continue;
            }

            ((ProjectExplorerTreeItem)child).setIcons(icons);
        }
    }

    /**
     * Remove additional icons.
     * 
     * @param icons
     */
    public void removeIcons(Map<String, TreeIconPosition> icons)
    {
        Item item = (Item)getUserObject();
        if (item != null) {
            TreeIconPosition iconPosition = icons.get(item.getId());
            if (iconPosition != null)
            {
                Grid grid = (Grid)getWidget();
                TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
                treeIcon.removeIcon(iconPosition);
            }
        }

        for (int i = 0; i < getChildCount(); i++) {
            TreeItem child = getChild(i);
            if (!(child instanceof ProjectExplorerTreeItem))
            {
                continue;
            }

            ((ProjectExplorerTreeItem)child).removeIcons(icons);
        }
    }
    
    /**
     * Remove items from tree which are points to nonexistent items on file system.
     */
    protected void removeNonexistendTreeItems()
    {
        /*
         * Remove nonexistent
         */
        List<String> idList = new ArrayList<String>();
        List<Item> items = getItems();
        for (Item item : items)
        {
            idList.add(item.getId());
        }

        ArrayList<TreeItem> itemsToRemove = new ArrayList<TreeItem>();
        for (int i = 0; i < getChildCount(); i++)
        {
            TreeItem child = getChild(i);
            if (!(child instanceof ProjectExplorerTreeItem))
            {
                itemsToRemove.add(child);
                continue;
            }

            ProjectExplorerTreeItem childTreeItem = (ProjectExplorerTreeItem)child;
            Item childItem = (Item)childTreeItem.getUserObject();
            if (!idList.contains(childItem.getId()))
            {
                itemsToRemove.add(child);
            }
        }

        for (TreeItem child : itemsToRemove)
        {
            removeItem(child);
        }
    }
    
    
    /**
     * Get child by Item ID.
     * 
     * @param id
     * @return
     */
    public ProjectExplorerTreeItem getChildByItemId(String id)
    {
        for (int i = 0; i < getChildCount(); i++)
        {
            TreeItem child = getChild(i);
            if (child instanceof ProjectExplorerTreeItem)
            {
                ProjectExplorerTreeItem treeItem = (ProjectExplorerTreeItem)child;
                if (((Item)treeItem.getUserObject()).getId().equals(id))
                {
                    return treeItem;
                }
            }
        }

        return null;
    }
    
    
}
