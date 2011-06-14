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
package org.exoplatform.ide.extension.openshift.client.domain;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 7, 2011 3:49:41 PM anya $
 *
 */
public class CreateDomainPresenter implements ViewClosedHandler, CreateDomainHandler, LoggedInHandler
{
   interface Display extends IsView
   {
      /**
       * Get create button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCreateButton();

      /**
       * Get cancel button's click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Get domain name field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getDomainNameField();

      /**
       * Change the enable state of the create button.
       * 
       * @param enable
       */
      void enableCreateButton(boolean enable);

      /**
       * Give focus to domain name field.
       */
      void focusInDomainNameField();
   }

   private Display display;

   private HandlerManager eventBus;

   /**
    * @param eventBus events handler
    */
   public CreateDomainPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(CreateDomainEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getCreateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            createDomain();
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

      display.getDomainNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean isNotEmpty = (event.getValue() != null && event.getValue().trim().length() > 0);
            display.enableCreateButton(isNotEmpty);
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
    * @see org.exoplatform.ide.extension.openshift.client.domain.CreateDomainHandler#onCreateDomain(org.exoplatform.ide.extension.openshift.client.domain.CreateDomainEvent)
    */
   @Override
   public void onCreateDomain(CreateDomainEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.enableCreateButton(false);
         display.focusInDomainNameField();
      }
   }

   protected void createDomain()
   {
      final String domainName =
         (display.getDomainNameField().getValue() != null) ? display.getDomainNameField().getValue().trim() : display
            .getDomainNameField().getValue();
      if (domainName == null || domainName.length() == 0)
      {
         return;
      }

      OpenShiftClientService.getInstance().createDomain(domainName, true, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            eventBus.fireEvent(new OutputEvent(
               OpenShiftExtension.LOCALIZATION_CONSTANT.createDomainSuccess(domainName), Type.INFO));
            IDE.getInstance().closeView(display.asView().getId());
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
               if (401 == serverException.getHTTPStatus())
               {
                  addLoggedInHandler();
                  eventBus.fireEvent(new LoginEvent());
                  return;
               }
            }
            eventBus.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT.createDomainFail(domainName)));
         }
      });
   }

   /**
    * Register {@link LoggedInHandler} handler.
    */   
   protected void addLoggedInHandler()
   {
      eventBus.addHandler(LoggedInEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      eventBus.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         createDomain();
      }
   }
}
