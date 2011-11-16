/*
 * Copyright (C) 2010 eXo Platform SAS.
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
 */
package org.exoplatform.ide.client.download;

import com.google.gwt.core.client.GWT;

import com.google.gwt.dom.client.Style.Overflow;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.messages.IdeUploadLocalizationConstant;
import org.exoplatform.ide.client.navigation.event.DownloadFileEvent;
import org.exoplatform.ide.client.navigation.event.DownloadFileHandler;
import org.exoplatform.ide.client.navigation.event.DownloadZippedFolderEvent;
import org.exoplatform.ide.client.navigation.event.DownloadZippedFolderHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DownloadForm implements DownloadFileHandler, DownloadZippedFolderHandler, ItemsSelectedHandler
{
   private static final IdeUploadLocalizationConstant lb = GWT.create(IdeUploadLocalizationConstant.class);

   private AbsolutePanel panel;

   private Item selectedItem;

   public DownloadForm()
   {
      IDE.addHandler(DownloadFileEvent.TYPE, this);
      IDE.addHandler(DownloadZippedFolderEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);

      panel = new AbsolutePanel();
      panel.getElement().getStyle().setWidth(1, Unit.PX);
      panel.getElement().getStyle().setHeight(1, Unit.PX);
      panel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
      RootPanel.get().add(panel, -10000, -10000);
   }

   private void downloadResource(String url)
   {
      String iframe =
         "<iframe src=\"" + url
            + "\" frameborder=0 width=\"100%\" height=\"100%\" style=\"overflow:visible;\"></iframe>";
      panel.getElement().setInnerHTML(iframe);
   }

   public void onDownloadFile(DownloadFileEvent event)
   {
      if (selectedItem instanceof FileModel)
      {
         downloadResource(selectedItem.getLinkByRelation(Link.REL_DOWNLOAD_FILE).getHref());
      }
      else
      {
         Dialogs.getInstance().showError(lb.downloadFileError());
      }
   }

   public void onDownloadZippedFolder(DownloadZippedFolderEvent event)
   {
      if (selectedItem instanceof FolderModel || selectedItem instanceof ProjectModel)
      {
         downloadResource(selectedItem.getLinkByRelation(Link.REL_DOWNLOAD_ZIP).getHref());
      }
      else
      {
         Dialogs.getInstance().showError(lb.downloadFolderError());
      }
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

}
