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

}
