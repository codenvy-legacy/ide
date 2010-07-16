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
package org.exoplatform.ideall.client.module.navigation.control;

import org.exoplatform.ideall.client.IDEImageBundle;
import org.exoplatform.ideall.client.browser.BrowserPanel;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.module.navigation.event.GetFileURLEvent;
import org.exoplatform.ideall.client.module.vfs.api.Item;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class GetFileURLControl extends IDEControl implements ItemsSelectedHandler, PanelSelectedHandler
{

   private static final String ID = "View/Get URL...";

   private static final String TITLE = "Get URL...";

   private static final String PROMPT = "Get URL";

   private boolean browserPanelSelected = true;

   private Item selectedItem;

   public GetFileURLControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.url(), IDEImageBundle.INSTANCE.urlDisabled());
      setEvent(new GetFileURLEvent());
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
      setEnabled(false);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() != 1)
      {
         setEnabled(false);
         return;
      }
      selectedItem = event.getSelectedItems().get(0);
      updateEnabling();
   }

   private void updateEnabling()
   {
//      if (!browserPanelSelected)
//      {
//         setEnabled(false);
//         return;
//      }

      if (selectedItem == null)
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      browserPanelSelected = BrowserPanel.ID.equals(event.getPanelId()) ? true : false;
      updateEnabling();
   }

}
