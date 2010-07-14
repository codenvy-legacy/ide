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

import org.exoplatform.ideall.client.browser.BrowserPanel;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;
import org.exoplatform.ideall.vfs.api.Folder;
import org.exoplatform.ideall.vfs.api.Item;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public abstract class MultipleSelectionItemsCommand extends IDEControl implements PanelSelectedHandler
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

   public boolean isItemsInSameFolder(List<Item> items)
   {
      List<String> hrefs = new ArrayList<String>();
      for (Item i : items)
      {
         if (i.getHref().equals(context.getEntryPoint()))
         {
            return false;
         }
         String p = i.getHref();
         p = p.substring(0, p.lastIndexOf("/"));
         // folders href ends with "/"
         if (i instanceof Folder)
         {
            p = p.substring(0, p.lastIndexOf("/"));
         }
         hrefs.add(p);

      }

      for (int i = 0; i < hrefs.size(); i++)
      {
         String path = hrefs.get(i);
         for (int j = i + 1; j < hrefs.size(); j++)
         {
            if (!path.equals(hrefs.get(j)))
            {
               return false;
            }
         }
      }

      return true;
   }

   protected abstract void updateEnabling();

   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
      updateEnabling();
   }
}
