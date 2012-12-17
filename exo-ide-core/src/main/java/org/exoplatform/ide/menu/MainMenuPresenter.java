/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.menu;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.ui.menu.MainMenuAgent;
import org.exoplatform.ide.core.event.ExpressionsChangedEvent;
import org.exoplatform.ide.core.event.ExpressionsChangedHandler;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonIntegerMap;
import org.exoplatform.ide.json.JsonIntegerMap.IterationCallback;
import org.exoplatform.ide.presenter.Presenter;

/**
 * Manages Main Menu Items, their runtime visibility and enabled state.
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class MainMenuPresenter implements Presenter, MainMenuAgent
{

   /**
    * Main Menu View 
    */
   public interface MainMenuView extends IsWidget
   {
      /**
       * Set Menu Item by given path visible or invisible.
       * 
       * @param path menuItem path
       * @param visible state
       */
      void setVisible(String path, boolean visible);

      /**
       * Set Menu Item by given path enabled or disabled.
       * 
       * @param path menuItem path
       * @param enabled
       */
      void setEnabled(String path, boolean enabled);

      /**
       * Add menu item with the following path, icon, command, visible and enabled states
       * 
       * @param path
       * @param icon
       * @param command
       * @param visible
       * @param enabled
       */
      void addMenuItem(String path, Image icon, Command command, boolean visible, boolean enabled);
   }

   /** Map Expression ID <--> MenuItemPath */
   private final JsonIntegerMap<JsonArray<String>> visibileWhenExpressions;

   /** Map Expression ID <--> MenuItemPath */
   private final JsonIntegerMap<JsonArray<String>> enabledWhenExpressions;

   private final EventBus eventBus;

   private final MainMenuView view;

   /**
    * Main Menu Presenter requires Event Bus to listen to Expression Changed Event
    * and View implementation
    * 
    * @param eventBus
    * @param view
    */
   @Inject
   public MainMenuPresenter(EventBus eventBus, MainMenuView view)
   {
      this.eventBus = eventBus;
      this.view = view;

      this.visibileWhenExpressions = JsonCollections.createIntegerMap();
      this.enabledWhenExpressions = JsonCollections.createIntegerMap();
      bind();
   }

   /**
    * Bind event handlers to Event Bus
    */
   private void bind()
   {
      // Listen to the Expression changed event to update menu item visible and enabled state
      eventBus.addHandler(ExpressionsChangedEvent.TYPE, new MenuItemStateUpdateHandler());
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMenuItem(String path, Command command)
   {
      addMenuItem(path, null, command, null, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMenuItem(String path, ExtendedCommand command)
   {
      if (command == null)
      {
         addMenuItem(path, null, null, null, null);
      }
      else
      {
         addMenuItem(path, command.getIcon(), command, command.visibleWhen(), command.enabledWhen());
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addMenuItem(String path, Image icon, Command command, Expression visibileWhen, Expression enabledWhen)
   {

      view.addMenuItem(path, icon, command, visibileWhen == null ? true : visibileWhen.getValue(),
         enabledWhen == null ? true : enabledWhen.getValue());
      // put MenuItem in relation to Expressions
      if (visibileWhen != null)
      {
         if (!visibileWhenExpressions.hasKey(visibileWhen.getId()))
         {
            visibileWhenExpressions.put(visibileWhen.getId(), JsonCollections.<String> createArray());
         }
         visibileWhenExpressions.get(visibileWhen.getId()).add(path);
      }
      if (enabledWhen != null)
      {
         if (!enabledWhenExpressions.hasKey(enabledWhen.getId()))
         {
            enabledWhenExpressions.put(enabledWhen.getId(), JsonCollections.<String> createArray());
         }
         enabledWhenExpressions.get(enabledWhen.getId()).add(path);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(HasWidgets container)
   {
      container.add(view.asWidget());
   }

   /**
    * Handle {@link ExpressionsChangedEvent} and updated the state of corresponding menu items
    */
   private final class MenuItemStateUpdateHandler implements ExpressionsChangedHandler
   {
      @Override
      public void onExpressionsChanged(ExpressionsChangedEvent event)
      {
         // process changed expression
         JsonIntegerMap<Boolean> changedExpressions = event.getChangedExpressions();
         changedExpressions.iterate(new IterationCallback<Boolean>()
         {
            @Override
            public void onIteration(int key, Boolean val)
            {
               // get the list of MenuItems registered with this expression
               if (visibileWhenExpressions.hasKey(key))
               {
                  JsonArray<String> itemsPath = visibileWhenExpressions.get(key);
                  for (int i = 0; i < itemsPath.size(); i++)
                  {
                     view.setVisible(itemsPath.get(i), val);
                  }
               }
               // get the list of MenuItems registered with this expression
               if (enabledWhenExpressions.hasKey(key))
               {
                  JsonArray<String> itemsPath = enabledWhenExpressions.get(key);
                  for (int i = 0; i < itemsPath.size(); i++)
                  {
                     view.setEnabled(itemsPath.get(i), val);
                  }
               }

            }
         });
      }
   }

}
