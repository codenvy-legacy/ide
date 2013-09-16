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
package org.exoplatform.ide.client.framework.ui;

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.gwtframework.ui.client.component.TreeIcon;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

import java.util.*;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: IdeTree Mar 14, 2011 4:00:06 PM evgen $
 */
public class ProjectTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Item> implements
                                                                                             OpenHandler<Item>, CloseHandler<Item> {

    private Map<String, String> locktokens = new HashMap<String, String>();

    private String id;

    private String prefixId;

    public ProjectTree() {
        sinkEvents(Event.ONCONTEXTMENU);
        addOpenHandler(this);
        addCloseHandler(this);
    }

    /**
     * @param id
     *         of UI component
     * @param prefixId
     *         prefix for child element ID
     */
    public ProjectTree(String id, String prefixId) {
        getElement().setId(id);
        this.prefixId = prefixId;

        sinkEvents(Event.ONCONTEXTMENU);
        addOpenHandler(this);
        addCloseHandler(this);
    }

    @Override
    public void doUpdateValue() {
        throw new RuntimeException("Method setValue(...) not supported");
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
        getElement().setId(id);
    }

    public void setTreeGridId(final String id) {
        this.id = id;
        getElement().setId(id);
    }

    public String getPrefixId() {
        return prefixId;
    }

    public void setPrefixId(String prefixId) {
        this.prefixId = prefixId;
    }

    /** @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event) */
    @Override
    public void onBrowserEvent(Event event) {
        if (Event.ONCONTEXTMENU == DOM.eventGetType(event)) {
            NativeEvent nativeEvent =
                    Document.get().createMouseDownEvent(-1, event.getScreenX(), event.getScreenY(), event.getClientX(),
                                                        event.getClientY(), event.getCtrlKey(), event.getAltKey(), event.getShiftKey(),
                                                        event.getMetaKey(),
                                                        NativeEvent.BUTTON_LEFT);
            DOM.eventGetTarget(event).dispatchEvent(nativeEvent);
        }

        super.onBrowserEvent(event);
    }


    private ProjectModel project;

    private Map<String, ProjectTreeItem> treeItems = new HashMap<String, ProjectTreeItem>();

    public void setProject(ProjectModel project) {
        if (this.project != null && project != null &&
            this.project.getId().equals(project.getId())) {
            return;
        }

        tree.removeItems();
        treeItems.clear();
        this.project = project;

        if (project == null) {
            return;
        }

        ProjectTreeItem projectTreeItem = new ProjectTreeItem(project, prefixId, locktokens);
        tree.addItem(projectTreeItem);
        treeItems.put(project.getId(), projectTreeItem);
        refresh(project, true);
    }

    private void removeChildren(TreeItem treeItem) {
        for (int i = 0; i < treeItem.getChildCount(); i++) {
            TreeItem child = treeItem.getChild(i);
            if (!(child instanceof ProjectTreeItem)) {
                continue;
            }

            if (child.getUserObject() instanceof FolderModel) {
                removeChildren(child);
            }

            String childId = ((Item)child.getUserObject()).getId();
            treeItems.remove(childId);
        }

        treeItem.removeItems();
    }

    @Override
    public void onOpen(OpenEvent<Item> event) {
        FolderModel folder = (FolderModel)event.getTarget();
        refresh(folder, true);
    }

    @Override
    public void onClose(CloseEvent<Item> event) {
        FolderModel folder = (FolderModel)event.getTarget();
        ProjectTreeItem folderTreeItem = treeItems.get(folder.getId());
        removeChildren(folderTreeItem);
        folderTreeItem.render();
    }

    /** Comparator for comparing items in received directory. */
    private Comparator<Item> comparator = new Comparator<Item>() {
        public int compare(Item item1, Item item2) {
            if (item1 instanceof Folder && item2 instanceof FileModel) {
                return -1;
            } else if (item1 instanceof File && item2 instanceof Folder) {
                return 1;
            }
            return item1.getName().compareTo(item2.getName());
        }
    };

    private Item getChild(Folder parent, String name) throws Exception {
        ItemList<Item> itemList = null;

        if (parent instanceof FolderModel) {
            itemList = ((FolderModel)parent).getChildren();
        } else if (parent instanceof ProjectModel) {
            itemList = ((ProjectModel)parent).getChildren();
        }

        if (itemList == null) {
            throw new Exception("Item " + parent.getPath() + " is not a folder");
        }

        for (Item item : itemList.getItems()) {
            if (item.getName().equals(name)) {
                return item;
            }
        }

        throw new Exception("Item " + name + " not found in folder " + parent.getPath());
    }

    public void navigateToItem(Item item) {
        if (project == null) {
            return;
        }

        try {
            Folder parent = project;
            List<Item> items = new ArrayList<Item>();
            items.add(parent);

            String[] parts = item.getPath().split("/");

            for (int i = 2; i < parts.length; i++) {
                Item child = getChild(parent, parts[i]);
                if (child instanceof Folder) {
                    parent = (Folder)child;
                    items.add(child);
                }
            }

            for (Item i : items) {
                refresh(i, true);
            }

            selectItem(item);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Get all selected items
     *
     * @return List of selected items
     */
    public List<Item> getSelectedItems() {
        List<Item> items = new ArrayList<Item>();
        if (tree.getSelectedItem() != null) {
            items.add((Item)tree.getSelectedItem().getUserObject());
            updateHighlighter(tree.getSelectedItem());
        }

        return items;
    }

    public boolean selectItem(Item item) {
        ProjectTreeItem treeItem = treeItems.get(item.getId());
        if (treeItem == null) {
            updateHighlighter(null);
            return false;
        }

        if (treeItem.getParentItem() == null && !item.getId().equals(project.getId())) {
            updateHighlighter(null);
            return false;
        }

        try {
            tree.setSelectedItem(treeItem);
            tree.ensureSelectedItemVisible();
        } catch (Exception e) {
            e.printStackTrace();
        }

        treeItem = treeItems.get(item.getId());
        tree.setSelectedItem(treeItem);

        updateHighlighter(treeItem);
        return true;
    }

    private void updateHighlighter(final TreeItem treeItem) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (treeItem != null) {
                    moveHighlight(treeItem);
                } else {
                    hideHighlighter();
                }
            }
        });
    }

    /**
     * Remove selection of Item
     *
     * @param itemId
     *         Item ID
     */
    public void deselectItem(String itemId) {
        ProjectTreeItem treeItem = treeItems.get(itemId);
        if (treeItem != null) {
            tree.setSelectedItem(treeItem, true);
        }
    }

    /** @param file */
    public void updateFileState(FileModel file) {
        ProjectTreeItem item = treeItems.get(file.getId());
        if (item == null) {
            return;
        }

        item.render();
    }

    /**
     * Set lock token map
     *
     * @param lockTokens
     */
    public void setLockTokens(Map<String, String> lockTokens) {
        this.locktokens.clear();

        if (locktokens != null) {
            this.locktokens.putAll(lockTokens);
        }
    }

    /**
     * Add info icons to Item main icon
     *
     * @param itemsIcons
     *         Map of Item, info icon position and info icon URL
     */
    public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons) {
        for (Item item : itemsIcons.keySet()) {
            TreeItem node = treeItems.get(item.getId());
            if (node == null) {
                continue;
            }
            Grid grid = (Grid)node.getWidget();
            TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
            Map<TreeIconPosition, ImageResource> map = itemsIcons.get(item);
            for (TreeIconPosition position : map.keySet()) {
                treeIcon.addIcon(position, map.get(position));
            }
        }
    }

    /**
     * Remove info icon from Item main icon
     *
     * @param itemsIcons
     *         Map of item and position of info icon
     */
    public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons) {
        for (Item item : itemsIcons.keySet()) {
            TreeItem node = treeItems.get(item.getId());
            if (node == null) {
                continue;
            }

            Grid grid = (Grid)node.getWidget();
            TreeIcon treeIcon = (TreeIcon)grid.getWidget(0, 0);
            treeIcon.removeIcon(itemsIcons.get(item));
        }
    }

    public void refresh() {
        if (project == null) {
            return;
        }

        refresh(project, false);

        if (tree.getSelectedItem() != null) {
            moveHighlight(tree.getSelectedItem());
        } else {
            hideHighlighter();
        }
    }

    private void refresh(Item item, boolean forceOpen) {
        ProjectTreeItem treeItem = treeItems.get(item.getId());
        if (treeItem == null) {
            return;
        }

        treeItem.setItem(item);

        if (!(item instanceof FolderModel)) {
            return;
        }

        if (!treeItem.getState() && !forceOpen) {
            return;
        }

        Collections.sort(((FolderModel)item).getChildren().getItems(), comparator);
        List<Item> filteredItems = DirectoryFilter.get().filter(((FolderModel)item).getChildren().getItems());

        List<String> idList = new ArrayList<String>();
        for (Item i : filteredItems) {
            idList.add(i.getId());
        }

        // remove not existed items
        List<TreeItem> itemsToRemove = new ArrayList<TreeItem>();
        for (int i = 0; i < treeItem.getChildCount(); i++) {
            TreeItem childItem = treeItem.getChild(i);
            Item child = (Item)childItem.getUserObject();
            if (child == null) {
                itemsToRemove.add(childItem);
                continue;
            }

            if (!idList.contains(child.getId())) {
                itemsToRemove.add(childItem);
            }
        }

        for (TreeItem itemToRemove : itemsToRemove) {
            if (itemToRemove.getUserObject() != null) {
                Item i = (Item)itemToRemove.getUserObject();
                treeItems.remove(i.getId());
            }

            treeItem.removeItem(itemToRemove);
        }

        // add necessary items
        int index = 0;
        for (Item itemToAdd : filteredItems) {
            ProjectTreeItem child = treeItem.getChildByItemId(itemToAdd.getId());
            if (child != null) {
                refresh(itemToAdd, false);
            } else {
                child = new ProjectTreeItem(itemToAdd, prefixId, locktokens);
                treeItems.put(itemToAdd.getId(), child);
                treeItem.insertItem(index, child);
            }
            index++;
        }

        if (forceOpen) {
            treeItem.setState(true, false);
        }
    }

    public List<Item> getVisibleItems() {
        List<Item> visibleItems = new ArrayList<Item>();
        if (project != null) {
            ProjectTreeItem projectItem = treeItems.get(project.getId());
            visibleItems.add((Item)projectItem.getUserObject());
            visibleItems.addAll(getVisibleItems(projectItem));
        }

        return visibleItems;
    }

    private List<Item> getVisibleItems(ProjectTreeItem treeItem) {
        List<Item> visibleItems = new ArrayList<Item>();
        if (treeItem.getState()) {
            for (int i = 0; i < treeItem.getChildCount(); i++) {
                TreeItem child = treeItem.getChild(i);
                if (!(child instanceof ProjectTreeItem)) {
                    continue;
                }

                Item item = (Item)child.getUserObject();
                if (!(item instanceof FileModel || item instanceof FolderModel)) {
                    continue;
                }

                visibleItems.add(item);

                if (item instanceof FolderModel && child.getState()) {
                    visibleItems.addAll(getVisibleItems((ProjectTreeItem)child));
                }
            }
        }

        return visibleItems;
    }

}
