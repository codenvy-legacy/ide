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
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;

import java.util.ArrayList;
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
public class ApplicationInfoPresenter implements ApplicationInfoHandler, ViewClosedHandler, ItemsSelectedHandler
{
   
   interface Display extends IsView
   {
      HasClickHandlers getOkButton();

      ListGridItem<Entry<String, String>> getApplicationInfoGrid();
   }
   
   private Display display;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items.
    */
   private List<Item> selectedItems;

   /**
    * @param eventBus events handler
    */
   public ApplicationInfoPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ApplicationInfoEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
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
      String workDir = selectedItems.get(0).getHref();
      showApplicationInfo(workDir);
   }
   
   private void showApplicationInfo(final String workDir)
   {
      CloudBeesClientService.getInstance().getApplicationInfo(workDir, null,
         new CloudBeesAsyncRequestCallback<Map<String, String>>(eventBus, new LoggedInHandler()
         {
            @Override
            public void onLoggedIn()
            {
               showApplicationInfo(workDir);
            }
         }, null)
         {
            @Override
            protected void onSuccess(Map<String, String> result)
            {
               if (display == null)
               {
                  display = GWT.create(Display.class);
                  bindDisplay();
                  IDE.getInstance().openView(display.asView());
               }
               Iterator<Entry<String, String>> it = result.entrySet().iterator();
               List<Entry<String, String>> valueList = new ArrayList<Map.Entry<String,String>>();
               while (it.hasNext())
               {
                  valueList.add(it.next());
               }
               display.getApplicationInfoGrid().setValue(valueList);
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
