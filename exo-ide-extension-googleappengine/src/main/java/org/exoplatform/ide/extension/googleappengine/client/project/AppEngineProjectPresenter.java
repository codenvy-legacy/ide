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
package org.exoplatform.ide.extension.googleappengine.client.project;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.deploy.DeployApplicationEvent;
import org.exoplatform.ide.extension.googleappengine.client.dos.UpdateDosEvent;
import org.exoplatform.ide.extension.googleappengine.client.logs.ShowLogsEvent;
import org.exoplatform.ide.extension.googleappengine.client.pagespeed.UpdatePageSpeedEvent;
import org.exoplatform.ide.extension.googleappengine.client.queues.UpdateQueuesEvent;
import org.exoplatform.ide.extension.googleappengine.client.rollback.RollbackUpdateEvent;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 22, 2012 5:14:40 PM anya $
 * 
 */
public class AppEngineProjectPresenter extends GoogleAppEnginePresenter implements ManageAppEngineProjectHandler,
   ViewClosedHandler
{

   interface Display extends IsView
   {
      HasClickHandlers getCloseButton();

      HasClickHandlers getConfigureBackendButton();

      HasClickHandlers getDeleteBackendButton();

      HasClickHandlers getUpdateBackendButton();

      HasClickHandlers getRollbackBackendButton();

      HasClickHandlers getRollbackAllBackendsButton();

      HasClickHandlers getLogsButton();

      HasClickHandlers getUpdateButton();

      HasClickHandlers getRollbackButton();

      HasClickHandlers getUpdateCronButton();

      HasClickHandlers getUpdateDosButton();

      HasClickHandlers getUpdateIndexesButton();

      HasClickHandlers getVacuumIndexesButton();

      HasClickHandlers getUpdatePageSpeedButton();

      HasClickHandlers getUpdateQueuesButton();
   }

   private Display display;

   public AppEngineProjectPresenter()
   {
      IDE.getInstance().addControl(new AppEngineProjectControl());

      IDE.addHandler(ManageAppEngineProjectEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getUpdateButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            updateApplication();
         }
      });

      display.getRollbackButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            rollbackApplicationUpdate();
         }
      });

      display.getLogsButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            showLogs();
         }
      });

      display.getUpdatePageSpeedButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            updatePageSpeed();
         }
      });

      display.getUpdateDosButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            updateDos();
         }
      });

      display.getUpdateQueuesButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            updateQueues();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.project.ManageAppEngineProjectHandler#onManageAppEngineProject(org.exoplatform.ide.extension.googleappengine.client.project.ManageAppEngineProjectEvent)
    */
   @Override
   public void onManageAppEngineProject(ManageAppEngineProjectEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }
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

   public void updateApplication()
   {
      IDE.fireEvent(new DeployApplicationEvent());
   }

   public void rollbackApplicationUpdate()
   {
      IDE.fireEvent(new RollbackUpdateEvent());
   }

   public void showLogs()
   {
      IDE.getInstance().closeView(display.asView().getId());
      IDE.fireEvent(new ShowLogsEvent());
   }

   public void updatePageSpeed()
   {
      IDE.fireEvent(new UpdatePageSpeedEvent());
   }

   public void updateQueues()
   {
      IDE.fireEvent(new UpdateQueuesEvent());
   }

   public void updateDos()
   {
      IDE.fireEvent(new UpdateDosEvent());
   }
}
