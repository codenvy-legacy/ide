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
package org.exoplatform.ide.extension.googleappengine.client.deploy;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.googleappengine.client.login.PerformOperationHandler;
import org.exoplatform.ide.extension.maven.client.event.BuildProjectEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent;
import org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 16, 2012 5:51:08 PM anya $
 * 
 */
public class DeployApplicationPresenter extends GoogleAppEnginePresenter implements DeployApplicationHandler,
   ProjectBuiltHandler
{
   private String email;

   private String password;

   private LoggedInHandler loggedInHandler;

   private String applicationUrl;

   private PerformOperationHandler performOperationHandler = new PerformOperationHandler()
   {

      @Override
      public void onPerformOperation(String e, String p, LoggedInHandler handler)
      {
         email = e;
         password = p;
         loggedInHandler = handler;
         deployApplication();
      }
   };

   public DeployApplicationPresenter()
   {
      // TODO removed from menu:
      // IDE.getInstance().addControl(new DeployApplicationControl());

      IDE.addHandler(DeployApplicationEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationHandler#onDeployApplication(org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationEvent)
    */
   @Override
   public void onDeployApplication(DeployApplicationEvent event)
   {
      if (currentProject != null && ProjectResolver.APP_ENGINE_JAVA.equals(currentProject.getProjectType()))
      {
         email = null;
         password = null;
         loggedInHandler = null;
         buildProject();
      }
   }

   public void deployApplication()
   {
      try
      {
         GoogleAppEngineClientService.getInstance().update(currentVfs.getId(), currentProject, applicationUrl, email,
            password, new GoogleAppEngineAsyncRequestCallback<Object>(performOperationHandler, null)
            {

               @Override
               protected void onSuccess(Object result)
               {
                  if (loggedInHandler != null)
                  {
                     loggedInHandler.onLoggedIn();
                  }
                  IDE.fireEvent(new OutputEvent(GoogleAppEngineExtension.GAE_LOCALIZATION
                     .deployApplicationSuccess(currentProject.getName()), Type.INFO));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void buildProject()
   {
      this.applicationUrl = null;
      IDE.addHandler(ProjectBuiltEvent.TYPE, this);
      IDE.fireEvent(new BuildProjectEvent());
   }

   /**
    * @see org.exoplatform.ide.extension.maven.client.event.ProjectBuiltHandler#onProjectBuilt(org.exoplatform.ide.extension.maven.client.event.ProjectBuiltEvent)
    */
   @Override
   public void onProjectBuilt(ProjectBuiltEvent event)
   {
      IDE.removeHandler(ProjectBuiltEvent.TYPE, this);
      if (event.getBuildStatus().getDownloadUrl() != null)
      {
         applicationUrl = event.getBuildStatus().getDownloadUrl();
         deployApplication();
      }
   }
}
