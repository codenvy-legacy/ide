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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.TreeItem;

import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class PackageExplorerItemTree extends org.exoplatform.gwtframework.ui.client.component.Tree<Item> implements
                                                                                                        OpenHandler<TreeItem> {

    private String id;

    private JavaProject project;

    public PackageExplorerItemTree() {
        sinkEvents(Event.ONCONTEXTMENU);
        tree.addOpenHandler(this);
    }

    @Override
    public void doUpdateValue() {
        System.out.println("Does not use setValue() anymore !!!");
    }

    public void setTreeGridId(String id) {
        this.id = id;
        getElement().setId(id);
    }

    public String getId() {
        return id;
    }

    /** @see com.google.gwt.user.client.ui.Composite#onBrowserEvent(com.google.gwt.user.client.Event) */
    @Override
    public void onBrowserEvent(Event event) {
        if (Event.ONCONTEXTMENU == DOM.eventGetType(event)) {
            NativeEvent nativeEvent = Document.get().createMouseDownEvent(
                    -1, event.getScreenX(), event.getScreenY(), event.getClientX(),
                    event.getClientY(), event.getCtrlKey(), event.getAltKey(),
                    event.getShiftKey(), event.getMetaKey(), NativeEvent.BUTTON_LEFT);
            DOM.eventGetTarget(event).dispatchEvent(nativeEvent);
        }
        super.onBrowserEvent(event);
    }

    @Override
    public void onOpen(OpenEvent<TreeItem> event) {
        PackageExplorerTreeItem treeItem = (PackageExplorerTreeItem)event.getTarget();
        treeItem.refresh(false);
    }

    public Item getSelectedItem() {
        if (tree.getSelectedItem() == null) {
            return null;
        }

        return (Item)tree.getSelectedItem().getUserObject();
    }

    public boolean selectItem(Item item) {
        if (tree.getItemCount() == 0) {
            return false;
        }

        return ((JavaProjectTreeItem)tree.getItem(0)).select(item);
    }

    private void updateHighlighter() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            @Override
            public void execute() {
                if (tree.getSelectedItem() != null) {
                    moveHighlight(tree.getSelectedItem());
                } else {
                    hideHighlighter();
                }
            }
        });
    }

    public void setProject(JavaProject project) {
        if (this.project != null && project != null && this.project.getId().equals(project.getId())) {
            return;
        }
        tree.removeItems();

        this.project = project;
        if (project == null) {
            return;
        }

        JavaProjectTreeItem treeItem = new JavaProjectTreeItem(project);
        tree.addItem(treeItem);
        treeItem.setState(true);
        updateHighlighter();
    }

    public void refresh() {
        try
        {
            if (tree.getItemCount() == 1) {
                PackageExplorerTreeItem packageExplorerTreeItem = (JavaProjectTreeItem)tree.getItem(0);

                packageExplorerTreeItem.refresh(true);
                updateHighlighter();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Add info icons to Item main icon
     * 
     * @param itemsIcons Map of Item, info icon position and info icon URL
     */
    public void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons) {
        if (tree.getItemCount() == 0)
        {
            return;
        }

        Map<String, Map<TreeIconPosition, ImageResource>> icons = new HashMap<String, Map<TreeIconPosition, ImageResource>>();
        for (Item item : itemsIcons.keySet())
        {
            Map<TreeIconPosition, ImageResource> iconMap = itemsIcons.get(item);
            icons.put(item.getId(), iconMap);
        }

        ((PackageExplorerTreeItem)tree.getItem(0)).setIcons(icons);
    }

    /**
     * Remove info icon from Item main icon
     * 
     * @param itemsIcons Map of item and position of info icon
     */
    public void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons) {
        if (tree.getItemCount() == 0)
        {
            return;
        }

        Map<String, TreeIconPosition> icons = new HashMap<String, TreeIconPosition>();
        for (Item item : itemsIcons.keySet())
        {
            TreeIconPosition iconPosition = itemsIcons.get(item);
            icons.put(item.getId(), iconPosition);
        }

        ((PackageExplorerTreeItem)tree.getItem(0)).removeIcons(icons);
    }

    public List<Item> getTreeChildren(FolderModel folder) {
        if (tree.getItemCount() == 0) {
            return new ArrayList<Item>();
        }

        return getTreeChildren((PackageExplorerTreeItem)tree.getItem(0), folder);
    }

    public List<Item> getTreeChildren(PackageExplorerTreeItem treeItem, FolderModel folder) {
        if (!(treeItem instanceof PackageExplorerTreeItem)) {
            return new ArrayList<Item>();
        }

        Item item = (Item)treeItem.getUserObject();
        if (!(item instanceof FolderModel)) {
            return new ArrayList<Item>();
        }

        if (item.getId().equals(folder.getId())) {
            List<Item> children = new ArrayList<Item>();

            for (int i = 0; i < treeItem.getChildCount(); i++) {
                TreeItem child = treeItem.getChild(i);
                if (!(child instanceof PackageExplorerTreeItem)) {
                    continue;
                }

                Item childItem = (Item)child.getUserObject();
                if (childItem instanceof FolderModel || childItem instanceof FileModel) {
                    children.add(childItem);
                }
            }

            return children;
        }

        for (int i = 0; i < treeItem.getChildCount(); i++) {
            TreeItem child = treeItem.getChild(i);
            if (!(child instanceof PackageExplorerTreeItem)) {
                continue;
            }

            Item childItem = (Item)child.getUserObject();
            if (folder.getPath().startsWith(childItem.getPath())) {
                return getTreeChildren((PackageExplorerTreeItem)child, folder);
            }
        }

        return new ArrayList<Item>();
    }

    public List<Item> getVisibleItems() {
        List<Item> visibleItems = new ArrayList<Item>();
        if (project != null && tree.getItemCount() > 0) {
            PackageExplorerTreeItem projectItem = (PackageExplorerTreeItem)tree.getItem(0);
            visibleItems.add((Item)projectItem.getUserObject());
            visibleItems.addAll(getVisibleItems(projectItem));
        }

        return visibleItems;
    }

    private List<Item> getVisibleItems(PackageExplorerTreeItem treeItem) {
        List<Item> visibleItems = new ArrayList<Item>();
        if (treeItem.getState()) {
            for (int i = 0; i < treeItem.getChildCount(); i++) {
                TreeItem child = treeItem.getChild(i);
                if (!(child instanceof PackageExplorerTreeItem)) {
                    continue;
                }

                Item item = (Item)child.getUserObject();
                if (!(item instanceof FileModel || item instanceof FolderModel)) {
                    continue;
                }

                visibleItems.add(item);

                if (item instanceof FolderModel && child.getState()) {
                    visibleItems.addAll(getVisibleItems((PackageExplorerTreeItem)child));
                }
            }
        }

        return visibleItems;
    }

}
