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
package com.codenvy.vfs.watcher.server;

import com.codenvy.vfs.dto.ProjectClosedDto;
import com.codenvy.vfs.dto.ProjectOpenedDto;
import com.codenvy.vfs.dto.server.DtoServerImpls.ItemDeletedDtoImpl;
import com.google.gson.internal.Pair;

import org.everrest.websockets.WSConnectionContext;
import org.everrest.websockets.message.ChannelBroadcastMessage;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class VfsWatcher
{

   private class EventListenerImpl implements EventListener
   {
      private final String vfsId;

      private final String projectPath;

      private EventListenerImpl(String vfsId, String projectPath)
      {
         this.vfsId = vfsId;
         this.projectPath = projectPath;
      }

      @Override
      public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
      {
         notifyUsers(event);
      }

      private void notifyUsers(ChangeEvent event)
      {
         String message = null;
         switch (event.getType())
         {
            case CREATED:
               break;
            case DELETED:
               ItemDeletedDtoImpl dto = ItemDeletedDtoImpl.make();
               dto.setFileId(event.getItemId());
               dto.setFileId(event.getItemPath());
               message = dto.toJson();
               break;
            case MOVED:
               break;
            case RENAMED:
               break;
            default:
               return;
         }
         Set<String> userIds = projectUsers.get(vfsId + projectPath);
         if (message != null && userIds != null)
         {
            broadcastToClients(message, userIds);
         }
      }
   }

   private static final Log LOG = ExoLogger.getLogger(VfsWatcher.class);

   /**
    * Map of per project listener.
    */
   private final ConcurrentMap<String, Pair<ChangeEventFilter, EventListener>> vfsListeners = new ConcurrentHashMap<String, Pair<ChangeEventFilter, EventListener>>();

   private final ConcurrentMap<String, Set<String>> projectUsers = new ConcurrentHashMap<String, Set<String>>();

   private VirtualFileSystemRegistry vfsRegistry;

   private EventListenerList listeners;

   public VfsWatcher(VirtualFileSystemRegistry vfsRegistry, EventListenerList listeners)
   {
      this.vfsRegistry = vfsRegistry;

      this.listeners = listeners;
   }

   public void openProject(String clientId, ProjectOpenedDto dto)
   {
      // for now project id is unique, but in case fs vfs, only combination of vfsId and projectPath is unique :(
      String key = dto.vfsId() + dto.projectPath();
      if (!projectUsers.containsKey(key))
      {
         projectUsers.put(key, new CopyOnWriteArraySet<String>());
         addListenerToProject(key, dto.vfsId(), dto.projectPath());
      }
      projectUsers.get(key).add(clientId);
   }

   private void addListenerToProject(String key, String vfsId, String projectPath)
   {
      LOG.debug("Add VFS listener for {} project", projectPath);
      EventListenerImpl eventListener = new EventListenerImpl(vfsId, projectPath);

      ChangeEventFilter filter = ChangeEventFilter.createAndFilter(new VfsIDFilter(vfsId),
         new PathFilter(projectPath + "*"), ChangeEventFilter.createOrFilter( // created, deleted, renamed or moved
         new TypeFilter(ChangeType.CREATED),//
         new TypeFilter(ChangeType.DELETED),//
         new TypeFilter(ChangeType.RENAMED),//
         new TypeFilter(ChangeType.MOVED)));

      listeners.addEventListener(filter, eventListener);
      Pair<ChangeEventFilter, EventListener> pair = new Pair<ChangeEventFilter, EventListener>(filter, eventListener);
      vfsListeners.putIfAbsent(key, pair);

   }

   public void closeProject(String clientId, ProjectClosedDto dto)
   {
      String key = dto.vfsId() + dto.projectPath();
      if (projectUsers.containsKey(key))
      {
         Set<String> ids = projectUsers.get(key);
         ids.remove(clientId);
         if (ids.isEmpty())
         {
            LOG.debug("Remove VFS listener for {} project", dto.projectPath());
            projectUsers.remove(clientId);
            Pair<ChangeEventFilter, EventListener> pair = vfsListeners.remove(clientId);
            listeners.removeEventListener(pair.first, pair.second);
         }
      }
   }

   private static void broadcastToClients(String message, Set<String> collaborators)
   {
      for (String collaborator : collaborators)
      {
         ChannelBroadcastMessage broadcastMessage = new ChannelBroadcastMessage();
         broadcastMessage.setChannel("vfs_watcher." + collaborator);
         broadcastMessage.setBody(message);
         try
         {
            WSConnectionContext.sendMessage(broadcastMessage);
         }
         catch (Exception e)
         {
            LOG.error(e.getMessage(), e);
         }
      }
   }

}
