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
package org.exoplatform.ide.extension.cloudbees.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
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
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesLocalizationConstant;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.login.LoginCanceledHandler;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltEvent;
import org.exoplatform.ide.extension.jenkins.client.event.ApplicationBuiltHandler;
import org.exoplatform.ide.extension.jenkins.client.event.BuildApplicationEvent;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DeployApplicationPresenter.java Dec 5, 2011 1:58:22 PM vereshchaka $
 *
 */
public class DeployApplicationPresenter implements ApplicationBuiltHandler, PaasComponent, VfsChangedHandler
{
   interface Display
   {
      HasValue<String> getNameField();

      HasValue<String> getUrlField();

      HasValue<String> getDomainsField();

      /**
       * Set the list of domains.
       * 
       * @param domains
       */
      void setDomainValues(String[] domains);

      Composite getView();

   }

   private static final CloudBeesLocalizationConstant lb = CloudBeesExtension.LOCALIZATION_CONSTANT;

   private VirtualFileSystemInfo vfs;

   private Display display;

   private String domain;

   private String name;

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

      IDE.getInstance().addPaas(new Paas("CloudBees", this, Arrays.asList(ProjectResolver.SERVLET_JSP)));
   }

   public void bindDisplay()
   {

      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            name = event.getValue();
            display.getUrlField().setValue(domain + "/" + name);
         }
      });

      display.getDomainsField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            domain = display.getDomainsField().getValue();
            display.getUrlField().setValue(domain + "/" + name);
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

   private void getDomains()
   {
      CloudBeesClientService.getInstance().getDomains(
         new CloudBeesAsyncRequestCallback<List<String>>(IDE.eventBus(), new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               getDomains();
            }
         }, new LoginCanceledHandler()
         {
            @Override
            public void onLoginCanceled()
            {
               paasCallback.onViewReceived(null);
            }
         })
         {
            @Override
            protected void onSuccess(List<String> result)
            {
               display.setDomainValues(result.toArray(new String[result.size()]));
               domain = display.getDomainsField().getValue();
               display.getNameField().setValue(projectName);
               name = display.getNameField().getValue();
               display.getUrlField().setValue(domain + "/" + name);
               paasCallback.onViewReceived(display.getView());
            }
         });
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

      CloudBeesClientService.getInstance().initializeApplication(domain + "/" + name, vfs.getId(), project.getId(),
         warUrl, null, new CloudBeesAsyncRequestCallback<Map<String, String>>(IDE.eventBus(), createAppHandler, null)
         {

            @Override
            protected void onSuccess(Map<String, String> result)
            {
               String output = CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationSuccess() + "<br>";
               output += CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationInfo() + "<br>";

               Iterator<Entry<String, String>> it = result.entrySet().iterator();
               while (it.hasNext())
               {
                  Entry<String, String> entry = (Entry<String, String>)it.next();
                  output += entry.getKey() + " : " + entry.getValue() + "<br>";
               }
               IDE.fireEvent(new OutputEvent(output, Type.INFO));
               IDE.fireEvent(new RefreshBrowserEvent(project));
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new OutputEvent(
                  CloudBeesExtension.LOCALIZATION_CONSTANT.deployApplicationFailureMessage(), Type.INFO));
               super.onFailure(exception);
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
      getDomains();
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#validate()
    */
   @Override
   public void validate()
   {
      name = display.getNameField().getValue();
      if (name == null || name.isEmpty())
      {
         Dialogs.getInstance().showError("Name field must be not empty");
         paasCallback.onValidate(false);
      }
      else
      {
         paasCallback.onValidate(true);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#deploy()
    */
   @Override
   public void deploy(ProjectModel project)
   {
      this.project = project;
      buildApplication();
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
