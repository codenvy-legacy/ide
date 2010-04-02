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
package org.exoplatform.ideall.client.common.command.file;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.browser.BrowserPanel;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.ItemsSelectedHandler;
import org.exoplatform.ideall.client.event.file.UploadFileEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;



/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenInEditorCommand extends IDECommand implements ItemsSelectedHandler, PanelSelectedHandler
{
   
   private final static String ID = "File/Open from local system";

   private final static String TITLE = "Open from local system";

   private boolean browserPanelSelected = true;
   
   public OpenInEditorCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setIcon(Images.MainMenu.UPLOAD);
      setEvent(new UploadFileEvent(true));
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

