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
package org.exoplatform.ide.extension.cloudbees.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesExtension;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

      ListGridItem<Entry<String, String>> getApplicationInfoGrid();
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
      if (event.getAppInfo() != null)
      {
         showAppInfo(event.getAppInfo());
      }
      else if (makeSelectionCheck())
      {
         String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
         showApplicationInfo(projectId);
      }
   }

   private void showApplicationInfo(final String projectId)
   {
      try
      {
         AutoBean<ApplicationInfo> autoBean = CloudBeesExtension.AUTO_BEAN_FACTORY.applicationInfo();
         CloudBeesClientService.getInstance().getApplicationInfo(
            null,
            vfs.getId(),
            projectId,
            new CloudBeesAsyncRequestCallback<ApplicationInfo>(new AutoBeanUnmarshaller<ApplicationInfo>(autoBean),
               new LoggedInHandler()
               {
                  @Override
                  public void onLoggedIn()
                  {
                     showApplicationInfo(projectId);
                  }
               }, null)
            {
               @Override
               protected void onSuccess(ApplicationInfo result)
               {
                  showAppInfo(result);
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   private void showAppInfo(ApplicationInfo appInfo)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
      }

      Map<String, String> map = new HashMap<String, String>();
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridId(), appInfo.getId());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridTitle(), appInfo.getTitle());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridServerPool(), appInfo.getServerPool());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridStatus(), appInfo.getStatus());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridContainer(), appInfo.getContainer());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridIdleTimeout(), appInfo.getIdleTimeout());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridMaxMemory(), appInfo.getMaxMemory());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridSecurityMode(), appInfo.getSecurityMode());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridClusterSize(), appInfo.getClusterSize());
      map.put(CloudBeesExtension.LOCALIZATION_CONSTANT.applicationInfoListGridUrl(), appInfo.getUrl());

      Iterator<Entry<String, String>> it = map.entrySet().iterator();
      List<Entry<String, String>> valueList = new ArrayList<Map.Entry<String, String>>();
      while (it.hasNext())
      {
         valueList.add(it.next());
      }
      display.getApplicationInfoGrid().setValue(valueList);
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
