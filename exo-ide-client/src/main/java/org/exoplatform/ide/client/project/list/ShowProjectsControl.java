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

package org.exoplatform.ide.client.project.list;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.project.explorer.TinyProjectExplorerView;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ShowProjectsControl extends SimpleControl implements IDEControl, VfsChangedHandler, ViewActivatedHandler,
   ItemsSelectedHandler
{

   public static final String ID = "Project/Open...";

   private static final String TITLE = "Open...";

   private static final String PROMPT = "Open Project...";

   private VirtualFileSystemInfo vfsInfo;

   private boolean projectExplorerSelected = false;

   /**
    * 
    */
   public ShowProjectsControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(PROMPT);
      setImages(IDEImageBundle.INSTANCE.projectOpened(), IDEImageBundle.INSTANCE.projectOpenedDisabled());
      setEvent(new ShowProjectsEvent());
      // setDelimiterBefore(true);
      setGroupName(GroupNames.ACTIONS);
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      update();
   }

   /**
    * 
    */
   private void update()
   {
      if (vfsInfo == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }
      setEnabled(true);
      setVisible(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
      update();
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {

      if (projectExplorerSelected && !event.getSelectedItems().isEmpty())
      {
         setShowInContextMenu(event.getSelectedItems().get(0) instanceof ProjectModel);
      }
      else
      {
         setShowInContextMenu(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler#onViewActivated(org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent)
    */
   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      projectExplorerSelected = event.getView().getId().equals(TinyProjectExplorerView.ID);
   }
}
