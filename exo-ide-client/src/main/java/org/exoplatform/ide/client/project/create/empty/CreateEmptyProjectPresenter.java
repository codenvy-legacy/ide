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
package org.exoplatform.ide.client.project.create.empty;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.i18n.client.Constants;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class CreateEmptyProjectPresenter implements CreateEmptyProjectHandler, VfsChangedHandler, ViewClosedHandler
{

   public interface ErrorMessage extends Constants
   {
      @Key("project.cantCreateProjectIfMultiselectionParent")
      @DefaultStringValue("Can't create project you must select only one parent folder")
      String cantCreateProjectIfMultiselectionParent();

      @Key("project.cantCreateProjectIfProjectNameNotSet")
      //@DefaultStringValue("Project name can't be empty or null")
      String cantCreateProjectIfProjectNameNotSet();
   }

   public interface Display extends IsView
   {
      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();

      void setProjectType(Set<String> set);

      void setProjectName(String name);

      HasValue<String> getProjectName();

      HasValue<String> getProjectType();

      Widget asWidget();

   }

   private ErrorMessage errorMessage = GWT.create(ErrorMessage.class);
   
   private Display display;

   private VirtualFileSystemInfo vfsInfo;
   
   public CreateEmptyProjectPresenter() {
//      IDE.getInstance().addControl(new NewProjectMenuGroup());
//      IDE.getInstance().addControl(new CreateProjectControl());

      IDE.addHandler(CreateEmptyProjectEvent.TYPE, this);      
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   private void bindDisplay()
   {
      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doCreateProject();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.setProjectType(ProjectResolver.getProjectsTypes());
   }

   public void doCreateProject()
   {
      if (display.getProjectName().getValue() == null || display.getProjectName().getValue().length() == 0)
      {
         IDE.fireEvent(new ExceptionThrownEvent(errorMessage.cantCreateProjectIfProjectNameNotSet())); //"Project name can't be empty or null"));
         return;
      }

      FolderModel parent = (FolderModel)vfsInfo.getRoot();
      
      ProjectModel model = new ProjectModel();
      model.setName(display.getProjectName().getValue());
      model.setProjectType(display.getProjectType().getValue());
      model.setParent(parent);
      try
      {
         VirtualFileSystem.getInstance().createProject(parent, new AsyncRequestCallback<ProjectModel>(
            new ProjectUnmarshaller(model))
         {
            @Override
            protected void onSuccess(ProjectModel result)
            {
               IDE.getInstance().closeView(display.asView().getId());
               IDE.fireEvent(new ProjectCreatedEvent(result));
               IDE.fireEvent(new RefreshBrowserEvent(result.getParent()));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception,
                  "Service is not deployed.<br>Resource already exist.<br>Parent folder not found."));
            }
         });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * Set default project name ("NewProject").
    * 
    * @param name
    */
   public void setProjectName(String name)
   {
      display.setProjectName(name);
   }

   /**
    * Replace instance of Error Messages. Technically it need for with pure JUnit.  
    * 
    * @param errorMessage
    */
   public void setErrorMessage(ErrorMessage errorMessage)
   {
      this.errorMessage = errorMessage;
   }


   @Override
   public void onCreateProject(CreateEmptyProjectEvent event)
   {
      if (display != null) {
         return;
      }
      
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();      
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      vfsInfo = event.getVfsInfo();
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display) {
         display = null;
      }
   }

}
