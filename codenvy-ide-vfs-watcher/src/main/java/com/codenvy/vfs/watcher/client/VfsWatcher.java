/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.vfs.watcher.client;

import com.codenvy.vfs.dto.ItemCreatedDto;
import com.codenvy.vfs.dto.ItemDeletedDto;
import com.codenvy.vfs.dto.ItemMovedDto;
import com.codenvy.vfs.dto.ItemRenamedDto;
import com.codenvy.vfs.dto.RoutingTypes;
import com.codenvy.vfs.dto.client.DtoClientImpls.ProjectClosedDtoImpl;
import com.codenvy.vfs.dto.client.DtoClientImpls.ProjectOpenedDtoImpl;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.communication.MessageFilter;
import org.exoplatform.ide.communication.MessageFilter.MessageRecipient;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsWatcher implements ProjectOpenedHandler, ProjectClosedHandler
{

   private VfsApi vfsApi;

   public VfsWatcher(MessageFilter messageFilter, HandlerManager handlerManager, VfsApi vfsApi)
   {
      this.vfsApi = vfsApi;
      handlerManager.addHandler(ProjectClosedEvent.TYPE, this);
      handlerManager.addHandler(ProjectOpenedEvent.TYPE, this);
      messageFilter.registerMessageRecipient(RoutingTypes.ITEM_MOVED, new MessageRecipient<ItemMovedDto>()
      {
         @Override
         public void onMessageReceived(ItemMovedDto message)
         {
            Window.alert("Moved" + message.toString());
         }
      });

      messageFilter.registerMessageRecipient(RoutingTypes.ITEM_RENAMED, new MessageRecipient<ItemRenamedDto>()
      {
         @Override
         public void onMessageReceived(ItemRenamedDto message)
         {
            Window.alert("Renamed" + message.toString());
         }
      });

      messageFilter.registerMessageRecipient(RoutingTypes.ITEM_CREATED, new MessageRecipient<ItemCreatedDto>()
      {
         @Override
         public void onMessageReceived(ItemCreatedDto message)
         {
            Window.alert("Created" + message.toString());
         }
      });

      messageFilter.registerMessageRecipient(RoutingTypes.ITEM_DELETED, new MessageRecipient<ItemDeletedDto>()
      {
         @Override
         public void onMessageReceived(ItemDeletedDto message)
         {
            Window.alert("Deleted" + message.toString());
         }
      });
   }

   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      ProjectClosedDtoImpl dto = ProjectClosedDtoImpl.make();
      dto.setProjectPath(event.getProject().getPath());
      dto.setVfsId(VirtualFileSystem.getInstance().getInfo().getId());
      dto.setProjectId(event.getProject().getId());
      vfsApi.PROJECT_CLOSED.send(dto);
   }

   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      ProjectOpenedDtoImpl dto = ProjectOpenedDtoImpl.make();
      dto.setProjectPath(event.getProject().getPath());
      dto.setVfsId(VirtualFileSystem.getInstance().getInfo().getId());
      dto.setProjectId(event.getProject().getId());
      vfsApi.PROJECT_OPEN.send(dto);
   }
}
