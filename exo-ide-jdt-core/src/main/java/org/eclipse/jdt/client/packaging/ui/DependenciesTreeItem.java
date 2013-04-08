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
import org.eclipse.jdt.client.packaging.model.next.Dependency;
import org.eclipse.jdt.client.packaging.model.next.Dependencies;
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
