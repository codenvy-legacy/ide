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

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorageClient;
import org.exoplatform.ide.extension.maven.server.BuilderClient;
import org.exoplatform.ide.extension.maven.server.BuilderException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.IOException;
import java.util.Timer;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class BuildDependencyTask extends BuildTask
{

   private static final Log LOG = ExoLogger.getLogger(BuildDependencyTask.class);

   private final Timer timer;

   private final VirtualFileSystem vfs;

   private final int delay;

   /**
    * @param dependencyList
    * @param project
    * @param copyId
    */
   BuildDependencyTask(BuilderClient client, CodeAssistantStorageClient storageClient, VirtualFileSystem vfs,
      Timer timer, String dependencyList, Item project, String copyId, int delay)
   {
      super(dependencyList, project, copyId, client, storageClient);
      this.vfs = vfs;
      this.timer = timer;
      this.delay = delay;
   }

   /**
    * @throws IOException 
    * @see org.exoplatform.ide.extension.java.server.BuildTask#buildSuccess(java.lang.String)
    */
   @Override
   protected void buildSuccess(String downloadUrl) throws IOException
   {
      storageClient.updateTypeIndex(dependencyList, downloadUrl);
      try
      {
         ConversationState.setCurrent(new ConversationState(new Identity("__system")));
         String copyId = client.dependenciesCopy(vfs, project.getId(), "sources");
         timer.schedule(new BuildSourcesTask(dependencyList, project, copyId, client, storageClient), delay, delay);
      }
      catch (BuilderException e)
      {
         LOG.error("Error when build project: " + project.getPath(), e);
      }
      catch (VirtualFileSystemException e)
      {
         LOG.error("Error when build project: " + project.getPath(), e);
      }
      finally
      {
         ConversationState.setCurrent(null);
      }
   }

}