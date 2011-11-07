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
package org.exoplatform.ide.client.operation.rename;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.navigator.NavigatorPresenter;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class RenameItemCommand extends SimpleControl implements IDEControl, ItemsSelectedHandler, ItemDeletedHandler,
   ViewVisibilityChangedHandler, VfsChangedHandler
{

   private static final String ID = "File/Rename...";

   private static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.renameTitleControl();

   private static final String PROMPT = IDE.IDE_LOCALIZATION_CONSTANT.renamePromptControl();

   private boolean browserPanelSelected = true;

   private Item selectedItem;

   private VirtualFileSystemInfo vfsInfo;

   /**
    * 
    */
   public RenameItemCommand()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setDelimiterBefore(true);
      setImages(IDEImageBundle.INSTANCE.rename(), IDEImageBundle.INSTANCE.renameDisabled());
      setEvent(new RenameItemEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(ItemDeletedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
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

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent)
    */
   @Override
   public void onItemDeleted(ItemDeletedEvent event)
   {
      selectedItem = null;
      updateEnabling();
   }

   /**
    * 
    */
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
         return;
      }

      if (selectedItem.getId().equals(vfsInfo.getRoot().getId()))
      {
         setEnabled(false);
      }
      else
      {
         setEnabled(true);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      if (event.getVfsInfo() != null)
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof NavigatorPresenter.Display)
      {
         browserPanelSelected = event.getView().isViewVisible();
         updateEnabling();
      }
   }
}
