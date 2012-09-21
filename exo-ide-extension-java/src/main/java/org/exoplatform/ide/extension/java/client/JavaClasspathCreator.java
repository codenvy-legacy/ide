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
package org.exoplatform.ide.extension.java.client;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.ProjectType;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class JavaClasspathCreator implements ProjectOpenedHandler
{

   private static final Set<String> projectTypes = new HashSet<String>();

   private final String restContext;

   private final HandlerManager eventBus;

   static
   {
      //TODO move this class to JDT extension and use SupportedProjects interface to remove this hardcode
      projectTypes.add(ProjectResolver.SERVLET_JSP);
      projectTypes.add(ProjectResolver.SPRING);
      projectTypes.add(ProjectResolver.APP_ENGINE_JAVA);
      projectTypes.add(ProjectType.JAVA.value());
      projectTypes.add(ProjectType.SPRING.value());
      projectTypes.add(ProjectType.JSP.value());
   }

   /**
    * 
    */
   public JavaClasspathCreator(HandlerManager eventBus, String restContext)
   {
      this.eventBus = eventBus;
      this.restContext = restContext;
      eventBus.addHandler(ProjectOpenedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      ProjectModel project = event.getProject();
      if (projectTypes.contains(project.getProjectType()) && !project.hasProperty("exoide:classpath"))
      {
         generateClassPath(project.getId());
      }
   }

   /**
    * @param id
    */
   private void generateClassPath(String projectId)
   {
      String url =
         restContext + "/ide/java/classpath/generate" + "?projectid=" + projectId + "&vfsid=" + VirtualFileSystem.getInstance().getInfo().getId();
      try
      {
         AsyncRequest.build(RequestBuilder.POST, url).send(new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(e));
      }
   }

}
