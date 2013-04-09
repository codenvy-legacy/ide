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
package org.eclipse.jdt.client.packaging.ui;

import com.google.gwt.resources.client.ImageResource;

import org.eclipse.jdt.client.packaging.model.next.JavaProject;
import org.eclipse.jdt.client.packaging.model.next.SourceDirectory;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.util.ImageUtil;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class FolderTreeItem extends PackageExplorerTreeItem {

    public FolderTreeItem(FolderModel folder) {
        super(folder);
    }

    @Override
    protected ImageResource getItemIcon() {
        return ImageUtil.getIcon(((FolderModel)getUserObject()).getMimeType());
    }

    @Override
    protected String getItemTitle() {
        return ((FolderModel)getUserObject()).getName();
    }

    @Override
    public List<Item> getItems() {
        List<Item> folderItems = new ArrayList<Item>();
        FolderModel folder = (FolderModel)getUserObject();
        for (Item item : folder.getChildren().getItems()) {
            if (DirectoryFilter.get().matchWithPattern(item.getName())) {
                continue;
            }

            if (item instanceof FolderModel && isSource(item, folder.getProject())) {
                continue;
            }

            folderItems.add(item);
        }
        return folderItems;
    }

    private boolean isSource(Item item, ProjectModel proj) {
        if (!(proj instanceof JavaProject)) {
            return false;
        }

        JavaProject javaProject = (JavaProject)proj;
        for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories()) {
            if (item.getPath().startsWith(sourceDirectory.getPath())) {
                return true;
            }
        }

        return false;
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
        FolderModel folder = (FolderModel)getUserObject();

        List<Item> items = getItems();
        Collections.sort(items, COMPARATOR);
        int index = 0;

        for (Item item : items) {
            if (item instanceof FolderModel && isSource(item, folder.getProject())) {
                continue;
            }

            PackageExplorerTreeItem child = getChildByItemId(item.getId());
            if (child == null) {
                if (item instanceof FolderModel) {
                    child = new FolderTreeItem((FolderModel)item);
                } else {
                    child = new FileTreeItem((FileModel)item);
                }

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
