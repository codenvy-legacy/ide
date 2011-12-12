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
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Presenter for getting and displaying application's information.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 1, 2011 11:32:37 AM anya $
 *
 */
public class ApplicationInfoPresenter extends GitPresenter implements ShowApplicationInfoHandler, ViewClosedHandler,
   LoggedInHandler
{
   /**
    * Properties order to be displayed.
    */
   private static List<String> order;

   /**
    * Set the properties order.
    */
   static
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
    * @param eventBus events handler
    */
   public ApplicationInfoPresenter()
   {
      IDE.addHandler(ShowApplicationInfoEvent.TYPE, this);
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
    * @see org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoHandler#onShowApplicationInfo(org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoEvent)
    */
   @Override
   public void onShowApplicationInfo(ShowApplicationInfoEvent event)
   {
      if (makeSelectionCheck())
      {
         getApplicationInfo();
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

   /**
    * Get application's information.
    */
   public void getApplicationInfo()
   {
      final String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      try
      {
         HerokuClientService.getInstance().getApplicationInfo(null, vfs.getId(), projectId, false,
            new HerokuAsyncRequestCallback(this)
            {

               @Override
               protected void onSuccess(List<Property> properties)
               {
                  System.out
                     .println("ApplicationInfoPresenter.getApplicationInfo().new HerokuAsyncRequestCallback() {...}.onSuccess()");
                  if (display == null)
                  {
                     display = GWT.create(Display.class);
                     bindDisplay();
                     IDE.getInstance().openView(display.asView());
                  }
                  //Make first letter of property name to be in upper case.
                  for (Property property : properties)
                  {
                     String name =
                        (property.getName().length() > 1)
                           ? (property.getName().substring(0, 1).toUpperCase() + property.getName().substring(1))
                           : property.getName().toUpperCase();
                     property.setName(name);
                  }
                  Collections.sort(properties, new PropertiesComparator());
                  display.getApplicationInfoGrid().setValue(properties);
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
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
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getApplicationInfo();
      }
   }
}
