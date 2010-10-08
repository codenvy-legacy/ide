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
package org.exoplatform.ide.client.download;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.application.ApplicationConfiguration;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.model.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.module.navigation.event.download.DownloadFileEvent;
import org.exoplatform.ide.client.module.navigation.event.download.DownloadFileHandler;
import org.exoplatform.ide.client.module.navigation.event.download.DownloadZippedFolderEvent;
import org.exoplatform.ide.client.module.navigation.event.download.DownloadZippedFolderHandler;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.RootPanel;
import com.smartgwt.client.widgets.HTMLPane;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DownloadForm implements DownloadFileHandler, DownloadZippedFolderHandler,
   ItemsSelectedHandler, ConfigurationReceivedSuccessfullyHandler
{

   private final String CONTEXT_DOWNLOAD = "/services/downloadcontent";

   private Handlers handlers;

   private HTMLPane htmlPane;

   private Item selectedItem;

   private ApplicationConfiguration applicationConfiguration;

   public DownloadForm(HandlerManager eventBus)
   {
      handlers = new Handlers(eventBus);
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      handlers.addHandler(DownloadFileEvent.TYPE, this);
      handlers.addHandler(DownloadZippedFolderEvent.TYPE, this);
      handlers.addHandler(ItemsSelectedEvent.TYPE, this);

      htmlPane = new HTMLPane();
      htmlPane.setWidth(1);
      htmlPane.setHeight(1);
      RootPanel.get().add(htmlPane, -100, -100);
   }

   private void downloadResource()
   {
      //Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
      String fileName = selectedItem.getHref();

      if (fileName.endsWith("/"))
      {
         fileName = fileName.substring(0, fileName.length() - 1);
      }
      fileName = fileName.substring(fileName.lastIndexOf("/") + 1);

      if (!(selectedItem instanceof File))
      {
         fileName += ".zip";
      }

      String path = selectedItem.getHref();
      String url = applicationConfiguration.getContext() + CONTEXT_DOWNLOAD + "/" + fileName + "?repoPath=" + path;
      String iframe =
         "<iframe src=\"" + url
            + "\" frameborder=0 width=\"100%\" height=\"100%\" style=\"overflow:visible;\"></iframe>";
      htmlPane.setContents(iframe);
   }

   public void onDownloadFile(DownloadFileEvent event)
   {
      downloadResource();
   }

   public void onDownloadZippedFolder(DownloadZippedFolderEvent event)
   {
      downloadResource();
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 0)
      {
         selectedItem = null;
      }
      else
      {
         selectedItem = event.getSelectedItems().get(0);
      }
   }

   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      applicationConfiguration = event.getConfiguration();
   }

}
