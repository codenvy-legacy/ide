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

import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorageClient;
import org.exoplatform.ide.extension.maven.server.BuilderClient;
import org.exoplatform.ide.extension.maven.server.BuilderException;
import org.exoplatform.ide.extension.maven.shared.BuildStatus;
import org.exoplatform.ide.extension.maven.shared.BuildStatus.Status;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class GenerateDependencysTask extends TimerTask
{

   private static final Log LOG = ExoLogger.getLogger(GenerateDependencysTask.class);

   /**
    * 
    */
   private final Item parent;

   /**
    * 
    */
   private final VirtualFileSystem vfs;

   /**
    * 
    */
   private final String buildId;

   private final Timer timer;

   private final BuilderClient builderClient;

   private final CodeAssistantStorageClient storageClient;

   private final int delay;

   public GenerateDependencysTask(Item parent, VirtualFileSystem vfs, String buildId, BuilderClient client,
      CodeAssistantStorageClient storageClient, Timer timer, int delay)
   {
      this.parent = parent;
      this.vfs = vfs;
      this.buildId = buildId;
      this.builderClient = client;
      this.storageClient = storageClient;
      this.timer = timer;
      this.delay = delay;
   }

   @Override
   public void run()
   {
      try
      {
         String status = builderClient.status(buildId);
         JsonParser parser = new JsonParser();
         parser.parse(new ByteArrayInputStream(status.getBytes("UTF-8")));
         BuildStatusBean buildStatus = ObjectBuilder.createObject(BuildStatusBean.class, parser.getJsonObject());
         if (Status.IN_PROGRESS != buildStatus.getStatus())
         {
            cancel();
            buildFinished(buildStatus, vfs, parent);
         }

      }
      catch (Exception e)
      {
         cancel();
         LOG.error("Project build '" + parent.getPath() + " failed", e);
      }
   }

   private void buildFinished(BuildStatus buildStatus, VirtualFileSystem vfs, Item project)
   {
      if (buildStatus.getStatus() == Status.FAILED)
      {
         String message =
            "Build failed, exit code: " + buildStatus.getExitCode() + ", message: " + buildStatus.getError();
         LOG.warn(message);
         List<Property> properties =
            Arrays.asList(new Property("exoide:build_error", buildId), new Property("exoide:classpath", (String)null));
         try
         {
            ConversationState.setCurrent(new ConversationState(new Identity("__system")));
            vfs.updateItem(project.getId(), properties, null);
         }
         catch (VirtualFileSystemException e)
         {
            if (LOG.isDebugEnabled())
               LOG.debug("Error while write build error message to project: " + project.getPath(), e);
         }
         finally
         {
            ConversationState.setCurrent(null);
         }
         return;
      }
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(buildStatus.getDownloadUrl());
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
         {
            LOG.error("Can't dowload dependency list from: " + buildStatus.getDownloadUrl());
         }

         InputStream data = http.getInputStream();
         try
         {
            ConversationState.setCurrent(new ConversationState(new Identity("__system")));
            String dependencys = readBody(data, http.getContentLength());
            List<Property> properties = Arrays.asList(new Property("exoide:classpath", dependencys),
                  new Property("exoide:build_error", (String)null));

            vfs.updateItem(project.getId(), properties, null);
            copyDependencys(project, vfs, dependencys);
         }
         catch (VirtualFileSystemException e)
         {
            LOG.error("Can't set classpath property, for project: " + project.getPath(), e);
         }
         catch (Exception e)
         {
            LOG.error("Error", e);
         }
         finally
         {
            data.close();
            ConversationState.setCurrent(null);
         }
      }
      catch (MalformedURLException e)
      {
         LOG.error("Invalid URL", e);
      }
      catch (IOException e)
      {
         LOG.error("Error", e);
      }
      finally
      {
         if (http != null)
         {
            http.disconnect();
         }
      }

   }

   private void copyDependencys(final Item project, final VirtualFileSystem vfs, final String dependencyList)
   {
      try
      {
         final String copyId = builderClient.dependenciesCopy(vfs, project.getId(), null);
         timer.schedule(new BuildDependencyTask(builderClient, storageClient, vfs, timer, dependencyList, project,
            copyId, delay), delay, delay);

      }
      catch (IOException e)
      {
         LOG.error("Error with project " + project.getPath(), e);
      }
      catch (BuilderException e)
      {
         LOG.error("Error when build project: " + project.getPath(), e);
      }
      catch (VirtualFileSystemException e)
      {
         LOG.error("Error when build project: " + project.getPath(), e);
      }
   }

   private String readBody(InputStream input, int contentLength) throws IOException
   {
      String body = null;
      if (contentLength > 0)
      {
         byte[] b = new byte[contentLength];
         int off = 0;
         int i;
         while ((i = input.read(b, off, contentLength - off)) > 0)
         {
            off += i;
         }
         body = new String(b);
      }
      else if (contentLength < 0)
      {
         ByteArrayOutputStream bout = new ByteArrayOutputStream();
         byte[] buf = new byte[1024];
         int i;
         while ((i = input.read(buf)) != -1)
         {
            bout.write(buf, 0, i);
         }
         body = bout.toString();
      }
      return body;
   }
}