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
import org.exoplatform.ideall.client.browser.BrowserPanel;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.Workspace;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class MultipleSelectionItemsCommand extends IDECommand implements PanelSelectedHandler
{
   
   protected boolean browserSelected = true;

   public MultipleSelectionItemsCommand(String id)
   {
      super(id);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(PanelSelectedEvent.TYPE, this);
   }

   public boolean isItemsInSameFolderOrNotSelectedWorspace(List<Item> items)
   {
      List<String> paths = new ArrayList<String>();
      for (Item i : items)
      {
         if(i instanceof Workspace)
         {
            return false;
         }
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

   protected void updateEnabling()
   {
      if (browserSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }
   
   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
      updateEnabling();
   }
}
