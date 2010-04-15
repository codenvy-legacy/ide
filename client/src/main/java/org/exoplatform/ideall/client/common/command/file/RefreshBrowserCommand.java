/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.client.common.command.file;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.browser.BrowserPanel;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class RefreshBrowserCommand extends IDECommand implements ItemsSelectedHandler, PanelSelectedHandler
{

   private static final String ID = "File/Refresh Selected Folder";

   private static final String TITLE = "Refresh";

   private static final String PROMPT = "Refresh Selected Folder";

   private boolean browserPanelSelected = true;

   public RefreshBrowserCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setIcon(Images.MainMenu.REFRESH);
      setEvent(new RefreshBrowserEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(ItemsSelectedEvent.TYPE, this);
      addHandler(PanelSelectedEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (browserPanelSelected)
      {
         setEnabled(true);
      }
      else
      {
         setEnabled(false);
      }
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         browserPanelSelected = false;
         updateEnabling();
      }
      else
      {
         browserPanelSelected = true;
         updateEnabling();
      }
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserPanelSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
      updateEnabling();
   }

}
