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

import org.eclipse.jdt.client.packaging.model.next.Dependencies;
import org.eclipse.jdt.client.packaging.model.next.JavaProject;
import org.eclipse.jdt.client.packaging.model.next.SourceDirectory;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
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
public class JavaProjectTreeItem extends PackageExplorerTreeItem {

    public JavaProjectTreeItem(JavaProject item) {
        super(item);
    }

    @Override
    protected ImageResource getItemIcon() {
        return ProjectResolver.getImageForProject(((JavaProject)getUserObject()).getProjectType());
    }

    @Override
    protected String getItemTitle() {
        return ((Item)getUserObject()).getName();
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
        JavaProject javaProject = (JavaProject)getUserObject();
        int index = 0;
        /*
         * Modules
         */
        for (ProjectModel module : javaProject.getModules()) {
            PackageExplorerTreeItem child = getChildByItemId(module.getId());
            if (child == null) {
                child = new JavaProjectTreeItem((JavaProject)module);
                insertItem(index, child);
            } else {
                child.setUserObject(module);
                child.refresh(false);
            }

            index++;
        }

        /*
         * Source Directories
         */
        for (SourceDirectory sourceDirectory : javaProject.getSourceDirectories()) {
            PackageExplorerTreeItem child = getChildByItemId(sourceDirectory.getId());
            if (child == null) {
                child = new SourceDirectoryTreeItem(sourceDirectory);
                insertItem(index, child);
            } else {
                child.setUserObject(sourceDirectory);
                child.refresh(false);
            }

            index++;
        }

        /*
         * Dependencies
         */
        for (Dependencies classpathFolder : javaProject.getClasspathFolders())
        {
            PackageExplorerTreeItem child = getChildByItemId(classpathFolder.getId());
            if (child == null) {
                child = new DependenciesTreeItem(classpathFolder);
                insertItem(index, child);
            } else {
                child.setUserObject(classpathFolder);
                child.refresh(false);
            }

            index++;
        }

        /*
         * Folders and files
         */

        Collections.sort(javaProject.getChildren().getItems(), COMPARATOR);

        for (Item item : javaProject.getChildren().getItems()) {
            if (DirectoryFilter.get().matchWithPattern(item.getName())) {
                continue;
            }
            
            if (item instanceof JavaProject) {
                continue;
            }

            if (item instanceof FolderModel && isSource(item, javaProject)) {
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
    public List<Item> getItems() {
        JavaProject project = (JavaProject)getUserObject();

        ArrayList<Item> items = new ArrayList<Item>();

        items.addAll(project.getModules());
        items.addAll(project.getSourceDirectories());
        items.addAll(project.getClasspathFolders());

        for (Item item : project.getChildren().getItems()) {
            if (DirectoryFilter.get().matchWithPattern(item.getName())) {
                continue;
            }

            if (item instanceof FileModel) {
                items.add(item);
            } else if (item instanceof FolderModel) {
                if (isSource(item, project)) {
                    continue;
                }

                items.add(item);
            }
        }

        return items;
    }
    
}
