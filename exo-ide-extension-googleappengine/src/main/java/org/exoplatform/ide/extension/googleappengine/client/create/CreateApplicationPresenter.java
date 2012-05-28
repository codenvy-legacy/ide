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
package org.exoplatform.ide.extension.googleappengine.client.create;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasLoadHandlers;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.util.ProjectResolver;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationEvent;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 21, 2012 2:27:42 PM anya $
 * 
 */
public class CreateApplicationPresenter extends GoogleAppEnginePresenter implements CreateApplicationHandler,
   ViewClosedHandler
{
   interface Display extends IsView
   {
      /**
       * @return {@link String}
       */
      String getAppEngineFrameContent();

      void setAppEngineFrameContent(String content);

      /**
       * @return {@link HasLoadHandlers} handler for iframe loaded event
       */
      HasLoadHandlers getLoadHandler();

      /**
       * @return {@link HasClickHandlers} handler for ready button click
       */
      HasClickHandlers getReadyButton();
      
      /**
       * @return {@link HasClickHandlers} handler for cancel button click
       */
      HasClickHandlers getCancelButton();

      /**
       * @return {@link HasValue} deploy application field value
       */
      HasValue<Boolean> getDeployValue();
   }

   private Display display;
   
   private static final String GOOGLE_APP_ENGINE_URL = "https://appengine.google.com";

   public CreateApplicationPresenter()
   {
      IDE.getInstance().addControl(new CreateApplicationControl());

      IDE.addHandler(CreateApplicationEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getLoadHandler().addLoadHandler(new LoadHandler()
      {

         @Override
         public void onLoad(LoadEvent event)
         {
            /*
             * display.setAppEngineFrameContent(GoogleAppEngineExtension.GAE_LOCALIZATION .createApplicationNotLoggedMessage());
             */
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

      display.getReadyButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            onReady();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationHandler#onCreateApplication(org.exoplatform.ide.extension.googleappengine.client.create.CreateApplicationEvent)
    */
   @Override
   public void onCreateApplication(CreateApplicationEvent event)
   {
      Window.open(GOOGLE_APP_ENGINE_URL, "_blank", null);
    /*  if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }*/
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
    * Perform actions, when user is ready.
    */
   public void onReady()
   {
      if (display.getDeployValue().getValue())
      {
         if (canDeploy())
         {
            IDE.fireEvent(new DeployApplicationEvent());
         }
         else
         {
            Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.createApplicationCannotDeploy());
         }
      }
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * Returns whether deploy can be performed.
    * 
    * @return {@link Boolean} <code>true</code> if can perform deploy to GAE
    */
   private boolean canDeploy()
   {
      return currentProject != null && ProjectResolver.APP_ENGINE_JAVA.equals(currentProject.getProjectType());
   }
}
