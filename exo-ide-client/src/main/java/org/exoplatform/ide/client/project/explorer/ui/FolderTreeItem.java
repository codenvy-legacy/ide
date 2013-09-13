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
package org.exoplatform.ide.client.project.explorer.ui;

import com.google.gwt.user.client.ui.TreeItem;

import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class FolderTreeItem extends ProjectExplorerTreeItem {

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

    public FolderTreeItem(FolderModel folder) {
        super(folder);
    }

    @Override
    public List<Item> getItems() {
        List<Item> folderItems = new ArrayList<Item>();
        FolderModel folder = (FolderModel)getUserObject();
        for (Item item : folder.getChildren().getItems()) {
            if (DirectoryFilter.get().matchWithPattern(item.getName())) {
                continue;
            }

            folderItems.add(item);
        }
        return folderItems;
    }


    protected ProjectExplorerTreeItem createTreeItem(Item item) {
        if (item instanceof FolderModel) {
            return new FolderTreeItem((FolderModel)item);
        }

        return new FileTreeItem((FileModel)item);
    }


    @Override
    public void refresh(boolean expand) {
        render();

        /*
         * Does not refresh children if tree item closed
         */
        if (!getState() && !expand) {
            return;
        }

        /*
         * Remove nonexistent
         */
        removeNonexistendTreeItems();

        /*
         * Add missing
         */
        List<Item> items = getItems();
        Collections.sort(items, COMPARATOR);
        int index = 0;

        for (Item item : items) {
            ProjectExplorerTreeItem child = getChildByItemId(item.getId());
            if (child == null) {
                child = createTreeItem(item);
                insertItem(index, child);
            } else {
                child.setUserObject(item);
                child.refresh(false);
            }

            index++;
        }

        if (expand) {
            setState(true);
        }
    }

    @Override
    public boolean select(Item item) {
        if (item.getId().equals(((Item)getUserObject()).getId()))
        {
            getTree().setSelectedItem(this);
            getTree().ensureSelectedItemVisible();
            return true;
        }

        String folderPath = ((Item)getUserObject()).getPath();
        if (!item.getPath().startsWith(folderPath)) {
            return false;
        }
        
        refresh(true);

        for (int i = 0; i < getChildCount(); i++)
        {
            TreeItem child = getChild(i);
            if (child instanceof ProjectExplorerTreeItem)
            {
                String path = ((Item)child.getUserObject()).getPath();
                if (path == null || path.isEmpty()) {
                    continue;
                }

                if (item.getPath().startsWith(path))
                {
                    ((ProjectExplorerTreeItem)child).refresh(true);
                    return ((ProjectExplorerTreeItem)child).select(item);
                }
            }
        }

        return false;
    }

}
