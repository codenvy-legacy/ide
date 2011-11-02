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
package org.exoplatform.ide.extension.cloudfoundry.client.info;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

/**
 * Presenter for showing application info.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationInfoPresenter.java Jun 30, 2011 5:02:31 PM vereshchaka $
 */
public class ApplicationInfoPresenter extends GitPresenter implements ApplicationInfoHandler, ViewClosedHandler
{

   interface Display extends IsView
   {
      HasClickHandlers getOkButton();

      ListGridItem<String> getApplicationUrisGrid();

      ListGridItem<String> getApplicationServicesGrid();

      ListGridItem<String> getApplicationEnvironmentsGrid();

      void setName(String text);

      void setState(String text);

      void setInstances(String text);

      void setVersion(String text);

      void setDisk(String text);

      void setMemory(String text);

      void setStack(String text);

      void setModel(String text);
   }

   private Display display;

   /**
    * @param eventBus events handler
    */
   public ApplicationInfoPresenter()
   {
      IDE.addHandler(ApplicationInfoEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
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
    * @see org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoHandler#onShowApplicationInfo(org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoEvent)
    */
   @Override
   public void onShowApplicationInfo(ApplicationInfoEvent event)
   {
      if (makeSelectionCheck())
      {
         showApplicationInfo(((ItemContext)selectedItems.get(0)).getProject().getId());
      }
   }

   private void showApplicationInfo(final String projectId)
   {
      CloudFoundryClientService.getInstance().getApplicationInfo(vfs.getId(), projectId, null, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(IDE.eventBus(), new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               showApplicationInfo(projectId);
            }
         }, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               if (display == null)
               {
                  display = GWT.create(Display.class);
                  bindDisplay();
                  display.setName(result.getName());
                  display.setState(result.getState());
                  display.setInstances(String.valueOf(result.getInstances()));
                  display.setVersion(result.getVersion());
                  display.setDisk(String.valueOf(result.getResources().getDisk()));
                  display.setMemory(String.valueOf(result.getResources().getMemory()) + "MB");
                  display.setModel(String.valueOf(result.getStaging().getModel()));
                  display.setStack(String.valueOf(result.getStaging().getStack()));
                  display.getApplicationUrisGrid().setValue(result.getUris());
                  display.getApplicationServicesGrid().setValue(result.getServices());
                  display.getApplicationEnvironmentsGrid().setValue(result.getEnv());
                  IDE.getInstance().openView(display.asView());
               }
            }
         });
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
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

}
