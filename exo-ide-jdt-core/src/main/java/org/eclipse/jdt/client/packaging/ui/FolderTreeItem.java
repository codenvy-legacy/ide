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

import org.eclipse.jdt.client.packaging.model.JavaProject;
import org.eclipse.jdt.client.packaging.model.SourceDirectory;
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
