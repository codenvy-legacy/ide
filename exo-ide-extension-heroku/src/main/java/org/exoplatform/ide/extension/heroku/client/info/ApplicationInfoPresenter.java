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
package org.exoplatform.ide.extension.heroku.client.info;

import com.google.gwt.core.client.GWT;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Presenter for getting and displaying application's information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 1, 2011 11:32:37 AM anya $
 *
 */
public class ApplicationInfoPresenter implements ShowApplicationInfoHandler, ViewClosedHandler, ItemsSelectedHandler,
   LoggedInHandler
{
   /**
    * Properties order to be displayed.
    */
   private List<String> order;

   /**
    * Set the properties order.
    */
   private void initPropertiesOrder()
   {
      order = new ArrayList<String>();

      order.add("Name");
      order.add("WebUrl");
      order.add("GitUrl");
      order.add("Owner");
      order.add("DomainName");
      order.add("Dynos");
      order.add("RepoSize");
      order.add("DatabaseSize");
      order.add("SlugSize");
      order.add("Stack");
      order.add("Workers");
   } 
   
   /**
    * Properties order comparator. 
    * 
    */
   private class PropertiesComparator implements Comparator<Property>
   {
      /**
       * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
       */
      @Override
      public int compare(Property p1, Property p2)
      {
         Integer index1 = order.indexOf(p1.getName());
         Integer index2 = order.indexOf(p2.getName());
         
         if (index1 == -1 || index2 == -1)
            return 0;

         return index1.compareTo(index2);
      }
      
   }
   
   interface Display extends IsView
   {
      HasClickHandlers getOkButton();

      ListGridItem<Property> getApplicationInfoGrid();
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
    * Git working directory.
    */
   private String workDir;

   /**
    * @param eventBus events handler
    */
   public ApplicationInfoPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      initPropertiesOrder();
      
      eventBus.addHandler(ShowApplicationInfoEvent.TYPE, this);
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
    * @see org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoHandler#onShowApplicationInfo(org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoEvent)
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
    * Get working directory.
    */
   protected void getWorkDir()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
      {
         Dialogs.getInstance().showInfo(Messages.SELECTED_ITEMS_FAIL);
         return;
      }

      //First get the working directory of the repository if exists:
      GitClientService.getInstance().getWorkDir(selectedItems.get(0).getHref(),
         new AsyncRequestCallback<WorkDirResponse>()
         {
            @Override
            protected void onSuccess(WorkDirResponse result)
            {
               workDir = result.getWorkDir();
               workDir = (workDir.endsWith("/.git")) ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;
               getApplicationInfo();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError(Messages.NOT_GIT_REPOSITORY);
            }
         });
   }

   /**
    * Get application's information.
    */
   public void getApplicationInfo()
   {
      HerokuClientService.getInstance().getApplicationInfo(workDir, null, false,
         new HerokuAsyncRequestCallback(eventBus, this)
         {

            @Override
            protected void onSuccess(HashMap<String, String> result)
            {
               if (display == null)
               {
                  display = GWT.create(Display.class);
                  bindDisplay();
                  IDE.getInstance().openView(display.asView());
               }

               List<Property> properties = new ArrayList<Property>();
               for (String key : result.keySet())
               {
                 String name = (key.length() > 1) ? (key.substring(0, 1).toUpperCase() + key.substring(1)) : key.toUpperCase();
                  properties.add(new Property(name, result.get(key)));
               }
               Collections.sort(properties, new PropertiesComparator());
               display.getApplicationInfoGrid().setValue(properties);
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
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
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
}
