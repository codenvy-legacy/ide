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
package org.exoplatform.ide.extension.cloudbees.client.list;

import com.google.gwt.event.shared.HandlerRegistration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfo;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;

import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:  Sep 21, 2011 evgen $
 *
 */
public class ApplicationListPresenter implements ViewClosedHandler, ShowApplicationListHandler, OutputHandler
{
   public interface Display extends IsView
   {
      String ID = "ideCloudBeesAppListView";

      HasClickHandlers getOkButton();

      HasApplicationListActions getAppListGrid();
   }

   private HandlerManager evenBus;

   private Display display;

   private HandlerRegistration outputHandler;

   /**
    * @param evenBus
    */
   public ApplicationListPresenter(HandlerManager evenBus)
   {
      super();
      this.evenBus = evenBus;
      evenBus.addHandler(ViewClosedEvent.TYPE, this);
      evenBus.addHandler(ShowApplicationListEvent.TYPE, this);
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

   private void bind()
   {
      display.getOkButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(Display.ID);
         }
      });
      display.getAppListGrid().addInfoHandler(new SelectionHandler<ApplicationInfo>()
      {

         @Override
         public void onSelection(SelectionEvent<ApplicationInfo> event)
         {
            outputHandler = evenBus.addHandler(OutputEvent.TYPE, ApplicationListPresenter.this);
            evenBus.fireEvent(new ApplicationInfoEvent(event.getSelectedItem()));
         }
      });

      display.getAppListGrid().addDeleteHandler(new SelectionHandler<ApplicationInfo>()
      {

         @Override
         public void onSelection(SelectionEvent<ApplicationInfo> event)
         {
            evenBus.fireEvent(new DeleteApplicationEvent(event.getSelectedItem().getId(), event.getSelectedItem()
               .getTitle()));
         }
      });

      getOrUpdateAppList();
   }

   /**
    * 
    */
   private void getOrUpdateAppList()
   {
      CloudBeesClientService.getInstance().applicationList(
         new CloudBeesAsyncRequestCallback<List<ApplicationInfo>>(evenBus, new LoggedInHandler()
         {

            @Override
            public void onLoggedIn()
            {
               getOrUpdateAppList();
            }
         }, null)
         {

            @Override
            protected void onSuccess(List<ApplicationInfo> result)
            {
               display.getAppListGrid().setValue(result);
            }
         });
   }

   /**
    * @see org.exoplatform.ide.extension.cloudbees.client.list.ShowApplicationListHandler#onShowApplicationList(org.exoplatform.ide.extension.cloudbees.client.list.ShowApplicationListEvent)
    */
   @Override
   public void onShowApplicationList(ShowApplicationListEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bind();
      }
      else
      {
         display.asView().activate();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.output.event.OutputHandler#onOutput(org.exoplatform.ide.client.framework.output.event.OutputEvent)
    */
   @Override
   public void onOutput(OutputEvent event)
   {
      outputHandler.removeHandler();
      getOrUpdateAppList();
   }
}
