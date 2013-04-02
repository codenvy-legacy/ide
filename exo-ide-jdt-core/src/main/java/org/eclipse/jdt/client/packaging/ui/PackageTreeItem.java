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
import org.exoplatform.ide.client.framework.navigation.DirectoryFilter;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PackageTreeItem extends PackageExplorerTreeItem
{

   public PackageTreeItem(Package p)
   {
      super(p);
   }

   @Override
   protected ImageResource getItemIcon()
   {
      for (FileModel file : ((Package)getUserObject()).getFiles())
      {
         if (!DirectoryFilter.get().matchWithPattern(file.getName()))
         {
            return JdtClientBundle.INSTANCE.packageFolder();
         }
      }

      return JdtClientBundle.INSTANCE.packageEmptyFolder();
   }

   @Override
   protected String getItemTitle()
   {
      return ((Package)getUserObject()).getPackageName();
   }

   @Override
   public List<Item> getItems()
   {
      List<Item> items = new ArrayList<Item>();

      List<FileModel> files = ((Package)getUserObject()).getFiles();
      for (FileModel file : files)
      {
         if (!DirectoryFilter.get().matchWithPattern(file.getName()))
         {
            items.add(file);
         }
      }

      return items;
   }

   @Override
   public void refresh(boolean expand)
   {
      render();

      /*
       * Does not refresh children if tree item closed
       */
      if (!getState() && !expand)
      {
         return;
      }

      /*
       * Remove nonexistent
       */
      removeNonexistendTreeItems();

      Package p = (Package)getUserObject();
      Collections.sort(p.getFiles(), COMPARATOR);

      int index = 0;
      for (FileModel file : p.getFiles())
      {
         if (DirectoryFilter.get().matchWithPattern(file.getName()))
         {
            continue;
         }

         PackageExplorerTreeItem child = getChildByItemId(file.getId());
         if (child == null)
         {
            child = new FileTreeItem(file);
            insertItem(index, child);
         }
         else
         {
            child.setUserObject(file);
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
