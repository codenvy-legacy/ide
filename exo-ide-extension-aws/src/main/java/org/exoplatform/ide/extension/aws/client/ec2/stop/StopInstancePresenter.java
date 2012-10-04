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
package org.exoplatform.ide.extension.aws.client.ec2.stop;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.aws.client.AWSExtension;
import org.exoplatform.ide.extension.aws.client.ec2.EC2ClientService;

/**
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.com">Artem Zatsarynnyy</a>
 * @version $Id: StopInstancePresenter.java Sep 28, 2012 3:56:46 PM azatsarynnyy $
 *
 */
public class StopInstancePresenter implements StopInstanceHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getOKButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getStopQuestion();

      HasValue<Boolean> getForce();
   }

   private Display display;

   /**
    * Identifier of instance to stop.
    */
   private String instanceId;

   public StopInstancePresenter()
   {
      IDE.addHandler(StopInstanceEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getOKButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            stopInstance();
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
    * @see org.exoplatform.ide.extension.aws.client.ec2.stop.StopInstanceHandler#onStopInstance(org.exoplatform.ide.extension.aws.client.ec2.stop.StopInstanceEvent)
    */
   @Override
   public void onStopInstance(StopInstanceEvent event)
   {
      this.instanceId = event.getInstanceId();
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();
      }
      display.getStopQuestion().setValue(AWSExtension.LOCALIZATION_CONSTANT.stopEC2InstanceQuestion(instanceId));
   }

   /**
    * Stop a specified EC2 instance.
    */
   private void stopInstance()
   {
      try
      {
         EC2ClientService.getInstance().stopInstance(instanceId, display.getForce().getValue(),
            new AsyncRequestCallback<Object>()
            {

               @Override
               protected void onSuccess(Object result)
               {
                  Dialogs.getInstance().showInfo(AWSExtension.LOCALIZATION_CONSTANT.stopEC2InstanceViewTitle(),
                     AWSExtension.LOCALIZATION_CONSTANT.stopInstanceSuccess(instanceId));
                  IDE.fireEvent(new OutputEvent(AWSExtension.LOCALIZATION_CONSTANT.stopInstanceSuccess(instanceId),
                     Type.INFO));
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String message = AWSExtension.LOCALIZATION_CONSTANT.stopInstanceFailed(instanceId);
                  if (exception instanceof ServerException && ((ServerException)exception).getMessage() != null)
                  {
                     message += "<br>" + ((ServerException)exception).getMessage();
                  }
                  Dialogs.getInstance().showError(message);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
      IDE.getInstance().closeView(display.asView().getId());
   }
}
