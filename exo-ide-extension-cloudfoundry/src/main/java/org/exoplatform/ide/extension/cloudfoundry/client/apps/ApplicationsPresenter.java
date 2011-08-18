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
package org.exoplatform.ide.extension.cloudfoundry.client.apps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Aug 18, 2011 evgen $
 *
 */
public class ApplicationsPresenter implements ViewClosedHandler, ShowApplicationsHandler, OutputHandler
{
   public interface Display extends IsView
   {
      String ID = "ideCloudFoundryApplicationsView";

      HasClickHandlers getOkButton();

      ListGridItem<CloudfoundryApplication> getAppsGrid();

      HasApplicationsActions getActions();
   }

   private Display display;

   /**
    *  
    */
   public ApplicationsPresenter()
   {
      IDE.EVENT_BUS.addHandler(ShowApplicationsEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(OutputEvent.TYPE, this);
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

      display.getActions().addStopApplicationHandler(new SelectionHandler<CloudfoundryApplication>()
      {

         @Override
         public void onSelection(SelectionEvent<CloudfoundryApplication> event)
         {
            IDE.EVENT_BUS.fireEvent(new StopApplicationEvent(event.getSelectedItem().getName()));
         }
      });

      display.getActions().addStartApplicationHandler(new SelectionHandler<CloudfoundryApplication>()
      {

         @Override
         public void onSelection(SelectionEvent<CloudfoundryApplication> event)
         {
            IDE.EVENT_BUS.fireEvent(new StartApplicationEvent(event.getSelectedItem().getName()));
         }
      });

      display.getActions().addRestartApplicationHandler(new SelectionHandler<CloudfoundryApplication>()
      {

         @Override
         public void onSelection(SelectionEvent<CloudfoundryApplication> event)
         {
            IDE.EVENT_BUS.fireEvent(new RestartApplicationEvent(event.getSelectedItem().getName()));
         }
      });

      display.getActions().addDeleteApplicationHandler(new SelectionHandler<CloudfoundryApplication>()
      {

         @Override
         public void onSelection(SelectionEvent<CloudfoundryApplication> event)
         {
            IDE.EVENT_BUS.fireEvent(new DeleteApplicationEvent(event.getSelectedItem().getName()));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ShowApplicationsHandler#onShowApplications(org.exoplatform.ide.extension.cloudfoundry.client.apps.ShowApplicationsEvent)
    */
   @Override
   public void onShowApplications(ShowApplicationsEvent event)
   {
      CloudFoundryClientService.getInstance().getApplicationList(
         new CloudFoundryAsyncRequestCallback<List<CloudfoundryApplication>>(IDE.EVENT_BUS, //
            new LoggedInHandler()//
            {
               @Override
               public void onLoggedIn()
               {
                  onShowApplications(null);
               }
            }, null)
         {

            @Override
            protected void onSuccess(List<CloudfoundryApplication> result)
            {
               if (display == null)
               {
                  display = GWT.create(Display.class);
                  bindDisplay();
                  IDE.getInstance().openView(display.asView());
               }
               display.getAppsGrid().setValue(result);
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
    * @see org.exoplatform.ide.client.framework.output.event.OutputHandler#onOutput(org.exoplatform.ide.client.framework.output.event.OutputEvent)
    */
   @Override
   public void onOutput(OutputEvent event)
   {
      if (display != null)
         onShowApplications(null);
   }

}
