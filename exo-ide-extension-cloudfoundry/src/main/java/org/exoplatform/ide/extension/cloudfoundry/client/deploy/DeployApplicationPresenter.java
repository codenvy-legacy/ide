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
package org.exoplatform.ide.extension.cloudfoundry.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.paas.PaasComponent;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationPresenter.java Dec 2, 2011 10:17:23 AM vereshchaka $
 */
public class DeployApplicationPresenter implements ApplicationBuiltHandler, PaasComponent, VfsChangedHandler
{
   interface Display
   {
      HasValue<String> getNameField();

      HasValue<String> getUrlField();

      HasValue<String> getServerField();

      /**
       * Set the list of servers to ServerSelectField.
       * 
       * @param servers
       */
      void setServerValues(String[] servers);

      Composite getView();

   }

   private static final CloudFoundryLocalizationConstant lb = CloudFoundryExtension.LOCALIZATION_CONSTANT;

   private VirtualFileSystemInfo vfs;

   private Display display;

   private String server;

   private String name;

   private String url;

   /**
    * Public url to war file of application. 
    */
   private String warUrl;

   private PaasCallback paasCallback;

   private String projectName;

   private ProjectModel project;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);

      IDE.getInstance().addPaas(
         new Paas("CloudFoundry", this, Arrays.asList(ProjectResolver.RAILS, ProjectResolver.SERVLET_JSP,
            ProjectResolver.SPRING)));
   }

   public void bindDisplay()
   {

      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            name = event.getValue();
         }
      });

      display.getUrlField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            url = event.getValue();
         }
      });

      display.getServerField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            server = display.getServerField().getValue();
            //if url set automatically, than try to create url using server and name
            String target = display.getServerField().getValue();
            String sufix = target.substring(target.indexOf("."));
            String oldUrl = display.getUrlField().getValue();
            String prefix = "<name>";
            if (!oldUrl.isEmpty() && oldUrl.contains("."))
            {
               prefix = oldUrl.substring(0, oldUrl.indexOf("."));
            }
            String url = prefix + sufix;
            display.getUrlField().setValue(url);
         }
      });

   }

   /**
    * @see org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler#onApplicationBuilt(org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent)
    */
   @Override
   public void onApplicationBuilt(ApplicationBuiltEvent event)
   {
      IDE.removeHandler(event.getAssociatedType(), this);
      if (event.getJobStatus().getArtifactUrl() != null)
      {
         warUrl = event.getJobStatus().getArtifactUrl();
         createApplication();
      }
   }

   //----Implementation------------------------

   private void buildApplication()
   {
      IDE.addHandler(ApplicationBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildApplicationEvent(project));
   }

   private void createApplication()
   {
      LoggedInHandler createAppHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            createApplication();
         }
      };

      CloudFoundryClientService.getInstance().create(server, name, null, url, 0, 0, true, vfs.getId(), project.getId(),
         warUrl,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), createAppHandler, null, server)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               String msg = lb.applicationCreatedSuccessfully(result.getName());
               IDE.fireEvent(new OutputEvent(msg, OutputMessage.Type.INFO));
               IDE.fireEvent(new RefreshBrowserEvent(project));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new OutputEvent(lb.applicationCreationFailed(), OutputMessage.Type.INFO));
               super.onFailure(exception);
            }
         });
   }

   /**
    * Get the list of server and put them to select field.
    */
   private void getServers()
   {
      CloudFoundryClientService.getInstance().getTargets(new AsyncRequestCallback<List<String>>()
      {
         @Override
         protected void onSuccess(List<String> result)
         {
            if (result.isEmpty())
            {
               display.setServerValues(new String[]{CloudFoundryExtension.DEFAULT_SERVER});
               display.getServerField().setValue(CloudFoundryExtension.DEFAULT_SERVER);
            }
            else
            {
               String[] servers = result.toArray(new String[result.size()]);
               display.setServerValues(servers);
               display.getServerField().setValue(servers[0]);
            }
            display.getNameField().setValue(projectName);
            //don't forget to init values, that are stored, when 
            //values in form fields are changed.
            name = projectName;
            server = display.getServerField().getValue();
            String urlSufix = server.substring(server.indexOf("."));
            display.getUrlField().setValue("<name>" + urlSufix);
            url = display.getUrlField().getValue();

            paasCallback.onViewReceived(display.getView());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#getView()
    */
   @Override
   public void getView(String projectName, PaasCallback paasCallback)
   {
      this.paasCallback = paasCallback;
      this.projectName = projectName;
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      getServers();
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#validate()
    */
   @Override
   public void validate()
   {
      LoggedInHandler validateHandler = new LoggedInHandler()
      {
         @Override
         public void onLoggedIn()
         {
            validate();
         }
      };

      CloudFoundryClientService.getInstance().validateAction("create", server, name, null, url, vfs.getId(), null, 0,
         0, true, new CloudFoundryAsyncRequestCallback<String>(IDE.eventBus(), validateHandler, null, server)
         {
            @Override
            protected void onSuccess(String result)
            {
               paasCallback.onValidate(true);
            }
         });
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#deploy()
    */
   @Override
   public void deploy(final ProjectModel project)
   {
      this.project = project;
      try
      {
         VirtualFileSystem.getInstance().getChildren(
            project,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<List<Item>>(
               new ChildrenUnmarshaller(new ArrayList<Item>()))
            {

               @Override
               protected void onSuccess(List<Item> result)
               {
                  project.getChildren().setItems(result);
                  for (Item i : result)
                  {
                     if (i.getItemType() == ItemType.FILE && "pom.xml".equals(i.getName()))
                     {
                        buildApplication();
                        return;
                     }
                  }
                  
                  createApplication();
                  
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception,
                     "Can't receive project children " + project.getName()));
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
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

}
