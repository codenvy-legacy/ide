/*
 * Copyright (C) 2011 eXo Platform SAS.
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
import com.google.gwt.user.client.ui.Frame;
import com.google.gwt.user.client.ui.RootPanel;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.messages.IdeUploadLocalizationConstant;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Link;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class DownloadHandler implements ItemsSelectedHandler, DownloadItemHandler
{

   private static final IdeUploadLocalizationConstant UPLOAD_LOCALIZATION_CONSTANT = GWT
      .create(IdeUploadLocalizationConstant.class);

   private AbsolutePanel downloadPanel;

   private List<Item> selectedItems = new ArrayList<Item>();

   public DownloadHandler()
   {
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(DownloadItemEvent.TYPE, this);

      IDE.getInstance().addControl(new DownloadItemControl(false));
      IDE.getInstance().addControl(new DownloadItemControl(true));
   }

   private void downloadResource(String url)
   {
      if (downloadPanel == null)
      {
         downloadPanel = new AbsolutePanel();
         downloadPanel.getElement().getStyle().setWidth(1, Unit.PX);
         downloadPanel.getElement().getStyle().setHeight(1, Unit.PX);
         downloadPanel.getElement().getStyle().setOverflow(Overflow.HIDDEN);
         RootPanel.get().add(downloadPanel, -10000, -10000);
      }

      Frame frame = new Frame(url);
      frame.setHeight("100%");
      frame.setWidth("100%");
      downloadPanel.add(frame);
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   @Override
   public void onDownloadItem(DownloadItemEvent event)
   {
      if (selectedItems.size() != 1)
      {
         Dialogs.getInstance().showError("Only one file must be selected.");
         return;
      }

      Item item = selectedItems.get(0);

      if (item instanceof FileModel)
      {
         downloadResource(item.getLinkByRelation(Link.REL_DOWNLOAD_FILE).getHref());
      }
      else if (item instanceof FolderModel)
      {
         downloadResource(item.getLinkByRelation(Link.REL_DOWNLOAD_ZIP).getHref());
      }
      else if (item instanceof ProjectModel)
      {
         downloadResource(item.getLinkByRelation(Link.REL_DOWNLOAD_ZIP).getHref());
      }
      else
      {
         Dialogs.getInstance().showError(UPLOAD_LOCALIZATION_CONSTANT.downloadFileError());
      }
   }

}
