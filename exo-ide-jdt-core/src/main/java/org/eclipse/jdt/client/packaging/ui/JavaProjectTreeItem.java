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

import org.eclipse.jdt.client.packaging.model.Dependencies;
import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.eclipse.jdt.client.packaging.model.SourceDirectory;
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
        List<Dependencies> dependencies = getDependencies();
        for (Dependencies classpathFolder : dependencies)
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
    
    private List<Dependencies> getDependencies() {
        List<Dependencies> dependencies = new ArrayList<Dependencies>();
        
        JavaProject project = (JavaProject)getUserObject();
        for (Dependencies dep : project.getClasspathFolders()) {
            if (dep.getClasspathList() != null && !dep.getClasspathList().isEmpty()) {
                dependencies.add(dep);
            }
        }
        
        return dependencies;
    }

    @Override
    public List<Item> getItems() {
        JavaProject project = (JavaProject)getUserObject();

        ArrayList<Item> items = new ArrayList<Item>();

        items.addAll(project.getModules());
        items.addAll(project.getSourceDirectories());
        items.addAll(getDependencies());

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
