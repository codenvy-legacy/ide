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

import org.eclipse.jdt.client.JdtClientBundle;
import org.eclipse.jdt.client.packaging.model.Package;
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class PackageTreeItem extends PackageExplorerTreeItem {

    public PackageTreeItem(Package p) {
        super(p);
    }

    @Override
    protected ImageResource getItemIcon() {
        for (FileModel file : ((Package)getUserObject()).getFiles()) {
            if (!DirectoryFilter.get().matchWithPattern(file.getName())) {
                return JdtClientBundle.INSTANCE.packageFolder();
            }
        }

        return JdtClientBundle.INSTANCE.packageEmptyFolder();
    }

    @Override
    protected String getItemTitle() {
        return ((Package)getUserObject()).getPackageName();
    }

    @Override
    public List<Item> getItems() {
        List<Item> items = new ArrayList<Item>();

        List<FileModel> files = ((Package)getUserObject()).getFiles();
        for (FileModel file : files) {
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

        Package p = (Package)getUserObject();
        Collections.sort(p.getFiles(), COMPARATOR);

        int index = 0;
        for (FileModel file : p.getFiles()) {
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

}
