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
package org.exoplatform.ide.client.model.project;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.user.client.Random;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPMethod;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.project.Project;
import org.exoplatform.ide.client.framework.project.ProjectList;
import org.exoplatform.ide.client.framework.project.ProjectService;
import org.exoplatform.ide.client.framework.project.event.ProjectListReceivedEvent;
import org.exoplatform.ide.client.framework.project.event.ProjectRemovedEvent;
import org.exoplatform.ide.client.framework.project.event.ProjectSavedEvent;
import org.exoplatform.ide.client.model.project.marshal.ProjectListUnmarshaller;
import org.exoplatform.ide.client.model.project.marshal.ProjectMarshaller;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 22, 2010 $
 *
 */
public class ProjectServiceImpl extends ProjectService
{
   private static final String CONTEXT = "/projects";

   private static final String PROJECT = "project-";

   /**
    * REST context.
    */
   private String restContext;

   /**
    * Handler manager.
    */
   private HandlerManager eventBus;

   /**
    * Loader to display.
    */
   private Loader loader;

   /**
    * @param eventBus handler manager
    * @param loader loader to display
    * @param restContext rest context
    */
   public ProjectServiceImpl(HandlerManager eventBus, Loader loader, String restContext)
   {
      this.eventBus = eventBus;
      this.loader = loader;
      this.restContext = restContext;
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectService#getCreatedProjects()
    */
   @Override
   public void getCreatedProjects()
   {
      String url = restContext + CONTEXT + "/?noCache=" + Random.nextInt();
      ProjectList projectList = new ProjectList();

      ProjectListUnmarshaller unmarshaller = new ProjectListUnmarshaller(projectList);
      ProjectListReceivedEvent event = new ProjectListReceivedEvent(projectList);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, unmarshaller, event, event);
      AsyncRequest.build(RequestBuilder.GET, url, loader).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectService#saveProject(org.exoplatform.ide.client.framework.project.Project)
    */
   @Override
   public void saveProject(Project project)
   {
      String url = restContext + CONTEXT + "/" + PROJECT + System.currentTimeMillis() + "/?createIfNotExist=true";
      ProjectMarshaller marshaller = new ProjectMarshaller(project);
      ProjectSavedEvent event = new ProjectSavedEvent(project);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, event);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.PUT)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).data(marshaller).send(callback);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectService#removeProject(org.exoplatform.ide.client.framework.project.Project)
    */
   @Override
   public void removeProject(Project project)
   {
      String url = restContext + CONTEXT + "/" + project.getNodeId();
      ProjectRemovedEvent event = new ProjectRemovedEvent(project);

      AsyncRequestCallback callback = new AsyncRequestCallback(eventBus, event, event);
      AsyncRequest.build(RequestBuilder.POST, url, loader).header(HTTPHeader.X_HTTP_METHOD_OVERRIDE, HTTPMethod.DELETE)
         .header(HTTPHeader.CONTENT_TYPE, MimeType.APPLICATION_XML).send(callback);
   }

}
