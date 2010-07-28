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
package org.exoplatform.ideall.client.statusbar;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.component.command.StatusTextControl;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ideall.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ideall.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ideall.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.module.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class NavigatorStatusControl extends StatusTextControl implements ItemsSelectedHandler, EntryPointChangedHandler
{

   public static final String ID = "__navigator_status";

   private String entryPoint;
   
   private Handlers handlers;

   public NavigatorStatusControl(HandlerManager eventBus)
   {
      super(ID);
      setVisible(true);
      setEnabled(true);
      setText("&nbsp;");
      
      handlers = new Handlers(eventBus);
      handlers.addHandler(ItemsSelectedEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (entryPoint == null) {
         setText("&nbsp;");
         return;
      }
      
      String statusMessage = null;

      if (event.getSelectedItems().size() == 1)
      {
         Item item = event.getSelectedItems().get(0);
         
         statusMessage = item.getHref();
         if (item instanceof File)
         {
            statusMessage = statusMessage.substring(0, statusMessage.lastIndexOf("/"));
         }

         String prefix = entryPoint;
         if (prefix.endsWith("/"))
         {
            prefix = prefix.substring(0, prefix.length() - 1);
         }

         prefix = prefix.substring(0, prefix.lastIndexOf("/") + 1);
         statusMessage = statusMessage.substring(prefix.length());
         if (statusMessage.endsWith("/"))
         {
            statusMessage = statusMessage.substring(0, statusMessage.length() - 1);
         }

         if (event.getSelectedItems().get(0).getHref().equals(entryPoint))
         {
            statusMessage = tuneMessage(statusMessage, Images.FileTypes.WORKSPACE);
            //statusMessage = "<img src=\"" + Images.FileTypes.WORKSPACE + "\" style=\"width:16px; height:16px;\">" + statusMessage;
         }
         else
         {
            statusMessage = tuneMessage(statusMessage, Images.FileTypes.FOLDER);
            //statusMessage = "<img src=\"" + Images.FileTypes.FOLDER + "\" style=\"width:16px; height:16px;\">" + statusMessage;
         }

      }
      else if (event.getSelectedItems().size() == 0)
      {
         statusMessage = "No items selected!";
      }
      else
      {
         statusMessage = "Selected: <b>" + event.getSelectedItems().size() + "</b> items";
      }

      setText(statusMessage);
   }

   private String tuneMessage(String originalStatusMessage, String icon)
   {
      String table =
         "<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"height:16px; border-collapse: collapse;\"><tr>"
            + "<td style=\"width:3px;\"><img src=\""
            + Images.BLANK
            + "\" style=\"width:1px; height:1px;\"></td>"
            + "<td style=\"width:16px; height:16px;\">"
            + "<img src=\""
            + icon
            + "\" style=\"width:16px; height:16px;\">"
            + "</td><td "
            + " style=\"border: none; font-family:Verdana,Bitstream Vera Sans,sans-serif; font-size:11px; font-style:normal; \"><nobr>&nbsp;"
            + originalStatusMessage + "</nobr></td></tr></table>";
      return table;
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

}
