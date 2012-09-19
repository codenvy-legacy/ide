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
package org.exoplatform.ide.extension.aws.client.beanstalk.application.update;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkAsyncRequestCallback;
import org.exoplatform.ide.extension.aws.client.beanstalk.BeanstalkClientService;
import org.exoplatform.ide.extension.aws.client.beanstalk.login.LoggedInHandler;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateApplicationRequest;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Sep 19, 2012 4:45:58 PM anya $
 * 
 */
public class UpdateApplicationPresenter implements UpdateApplicationHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getUpdateButton();

      HasClickHandlers getCancelButton();

      TextFieldItem getDescriptionField();

      void enableUpdateButton(boolean enabled);

      void focusInDescriptionField();
   }

   private Display display;

   private String vfsId;

   private String projectId;

   private ApplicationUpdatedHandler applicationUpdatedHandler;

   private ApplicationInfo applicationInfo;

   public UpdateApplicationPresenter()
   {
      IDE.addHandler(UpdateApplicationEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
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
    * @see org.exoplatform.ide.extension.aws.client.beanstalk.application.update.UpdateApplicationHandler#onUpdateApplication(org.exoplatform.ide.extension.aws.client.beanstalk.application.update.UpdateApplicationEvent)
    */
   @Override
   public void onUpdateApplication(UpdateApplicationEvent event)
   {
      this.vfsId = event.getVfsId();
      this.projectId = event.getProjectId();
      this.applicationInfo = event.getApplicationInfo();
      this.applicationUpdatedHandler = event.getApplicationUpdatedHandler();

   /*TODO   if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }

      display.getDescriptionField().setValue(
         applicationInfo.getDescription() != null ? applicationInfo.getDescription() : "");
      display.focusInDescriptionField();
      display.enableUpdateButton(false);*/
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

      display.getDescriptionField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableUpdateButton(event.getValue() != null
               && !event.getValue().equals(applicationInfo.getDescription()));
         }
      });

      display.getUpdateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doUpdate();
         }
      });
   }

   public void doUpdate()
   {
      UpdateApplicationRequest updateApplicationRequest =
         AWSExtension.AUTO_BEAN_FACTORY.updateApplicationRequest().as();
      updateApplicationRequest.setApplicationName(applicationInfo.getName());
      updateApplicationRequest.setDescription(display.getDescriptionField().getValue());

      AutoBean<ApplicationInfo> autoBean = AWSExtension.AUTO_BEAN_FACTORY.applicationInfo();

      try
      {
         BeanstalkClientService.getInstance().updateApplication(
            vfsId,
            projectId,
            updateApplicationRequest,
            new BeanstalkAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
               new LoggedInHandler()
               {

                  @Override
                  public void onLoggedIn()
                  {
                     doUpdate();
                  }
               })
            {

               @Override
               protected void processFail(Throwable exception)
               {
               /* TODO  String message = AWSExtension.LOCALIZATION_CONSTANT.updateApplicationFailed(applicationInfo.getName());
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  IDE.fireEvent(new OutputEvent(message, Type.ERROR));*/
               }

               @Override
               protected void onSuccess(ApplicationInfo result)
               {
                  if (applicationUpdatedHandler != null)
                  {
                     applicationUpdatedHandler.onApplicationUpdated(result);
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
