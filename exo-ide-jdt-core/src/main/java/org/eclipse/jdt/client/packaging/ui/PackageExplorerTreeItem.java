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
package org.eclipse.jdt.client.packaging.ui;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public abstract class PackageExplorerTreeItem extends TreeItem {

    private static final String PREFIX_ID = "ide.package_explorer.item.";

    public PackageExplorerTreeItem(Item item) {
        setUserObject(item);
        render();
    }

    /**
     * Render tree item.
     */
    protected void render() {
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
        if (!getState() && items != null && !items.isEmpty()) {
            if (getChildCount() == 0) {
                addItem("");
            }
        }
        else if (!getState() && items != null && items.isEmpty()) {
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
     * Get child by Item ID.
     * 
     * @param id
     * @return
     */
    public PackageExplorerTreeItem getChildByItemId(String id) {
        for (int i = 0; i < getChildCount(); i++) {
            TreeItem child = getChild(i);
            if (child instanceof PackageExplorerTreeItem) {
                PackageExplorerTreeItem treeItem = (PackageExplorerTreeItem)child;
                if (((Item)treeItem.getUserObject()).getId().equals(id)) {
                    return treeItem;
                }
            }
        }

        return null;
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
    public boolean select(Item item) {
        if (item.getId().equals(((Item)getUserObject()).getId())) {
            getTree().setSelectedItem(this);
            getTree().ensureSelectedItemVisible();
            return true;
        }

        String packageExplorerItemPath = ((Item)getUserObject()).getPath();
        if (packageExplorerItemPath == null || 
            packageExplorerItemPath.isEmpty() ||
            !item.getPath().startsWith(packageExplorerItemPath)) {
            return false;
        }

        refresh(true);

        for (int i = 0; i < getChildCount(); i++) {
            TreeItem child = getChild(i);
            if (child instanceof PackageExplorerTreeItem) {
                String path = ((Item)child.getUserObject()).getPath();
                if (path == null || path.isEmpty()) {
                    continue;
                }

                if (item.getPath().startsWith(path)) {
                    ((PackageExplorerTreeItem)child).refresh(true);
                    return ((PackageExplorerTreeItem)child).select(item);
                }
            }
        }

        return false;
    }

    /**
     * Remove items from tree which are points to nonexistent items on file system.
     */
    protected void removeNonexistendTreeItems() {
        /*
         * Remove nonexistent
         */
        List<String> idList = new ArrayList<String>();
        List<Item> items = getItems();
        for (Item item : items) {
            idList.add(item.getId());
        }

        ArrayList<TreeItem> itemsToRemove = new ArrayList<TreeItem>();
        for (int i = 0; i < getChildCount(); i++) {
            TreeItem child = getChild(i);
            if (!(child instanceof PackageExplorerTreeItem)) {
                itemsToRemove.add(child);
                continue;
            }

            PackageExplorerTreeItem childTreeItem = (PackageExplorerTreeItem)child;
            Item childItem = (Item)childTreeItem.getUserObject();
            if (!idList.contains(childItem.getId())) {
                itemsToRemove.add(child);
            }
        }

        for (TreeItem child : itemsToRemove) {
            removeItem(child);
        }
    }

    /**
     * Comparator for comparing items in received directory.
     */
    protected Comparator<Item> COMPARATOR = new Comparator<Item>() {
          public int compare(Item item1, Item item2) {
              if (item1 instanceof FolderModel && item2 instanceof FileModel) {
                  return -1;
              }
              else if (item1 instanceof FileModel && item2 instanceof FolderModel) {
                  return 1;
              }
              return item1.getName().compareTo(item2.getName());
          }
      };

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
            if (!(child instanceof PackageExplorerTreeItem))
            {
                continue;
            }

            ((PackageExplorerTreeItem)child).setIcons(icons);
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
            if (!(child instanceof PackageExplorerTreeItem))
            {
                continue;
            }

            ((PackageExplorerTreeItem)child).removeIcons(icons);
        }
    }

    // public void insertItem(int beforeIndex, TreeItem item)
    // throws IndexOutOfBoundsException {
    // if (beforeIndex > getChildCount())
    // {
    // System.out.println("!!! ERR");
    // System.out.println("tree item > " + getItemTitle());
    // System.out.println("beforeIndex > " + beforeIndex);
    // System.out.println("child count > " + getChildCount());
    //
    // beforeIndex = getChildCount();
    // }
    //
    // super.insertItem(beforeIndex, item);
    // }

}
