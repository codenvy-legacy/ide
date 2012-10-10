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
package org.exoplatform.ide.client.project.packaging;

import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 * 
 */
public class PackageExplorerLogger
{

   public static void dump(Item item)
   {
      System.out.println("-----------------------------------------------------------");
      dumpTree(" >   ", item);
      System.out.println("-----------------------------------------------------------");
   }

   private static void dumpTree(String prefix, Item item)
   {
      System.out.println(prefix + item.getName());

      ItemList<Item> items;
      if (item instanceof FolderModel)
      {
         items = ((FolderModel)item).getChildren();
      }
      else if (item instanceof ProjectModel)
      {
         items = ((ProjectModel)item).getChildren();
      }
      else
      {
         return;
      }

      for (Item i : items.getItems())
      {
         dumpTree(prefix + "    ", i);
      }
   }

}
