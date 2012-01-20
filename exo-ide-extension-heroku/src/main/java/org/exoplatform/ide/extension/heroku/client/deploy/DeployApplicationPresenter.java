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
package org.exoplatform.ide.extension.heroku.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.paas.PaasComponent;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.HerokuExtension;
import org.exoplatform.ide.extension.heroku.client.HerokuLocalizationConstant;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationPresenter.java Dec 5, 2011 1:58:22 PM vereshchaka $
 * 
 */
public class DeployApplicationPresenter implements PaasComponent, VfsChangedHandler, LoggedInHandler
{
   interface Display
   {
      HasValue<String> getApplicationNameField();

      HasValue<String> getRemoteNameField();

      Composite getView();

   }

   private static final HerokuLocalizationConstant lb = HerokuExtension.LOCALIZATION_CONSTANT;

   private VirtualFileSystemInfo vfs;

   private Display display;

   private PaasCallback paasCallback;

   private ProjectModel project;

   private String applicationName;

   private String remoteName;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);

      IDE.getInstance().addPaas(new Paas("Heroku", this, Arrays.asList(ProjectResolver.RAILS)));
   }

   public void bindDisplay()
   {

      display.getApplicationNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue().isEmpty())
            {
               applicationName = null;
            }
            else
            {
               applicationName = event.getValue();
            }
         }
      });

      display.getRemoteNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue().isEmpty())
            {
               remoteName = null;
            }
            else
            {
               remoteName = event.getValue();
            }
         }
      });

   }

   // ----Implementation------------------------

   /**
    * Form the message about application creation to display in output.
    * 
    * @param properties application's properties
    * @return {@link String}
    */
   public String formApplicationCreatedMessage(List<Property> properties)
   {
      if (properties == null)
      {
         return lb.createApplicationSuccess("");
      }
      String message = "<br> [";
      for (Property property : properties)
      {
         message += "<b>" + property.getName() + "</b>" + " : " + property.getValue() + "<br>";
      }
      message += "] ";

      return lb.createApplicationSuccess(message);
   }

   private void createApplication()
   {
      try
      {
         HerokuClientService.getInstance().createApplication(applicationName, vfs.getId(), project.getId(), remoteName,
            new HerokuAsyncRequestCallback(this)
            {

               @Override
               protected void onSuccess(List<Property> properties)
               {
                  IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(properties), Type.INFO));
                  IDE.fireEvent(new RefreshBrowserEvent(project));
                  paasCallback.onDeploy(true);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  super.onFailure(exception);
                  paasCallback.onDeploy(false);
               }
            });
      }
      catch (RequestException e)
      {
         paasCallback.onDeploy(false);
      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#getView()
    */
   @Override
   public void getView(String projectName, PaasCallback paasCallback)
   {
      this.paasCallback = paasCallback;
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      // clear values
      display.getApplicationNameField().setValue("");
      display.getRemoteNameField().setValue("");
      applicationName = null;
      remoteName = null;
      this.paasCallback.onViewReceived(display.getView());
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#validate()
    */
   @Override
   public void validate()
   {
      paasCallback.onValidate(true);
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#deploy()
    */
   @Override
   public void deploy(ProjectModel project)
   {
      this.project = project;

      checkIsGitRepository(project);
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
      createApplication();
   }

   private void checkIsGitRepository(final ProjectModel project)
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(
            project,
            new AsyncRequestCallback<List<Item>>(
               new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  for (Item item : result)
                  {
                     if (".git".equals(item.getName()))
                     {
                        // beforeBuild();
                        createApplication();
                        return;
                     }
                  }
                  initRepository(project.getId());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  initRepository(project.getId());
               }
            });
      }
      catch (RequestException e)
      {
      }
   }

   /**
    * Initialize Git repository.
    * 
    * @param path working directory of the repository
    */
   private void initRepository(final String projectId)
   {
      try
      {
         GitClientService.getInstance().init(vfs.getId(), projectId, false,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<String>()
            {
               @Override
               protected void onSuccess(String result)
               {
                  createApplication();
                  // showBuildMessage(GitExtension.MESSAGES.initSuccess());
                  // IDE.fireEvent(new RefreshBrowserEvent());
                  // createJob();
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.initFailed();
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .initFailed();
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
   }

}
