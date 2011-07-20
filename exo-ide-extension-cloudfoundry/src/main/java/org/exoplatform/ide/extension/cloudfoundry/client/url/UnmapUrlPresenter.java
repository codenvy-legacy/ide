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
package org.exoplatform.ide.extension.cloudfoundry.client.url;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for unmaping (unregistering) URLs from application.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: UnmapUrlPresenter.java Jul 19, 2011 2:31:19 PM vereshchaka $
 *
 */
public class UnmapUrlPresenter implements ItemsSelectedHandler, UnmapUrlHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getCloseButton();
      
      HasClickHandlers getDeleteButton();

      ListGridItem<UrlData> getRegisteredUrlsGrid();
   }
   
   private CloudFoundryLocalizationConstant localeBundle = CloudFoundryExtension.LOCALIZATION_CONSTANT;
   
   private Display display;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;
   
   /**
    * Selected items in navigation tree.
    */
   private List<Item> selectedItems;
   
   /**
    * List of urls to unregister.
    */
   private List<UrlData> unregisterUrlData;
   
   /**
    * List of all urls. Unregistered urls will be removed from this list.
    */
   private List<UrlData> allUrlData;
   
   public UnmapUrlPresenter(HandlerManager eventbus)
   {
      this.eventBus = eventbus;
      
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(UnmapUrlEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }
   
   public void bindDisplay(List<String> registeredUrls)
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
      
      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            confirmUnregisteringUrls();
         }
      });
      
      List<UrlData> urlData = new ArrayList<UrlData>();
      for (String url : registeredUrls)
      {
         urlData.add(new UrlData(url, false));
      }
      
      display.getRegisteredUrlsGrid().setValue(urlData);
   }
   
   /**
    * Show confirmation dialog to unregister urls.
    */
   private void confirmUnregisteringUrls()
   {
      allUrlData = display.getRegisteredUrlsGrid().getValue();
      unregisterUrlData = new ArrayList<UrlData>();
      
      for (UrlData obj : allUrlData)
      {
         if (obj.isChecked())
         {
            unregisterUrlData.add(obj);
         }
      }
      
      Dialogs.getInstance().ask(localeBundle.unmapUrlConfirmationDialogTitle(),
         localeBundle.unmapUrlConfirmationDialogMessage(), new BooleanValueReceivedHandler()
         {
            @Override
            public void booleanValueReceived(Boolean value)
            {
               if (value == null || !value)
                  return;
               
               unregisterUrls();
            }
         });
   }
   
   /**
    * Update list grid after unregistering selected urls.
    */
   private void updateRegisteredUrlsValue()
   {
      display.getRegisteredUrlsGrid().setValue(allUrlData);
   }
   
   /**
    * Unregister selected urls from application.
    */
   private void unregisterUrls()
   {
      if (unregisterUrlData.isEmpty())
      {
         updateRegisteredUrlsValue();
         return;
      }

      final UrlData urlData = unregisterUrlData.get(0);
      CloudFoundryClientService.getInstance().unmapUrl(getWorkDir(), null, urlData.getUrl(),
         new CloudFoundryAsyncRequestCallback<String>(eventBus, unmapUrlLoggedInHandler, null)
         {
            @Override
            protected void onSuccess(String result)
            {
               unregisterUrlData.remove(urlData);
               allUrlData.remove(urlData);
               unregisterUrls();
            }
         });
   }
   
   /**
    * Logged in hanldler, which will be called after authorization.
    */
   private LoggedInHandler unmapUrlLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         unregisterUrls();
      }
   };
   
   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * Get the url to workdir, using selected items.
    * @return
    */
   private String getWorkDir()
   {
      if (selectedItems.size() == 0)
         return null;
      
      String workDir = selectedItems.get(0).getHref();
      if (selectedItems.get(0) instanceof File)
      {
         workDir = workDir.substring(0, workDir.lastIndexOf("/") + 1);
      }
      return workDir;
   }
   
   LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler()
   {
      @Override
      public void onLoggedIn()
      {
         getAppRegisteredUrls();
      }
   };

   /**
    * @see org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationHandler#onRestartApplication(org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent)
    */
   @Override
   public void onUnmapUrl(UnmapUrlEvent event)
   {
      getAppRegisteredUrls();
   }
   
   private void getAppRegisteredUrls()
   {
      String workDir = getWorkDir();

      CloudFoundryClientService.getInstance().getApplicationInfo(workDir, null,
         new CloudFoundryAsyncRequestCallback<CloudfoundryApplication>(eventBus, null, null)
         {
            @Override
            protected void onSuccess(CloudfoundryApplication result)
            {
               openView(result.getUris());
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
   
   private void openView(List<String> registeredUrls)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay(registeredUrls);
         IDE.getInstance().openView(display.asView());
      }
   }

}
