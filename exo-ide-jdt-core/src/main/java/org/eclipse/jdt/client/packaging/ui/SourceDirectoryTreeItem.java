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

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.packaging.model.next.Package;
import org.eclipse.jdt.client.packaging.model.next.SourceDirectory;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class SourceDirectoryTreeItem extends PackageExplorerTreeItem {

    public SourceDirectoryTreeItem(SourceDirectory sourceDirectory) {
        super(sourceDirectory);
    }

    @Override
    protected ImageResource getItemIcon() {
        return JdtClientBundle.INSTANCE.resourceDirectory();
    }

    @Override
    protected String getItemTitle() {
        return ((SourceDirectory)getUserObject()).getSourceDirectoryName();
    }

    @Override
    public List<Item> getItems() {
        SourceDirectory sourceDirectory = (SourceDirectory)getUserObject();
        List<Item> items = new ArrayList<Item>();

        for (Package p : sourceDirectory.getPackages()) {
            if (!p.getPackageName().isEmpty()) {
                if (isNeedShowPackage(p)) {
                    items.add(p);
                }
            }
        }

        for (FileModel file : sourceDirectory.getDefaultPackage().getFiles()) {
            if (!DirectoryFilter.get().matchWithPattern(file.getName())) {
                items.add(file);
            }
        }

        return items;
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
        SourceDirectory sourceDirectory = (SourceDirectory)getUserObject();
        int index = 0;

      /*
       * Packages
       */
        Package defaultPackage = null;
        for (Package p : sourceDirectory.getPackages()) {
            if (p.getPackageName().isEmpty()) {
                defaultPackage = p;
                continue;
            }

            if (!isNeedShowPackage(p)) {
                continue;
            }

            PackageExplorerTreeItem child = getChildByItemId(p.getId());
            if (child == null) {
                child = new PackageTreeItem(p);
                insertItem(index, child);
            } else {
                child.setUserObject(p);
                child.refresh(false);
            }

            index++;
        }

      /*
       * Files
       */
        Collections.sort(defaultPackage.getFiles(), COMPARATOR);
        for (FileModel file : defaultPackage.getFiles()) {
            if (DirectoryFilter.get().matchWithPattern(file.getName())) {
                continue;
            }

            PackageExplorerTreeItem child = getChildByItemId(file.getId());
            if (child == null) {
                child = new FileTreeItem(file);
                insertItem(index, child);
            } else {
                child.setUserObject(file);
                child.refresh(false);
            }
            index++;
        }

        if (expand) {
            setState(true);
        }
    }

    private boolean isNeedShowPackage(Package p) {
        boolean hasFiles = false;
        boolean hasFolders = false;

        for (Item item : p.getChildren().getItems()) {
            if (item instanceof FolderModel) {
                hasFolders = true;
            } else if (item instanceof FileModel) {
                hasFiles = true;
            }
        }

        if (hasFolders == true && hasFiles == false) {
            return false;
        }

        return true;
    }

}
