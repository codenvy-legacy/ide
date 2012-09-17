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
package org.exoplatform.ide.extension.aws.client.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationRequest;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 17, 2012 11:54:00 AM anya $
 * 
 */
public class CreateApplicationPresenter implements ProjectOpenedHandler, ProjectClosedHandler, VfsChangedHandler,
   CreateApplicationHandler, ViewClosedHandler
{
   interface Display extends IsView
   {

   }

   private Display display;

   private ProjectModel openedProject;

   private VirtualFileSystemInfo vfsInfo;

   public CreateApplicationPresenter()
   {
      IDE.getInstance().addControl(new CreateApplicationControl());

      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      // TODO
   }

   /**
    * @see org.exoplatform.ide.extension.aws.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.aws.client.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfsInfo = event.getVfsInfo();
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      this.openedProject = null;
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(ProjectOpenedEvent event)
   {
      this.openedProject = event.getProject();
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   public void createApplication()
   {
      CreateApplicationRequest createApplicationRequest =
         AWSExtension.AUTO_BEAN_FACTORY.createApplicationRequest().as();
      // TODO set create application request
      AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();

      try
      {
         BeanstalkClientService.getInstance().createApplication(vfsInfo.getId(), openedProject.getId(),
            createApplicationRequest,
            new AsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean))
            {

               @Override
               protected void onSuccess(ApplicationInfo result)
               {
                  // Display result of the created application
                  // TODO Auto-generated method stub

               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  // TODO Auto-generated method stub

               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
