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
import org.exoplatform.ideall.client.application.component.SimpleCommand;
import org.exoplatform.ideall.client.browser.event.BrowserPanelDeselectedEvent;
import org.exoplatform.ideall.client.browser.event.BrowserPanelDeselectedHandler;
import org.exoplatform.ideall.client.browser.event.BrowserPanelSelectedEvent;
import org.exoplatform.ideall.client.browser.event.BrowserPanelSelectedHandler;
import org.exoplatform.ideall.client.event.file.GetFileURLEvent;
import org.exoplatform.ideall.client.event.file.ItemSelectedEvent;
import org.exoplatform.ideall.client.event.file.ItemSelectedHandler;
import org.exoplatform.ideall.client.model.Item;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GetFileURLCommand extends SimpleCommand implements ItemSelectedHandler, BrowserPanelSelectedHandler,
   BrowserPanelDeselectedHandler
{

   private static final String ID = "File/Get File URL";

   private static final String TITLE = "Get File URL";

   private boolean browserPanelSelected = true;

   private Item selectedItem;

   public GetFileURLCommand()
   {
      super(ID, TITLE, Images.MainMenu.GET_URL, new GetFileURLEvent());
   }

   @Override
   protected void onRegisterHandlers()
   {
      addHandler(ItemSelectedEvent.TYPE, this);
      addHandler(BrowserPanelSelectedEvent.TYPE, this);
      addHandler(BrowserPanelDeselectedEvent.TYPE, this);
   }

   @Override
   protected void onInitializeApplication()
   {
      setVisible(true);
      setEnabled(false);
   }

   public void onItemSelected(ItemSelectedEvent event)
   {
      selectedItem = event.getSelectedItem();
      updateEnabling();
   }

   private void updateEnabling()
   {
      if (!browserPanelSelected)
      {
         setEnabled(false);
         return;
      }

      if (selectedItem == null)
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }

   }

   public void onBrowserPanelSelected(BrowserPanelSelectedEvent event)
   {
      browserPanelSelected = true;
      updateEnabling();
   }

   public void onBrowserPanelDeselected(BrowserPanelDeselectedEvent event)
   {
      browserPanelSelected = false;
      updateEnabling();
   }

}
