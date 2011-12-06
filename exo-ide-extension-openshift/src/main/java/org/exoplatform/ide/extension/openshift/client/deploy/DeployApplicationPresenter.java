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
package org.exoplatform.ide.extension.openshift.client.deploy;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.HTTPStatus;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.paas.Paas;
import org.exoplatform.ide.client.framework.paas.PaasCallback;
import org.exoplatform.ide.client.framework.paas.PaasComponent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.OpenShiftLocalizationConstant;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

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

      HasValue<String> getTypeField();

      void setTypeValues(String[] types);

      Composite getView();

   }

   private static final OpenShiftLocalizationConstant lb = OpenShiftExtension.LOCALIZATION_CONSTANT;

   private VirtualFileSystemInfo vfs;

   private Display display;

   private PaasCallback paasCallback;

   private ProjectModel project;

   private String applicationName;

   private String applicationType;

   public DeployApplicationPresenter()
   {
      IDE.addHandler(VfsChangedEvent.TYPE, this);

      IDE.getInstance().addPaas(new Paas("OpenShift", this));
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

      display.getTypeField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            if (event.getValue().isEmpty())
            {
               applicationType = null;
            }
            else
            {
               applicationType = event.getValue();
            }
         }
      });

   }

   //----Implementation------------------------

   /**
    * Register {@link LoggedInHandler} handler.
    */
   protected void addLoggedInHandler()
   {
      IDE.addHandler(LoggedInEvent.TYPE, this);
   }

   /**
    * Forms the message to be shown, when application is created.
    * 
    * @param appInfo application information
    * @return {@link String} message
    */
   protected String formApplicationCreatedMessage(AppInfo appInfo)
   {
      String applicationStr = "<br> [";
      applicationStr += "<b>Name</b>" + " : " + appInfo.getName() + "<br>";
      applicationStr += "<b>Git URL</b>" + " : " + appInfo.getGitUrl() + "<br>";
      applicationStr += "<b>Public URL</b>" + " : " + appInfo.getPublicUrl() + "<br>";
      applicationStr += "<b>Type</b>" + " : " + appInfo.getType() + "<br>";
      applicationStr += "] ";

      return lb.createApplicationSuccess(applicationStr);
   }

   private void createApplication()
   {
      OpenShiftClientService.getInstance().createApplication(applicationName, vfs.getId(), project.getId(),
         applicationType, new AsyncRequestCallback<AppInfo>()
         {

            @Override
            protected void onSuccess(AppInfo result)
            {
               IDE.fireEvent(new OutputEvent(formApplicationCreatedMessage(result), Type.INFO));
               paasCallback.onDeploy(true);
            }

            /**
             * @see org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback#onFailure(java.lang.Throwable)
             */
            @Override
            protected void onFailure(Throwable exception)
            {
               if (exception instanceof ServerException)
               {
                  ServerException serverException = (ServerException)exception;
                  if (HTTPStatus.OK == serverException.getHTTPStatus()
                     && "Authentication-required".equals(serverException.getHeader(HTTPHeader.JAXRS_BODY_PROVIDED)))
                  {
                     addLoggedInHandler();
                     IDE.fireEvent(new LoginEvent());
                     return;
                  }
               }
               IDE.fireEvent(new OpenShiftExceptionThrownEvent(exception, lb.createApplicationFail(applicationName)));
               paasCallback.onDeploy(false);
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
      if (display == null)
      {
         display = GWT.create(Display.class);
      }
      bindDisplay();
      getApplicationTypes();
   }

   /**
    * @see org.exoplatform.ide.client.framework.paas.PaasComponent#validate()
    */
   @Override
   public void validate()
   {
      if (applicationName == null || applicationName.isEmpty())
      {
         Dialogs.getInstance().showError("Application name must not be empty");
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
      createApplication();
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
    * @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         createApplication();
      }
   }

   private void getApplicationTypes()
   {
      OpenShiftClientService.getInstance().getApplicationTypes(new AsyncRequestCallback<List<String>>()
      {
         @Override
         protected void onSuccess(List<String> result)
         {
            display.setTypeValues(result.toArray(new String[result.size()]));
            applicationType = display.getTypeField().getValue();
            display.getApplicationNameField().setValue("");
            applicationName = null;
            paasCallback.onViewReceived(display.getView());
         }
      });
   }

}
