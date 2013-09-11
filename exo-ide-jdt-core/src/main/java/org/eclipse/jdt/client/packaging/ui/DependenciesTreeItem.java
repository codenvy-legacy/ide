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
import org.eclipse.jdt.client.packaging.model.Dependencies;
import org.eclipse.jdt.client.packaging.model.Dependency;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class DependenciesTreeItem extends PackageExplorerTreeItem
{

    public DependenciesTreeItem(Dependencies classpathFolder)
    {
        super(classpathFolder);
    }

    @Override
    protected ImageResource getItemIcon()
    {
        return JdtClientBundle.INSTANCE.jarReferences();
    }

    @Override
    protected String getItemTitle()
    {
        return ((Dependencies)getUserObject()).getName();
    }

    @Override
    public List<Item> getItems()
    {
        Dependencies classpathFolder = ((Dependencies)getUserObject());
        List<Item> classPathItems = new ArrayList<Item>();
        classPathItems.addAll(classpathFolder.getClasspathList());
        return classPathItems;
    }

    @Override
    public void refresh(boolean expand)
    {
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

        Dependencies classpathFolder = ((Dependencies)getUserObject());
        int index = 0;

        /*
         * Classpath items
         */
        for (Dependency classpath : classpathFolder.getClasspathList())
        {
            PackageExplorerTreeItem child = getChildByItemId(classpath.getId());
            if (child == null)
            {
                child = new DependencyTreeItem(classpath);
                insertItem(index, child);
            }
            else
            {
                child.setUserObject(classpath);
                child.refresh(false);
            }

            index++;
        }

        if (expand)
        {
            setState(true);
        }
    }

}
