/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.common.command;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.model.Workspace;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class MultipleSelectionItemsCommand extends IDECommand
{

   public MultipleSelectionItemsCommand(String id)
   {
      super(id);
   }

   public boolean isSelectedWorkspace(List<Item> items)
   {
      for (Item i : items)
      {
         if (i instanceof Workspace)
         {
            return true;
         }
      }
      return false;
   }

   public boolean isItemsInSameFolder(List<Item> items)
   {
      List<String> paths = new ArrayList<String>();
      for (Item i : items)
      {
         String p = i.getPath();
         p = p.substring(0, p.lastIndexOf("/"));
         paths.add(p);

      }

      for (int i = 0; i < paths.size(); i++)
      {
         String path = paths.get(i);
         for (int j = i + 1; j < paths.size(); j++)
         {
            if (!path.equals(paths.get(j)))
            {
               return false;
            }
         }
      }

      return true;
   }
}
