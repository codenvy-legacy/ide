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
package org.exoplatform.ide.client.module.navigation.control;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.module.vfs.api.Folder;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public abstract class MultipleSelectionItemsCommand extends IDEControl implements PanelSelectedHandler, EntryPointChangedHandler
{

   protected boolean browserSelected = true;
   
   private String entryPoint;

   public MultipleSelectionItemsCommand(String id, HandlerManager eventBus)
   {
      super(id, eventBus);
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(PanelSelectedEvent.TYPE, this);
      addHandler(EntryPointChangedEvent.TYPE, this);
   }

   public boolean isItemsInSameFolder(List<Item> items)
   {
      List<String> hrefs = new ArrayList<String>();
      for (Item i : items)
      {
         if (i.getHref().equals(entryPoint))
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

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      this.entryPoint = event.getEntryPoint();
   }
   
}
