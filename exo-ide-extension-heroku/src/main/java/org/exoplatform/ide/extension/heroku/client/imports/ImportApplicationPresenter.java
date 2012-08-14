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
package org.exoplatform.ide.extension.heroku.client.imports;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ProjectUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.StringProperty;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * Presenter for importing Heroku application. The following steps are done:
 * <ol>
 * <li>Get application information to retrieve GIT URL.</li>
 * <li>Create empty Ruby project.</li>
 * <li>Deploy public SSH key on Heroku, if necessary.</li>
 * <li>Clone Heroku application into created project.</li>
 * <li>Update project's properties, marking it as Heroku application.</li>
 * </ol>
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 19, 2012 10:36:33 AM anya $
 * 
 */
public class ImportApplicationPresenter implements ImportApplicationHandler, ViewClosedHandler, VfsChangedHandler,
   LoggedInHandler
{

   interface Display extends IsView
   {
      HasValue<String> getProjectName();

      HasValue<String> getApplicationName();

      HasValue<Boolean> getDeployPublicKey();

      HasClickHandlers getImportButton();

      HasClickHandlers getCancelButton();

      void setImportButtonEnabled(boolean enabled);
   }

   private final static String GIT_URL = "gitUrl";

   private Display display;

   /**
    * Current VFS.
    */
   private VirtualFileSystemInfo vfs;

   /**
    * GIT repository location.
    */
   private String gitLocation;

   /**
    * Project to be binded with Heroku application.
    */
   private ProjectModel project;

   /**
    * Heroku application to import.
    */
   private String herokuApplication;

   public ImportApplicationPresenter()
   {
      IDE.addHandler(ImportApplicationEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getProjectName().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isEmpty = event.getValue() == null || event.getValue().isEmpty();
            display.setImportButtonEnabled(!isEmpty);
         }
      });

      display.getImportButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            createEmptyProject();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationHandler#onImportApplication(org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationEvent)
    */
   @Override
   public void onImportApplication(ImportApplicationEvent event)
   {
      this.herokuApplication = event.getApplication();
      getApplicationInfo(event.getApplication());
   }

   /**
    * Get application's information.
    * 
    * @param applicationName application for getting info
    */
   private void getApplicationInfo(final String applicationName)
   {
      try
      {
         HerokuClientService.getInstance().getApplicationInfo(applicationName, vfs.getId(), null, false,
            new HerokuAsyncRequestCallback(this)
            {
               @Override
               protected void onSuccess(List<Property> properties)
               {
                  for (Property property : properties)
                  {
                     if (GIT_URL.equals(property.getName()))
                     {
                        gitLocation = property.getValue();
                        break;
                     }
                  }

                  if (display == null)
                  {
                     display = GWT.create(Display.class);
                     bindDisplay();
                     IDE.getInstance().openView(display.asView());
                  }
                  display.getApplicationName().setValue(applicationName);
                  display.getProjectName().setValue(applicationName);
                  display.getDeployPublicKey().setValue(true);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
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

   /**
    * Create empty Ruby project.
    */
   private void createEmptyProject()
   {
      if (vfs == null)
      {
         return;
      }

      FolderModel parent = (FolderModel)vfs.getRoot();
      ProjectModel model = new ProjectModel();
      model.setName(display.getProjectName().getValue());
      model.setProjectType(ProjectResolver.RAILS);
      model.setParent(parent);

      final boolean deployPublicKey = display.getDeployPublicKey().getValue();
      try
      {
         VirtualFileSystem.getInstance().createProject(parent,
            new AsyncRequestCallback<ProjectModel>(new ProjectUnmarshaller(model))
            {
               @Override
               protected void onSuccess(ProjectModel result)
               {
                  project = result;
                  if (deployPublicKey)
                  {
                     deployPublicKey();
                  }
                  else
                  {
                     cloneRepository();
                  }
                  IDE.getInstance().closeView(display.asView().getId());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Clone repository. Remote repository is Heroku application.
    */
   private void cloneRepository()
   {
      try
      {
         GitClientService.getInstance().cloneRepository(vfs.getId(), project, gitLocation, null,
            new AsyncRequestCallback<RepoInfo>()
            {
               @Override
               protected void onSuccess(RepoInfo result)
               {
                  updateProperties();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Deploy public key to Heroku.
    */
   private void deployPublicKey()
   {
      try
      {
         HerokuClientService.getInstance().addKey(new HerokuAsyncRequestCallback(this)
         {
            @Override
            protected void onSuccess(List<Property> properties)
            {
               IDE.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT.addKeysSuccess(), Type.INFO));
               cloneRepository();
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Update project properties, setting "heroku-application" property's value.
    */
   private void updateProperties()
   {
      project.getProperties().add(new StringProperty("heroku-application", herokuApplication));

      try
      {
         VirtualFileSystem.getInstance().updateItem(project, null, new AsyncRequestCallback<Object>()
         {
            @Override
            protected void onSuccess(Object result)
            {
               IDE.fireEvent(new OutputEvent(HerokuExtension.LOCALIZATION_CONSTANT
                  .importApplicationSuccess(herokuApplication), Type.INFO));
               IDE.fireEvent(new OpenProjectEvent(project));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
         });

      }
      catch (Exception e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.application.event.VfsChangedHandler#onVfsChanged(org.exoplatform.ide.client.framework.application.event.VfsChangedEvent)
    */
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      this.vfs = event.getVfsInfo();
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getApplicationInfo(herokuApplication);
      }
   }
}
