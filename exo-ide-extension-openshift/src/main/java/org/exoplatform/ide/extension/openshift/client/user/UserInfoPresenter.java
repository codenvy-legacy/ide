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
package org.exoplatform.ide.extension.openshift.client.user;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExceptionThrownEvent;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.info.Property;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 14, 2011 2:38:17 PM anya $
 *
 */
public class UserInfoPresenter implements ShowUserInfoHandler, ViewClosedHandler, LoggedInHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getOkButton();

      HasValue<String> getLoginField();

      HasValue<String> getDomainField();

      ListGridItem<Property> getApplicationInfoGrid();

      ListGridItem<AppInfo> getApplicationGrid();
   }

   private Display display;

   private HandlerManager eventBus;

   /**
    * @param eventBus events handler
    */
   public UserInfoPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ShowUserInfoEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind presenter with display.
    */
   public void bindDisplay()
   {
      display.getOkButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getApplicationGrid().addSelectionHandler(new SelectionHandler<AppInfo>()
      {

         @Override
         public void onSelection(SelectionEvent<AppInfo> event)
         {
            if (event.getSelectedItem() != null)
            {
               displayAppInfo(event.getSelectedItem());
            }
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
    * @see org.exoplatform.ide.extension.openshift.client.user.ShowUserInfoHandler#onShowUserInfo(org.exoplatform.ide.extension.openshift.client.user.ShowUserInfoEvent)
    */
   @Override
   public void onShowUserInfo(ShowUserInfoEvent event)
   {
      getUserInfo();
   }

   protected void getUserInfo()
   {
      OpenShiftClientService.getInstance().getUserInfo(true, new AsyncRequestCallback<RHUserInfo>()
      {

         @Override
         protected void onSuccess(RHUserInfo result)
         {
            if (display == null)
            {
               display = GWT.create(Display.class);
               bindDisplay();
               IDE.getInstance().openView(display.asView());
            }
            display.getLoginField().setValue(result.getRhlogin());
            display.getDomainField().setValue(result.getNamespace());
            display.getApplicationGrid().setValue(result.getApps());
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
            eventBus.fireEvent(new OpenShiftExceptionThrownEvent(exception, OpenShiftExtension.LOCALIZATION_CONSTANT
               .getUserInfoFail()));
         }
      });

   }

   protected void displayAppInfo(AppInfo appInfo)
   {
      List<Property> properties = new ArrayList<Property>();
      properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationName(), appInfo.getName()));
      properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationType(), appInfo.getType()));
      String time =
         DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date(appInfo.getCreationTime()));
      properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationCreationTime(), time));
      display.getApplicationInfoGrid().setValue(properties);
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
         getUserInfo();
      }
   }
}
