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
package org.exoplatform.ide.extension.aws.client.beanstalk.application.versions;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 20, 2012 5:01:04 PM anya $
 * 
 */
public class DeleteVersionPresenter implements DeleteVersionHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getDeleteButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getDeleteQuestion();

      HasValue<Boolean> getDeleteS3Bundle();
   }

   private Display display;

   private String vfsId;

   private String projectId;

   private ApplicationVersionInfo version;

   private VersionDeletedHandler versionDeletedHandler;

   public DeleteVersionPresenter()
   {
      IDE.addHandler(DeleteVersionEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

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

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            deleteVersion();
         }
      });
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
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.DeleteVersionHandler#onDeleteVersion(org.exoplatform.ide.extension.aws.client.beanstalk.application.versions.DeleteVersionEvent)
    */
   @Override
   public void onDeleteVersion(DeleteVersionEvent event)
   {
      this.vfsId = event.getVfsId();
      this.projectId = event.getProjectId();
      this.versionDeletedHandler = event.getVersionDeletedHandler();
      this.version = event.getVersion();

      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.getDeleteS3Bundle().setValue(true);
      display.getDeleteQuestion().setValue(
         AWSExtension.LOCALIZATION_CONSTANT.deleteVersionQuestion(version.getVersionLabel()));
   }

   private void deleteVersion()
   {
      try
      {
         BeanstalkClientService.getInstance().deleteVersion(vfsId, projectId, version.getApplicationName(),
            version.getVersionLabel(), display.getDeleteS3Bundle().getValue(),
            new BeanstalkAsyncRequestCallback<Object>(new LoggedInHandler()
            {

               @Override
               public void onLoggedIn()
               {
                  deleteVersion();
               }
            })
            {

               @Override
               protected void processFail(Throwable exception)
               {
                  String message = AWSExtension.LOCALIZATION_CONSTANT.deleteVersionFailed(version.getVersionLabel());
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  Dialogs.getInstance().showError(message);
               }

               @Override
               protected void onSuccess(Object result)
               {
                  IDE.getInstance().closeView(display.asView().getId());
                  if (versionDeletedHandler != null)
                  {
                     versionDeletedHandler.onVersionDeleted(version);
                  }
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }
}
