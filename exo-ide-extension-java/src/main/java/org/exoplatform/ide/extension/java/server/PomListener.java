/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorageClient;
import org.exoplatform.ide.extension.maven.server.BuilderClient;
import org.exoplatform.ide.extension.maven.server.BuilderException;
import org.exoplatform.ide.utils.ExoConfigurationHelper;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent;
import org.exoplatform.ide.vfs.server.observation.ChangeEvent.ChangeType;
import org.exoplatform.ide.vfs.server.observation.ChangeEventFilter;
import org.exoplatform.ide.vfs.server.observation.EventListener;
import org.exoplatform.ide.vfs.server.observation.EventListenerList;
import org.exoplatform.ide.vfs.server.observation.MimeTypeFilter;
import org.exoplatform.ide.vfs.server.observation.PathFilter;
import org.exoplatform.ide.vfs.server.observation.TypeFilter;
import org.exoplatform.ide.vfs.server.observation.VfsIDFilter;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.picocontainer.Startable;

import java.io.IOException;
import java.util.Timer;

import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class PomListener implements Startable
{
   private final int DELAY;

   private static final Log LOG = ExoLogger.getLogger(PomListener.class);

   private final EventListenerList listenerList;

   private final String vfsId;

   private final BuilderClient client;

   private Timer timer = new Timer(true);

   private final CodeAssistantStorageClient storageClient;

   public PomListener(EventListenerList listenerList, BuilderClient builderClient,
      CodeAssistantStorageClient storageClient, InitParams params)
   {
      this.listenerList = listenerList;
      this.client = builderClient;
      this.storageClient = storageClient;
      vfsId = ExoConfigurationHelper.readValueParam(params, "vfsId");
      DELAY = Integer.parseInt(ExoConfigurationHelper.readValueParam(params, "delay"));
   }

   private EventListener updateListener = new EventListener()
   {

      @Override
      public void handleEvent(ChangeEvent event) throws VirtualFileSystemException
      {
         final VirtualFileSystem vfs = event.getVirtualFileSystem();
         Item item = vfs.getItem(event.getItemId(), PropertyFilter.ALL_FILTER);
         final Item parent = vfs.getItem(item.getParentId(), PropertyFilter.ALL_FILTER);
         //TODO if project create from zip archive, pom.xml file created before parent folder become project, so remove check for now 
         //         if (!Project.PROJECT_MIME_TYPE.equals(parent.getMimeType()))
         //         {
         //            LOG.debug("Pom file '" + item.getPath() + "' not children of project");
         //            return;
         //         }
         try
         {
            final String buildId = client.dependenciesList(vfs, parent.getId());
            timer.schedule(new GenerateDependencysTask(parent, vfs, buildId, client, storageClient, timer, DELAY),
               DELAY, DELAY);
         }
         catch (IOException e)
         {
            LOG.error("Error with project " + parent.getPath(), e);
         }
         catch (BuilderException e)
         {
            LOG.error("Error when build project: " + parent.getPath(), e);
         }
      }

   };

   /**
    * @see org.picocontainer.Startable#start()
    */
   @Override
   public void start()
   {
      if (vfsId == null)
         throw new RuntimeException("VFS ID not configured.");

      VfsIDFilter idFilter = new VfsIDFilter(vfsId);
      MimeTypeFilter mimeTypeFilter = new MimeTypeFilter(MediaType.TEXT_XML);
      PathFilter pathFilter = new PathFilter("^(.*/)?pom\\.xml");
      listenerList.addEventListener(ChangeEventFilter.createAndFilter(//
         idFilter, //
         new TypeFilter(ChangeType.CONTENT_UPDATED),//
         mimeTypeFilter, pathFilter), updateListener);
   }

   /**
    * @see org.picocontainer.Startable#stop()
    */
   @Override
   public void stop()
   {
      timer.cancel();
   }

}
