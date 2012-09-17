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
package org.eclipse.jdt.client;

import com.google.gwt.user.client.Timer;

import com.google.collide.json.shared.JsonStringSet;
import com.google.collide.shared.util.JsonCollections;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;

import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class PackagesUpdater implements ProjectOpenedHandler, FileSavedHandler, ProjectClosedHandler
{
   
   private static final int DALAY = 1000 * 60 * 5;
   
   private static final int MAX_REQUEST = 3;
   
   private final HandlerManager eventBus;

   private final SupportedProjectResolver projectResolver;

   private HandlerRegistration saveFileHandler;

   private final TypeInfoStorage storage;
   
   private int requestCount = 0;
   
   private String projectId;

   public PackagesUpdater(HandlerManager eventBus, SupportedProjectResolver projectResolver, TypeInfoStorage storage)
   {
      this.eventBus = eventBus;
      this.projectResolver = projectResolver;
      this.storage = storage;
      eventBus.addHandler(ProjectOpenedEvent.TYPE, this);
      eventBus.addHandler(ProjectClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event.FileSavedEvent)
    */
   @Override
   public void onFileSaved(FileSavedEvent event)
   {
      if("pom.xml".equals(event.getFile().getName()))
      {
         requestCount = 0;
         timer.cancel();
         timer.schedule(DALAY);
      }
   }
   
   private Timer timer = new Timer()
   {
      @Override
      public void run()
      {
         updatePackages(projectId);
         requestCount++;
         if(requestCount < MAX_REQUEST)
           schedule(DALAY);
      }
   };

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      if (projectResolver.isProjectSupported(event.getProject().getProjectType()))
      {
         saveFileHandler = eventBus.addHandler(FileSavedEvent.TYPE, this);
         projectId  = event.getProject().getId();
         updatePackages(projectId);
      }
   }

   /**
    * @param projectId
    */
   private void updatePackages(final String projectId)
   {
      String url =
         JdtExtension.REST_CONTEXT + "/ide/code-assistant/java/get-packages" + "?projectid=" + projectId + "&vfsid="
            + VirtualFileSystem.getInstance().getInfo().getId();
      try
      {
         AsyncRequest.build(RequestBuilder.GET,url).send(new AsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
         {

            @Override
            protected void onSuccess(StringBuilder result)
            {
               JSONArray  arr = JSONParser.parseLenient(result.toString()).isArray();
               JsonStringSet stringSet = JsonCollections.createStringSet();
               for (int i = 0; i < arr.size(); i++)
               {
                 stringSet.add(arr.get(i).isString().stringValue());
               }
               storage.setPackages(projectId, stringSet);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new OutputEvent(exception.getMessage(), Type.ERROR));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new OutputEvent(e.getMessage(), Type.ERROR));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      if (saveFileHandler != null)
      {
         saveFileHandler.removeHandler();
         saveFileHandler = null;
      }
   }

}
