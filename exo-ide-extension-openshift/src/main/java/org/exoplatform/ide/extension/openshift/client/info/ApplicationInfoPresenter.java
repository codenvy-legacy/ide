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
package org.exoplatform.ide.extension.openshift.client.info;

import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.DateTimeFormat.PredefinedFormat;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.openshift.client.OpenShiftClientService;
import org.exoplatform.ide.extension.openshift.client.OpenShiftExtension;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.openshift.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.openshift.client.login.LoginEvent;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.git.client.GitPresenter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Presenter for getting and displaying application's information.
 * The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 1, 2011 11:32:37 AM anya $
 *
 */
public class ApplicationInfoPresenter extends GitPresenter implements ShowApplicationInfoHandler, ViewClosedHandler,
   LoggedInHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getOkButton();

      ListGridItem<Property> getApplicationInfoGrid();
   }

   private Display display;

   /**
    * @param eventBus events handler
    */
   public ApplicationInfoPresenter(HandlerManager eventBus)
   {
      super(eventBus);

      eventBus.addHandler(ShowApplicationInfoEvent.TYPE, this);
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
   }

   /**
    * @see org.exoplatform.ide.extension.openshift.client.info.ShowApplicationInfoHandler#onShowApplicationInfo(org.exoplatform.ide.extension.openshift.client.info.ShowApplicationInfoEvent)
    */
   @Override
   public void onShowApplicationInfo(ShowApplicationInfoEvent event)
   {
      getWorkDir();
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
    * Get application's information.
    */
   public void getApplicationInfo()
   {
      OpenShiftClientService.getInstance().getApplicationInfo(null, workDir, new AsyncRequestCallback<AppInfo>()
      {

         @Override
         protected void onSuccess(AppInfo result)
         {
            if (display == null)
            {
               display = GWT.create(Display.class);
               bindDisplay();
               IDE.getInstance().openView(display.asView());
            }
            List<Property> properties = new ArrayList<Property>();
            properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationName(), result.getName()));
            properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationType(), result.getType()));
            properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationPublicUrl(), result
               .getPublicUrl()));
            properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationGitUrl(), result
               .getGitUrl()));
            String time =
               DateTimeFormat.getFormat(PredefinedFormat.DATE_TIME_MEDIUM).format(new Date(result.getCreationTime()));
            properties.add(new Property(OpenShiftExtension.LOCALIZATION_CONSTANT.applicationCreationTime(), time));
            display.getApplicationInfoGrid().setValue(properties);
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
            super.onFailure(exception);
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
         getApplicationInfo();
      }
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      getApplicationInfo();
   }
}
