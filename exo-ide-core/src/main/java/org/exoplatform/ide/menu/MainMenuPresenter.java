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
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.core.event.ExpressionsChangedEvent;
import org.exoplatform.ide.core.event.ExpressionsChangedHandler;
import org.exoplatform.ide.core.expressions.Expression;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonIntegerMap;
import org.exoplatform.ide.json.JsonIntegerMap.IterationCallback;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.presenter.Presenter;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class MainMenuPresenter implements Presenter
{
   private final MenuBar parentMenuBar;

   private final JsonStringMap<MenuItem> menuItems;

   /** Map Expression ID <--> MenuItemPath */
   private final JsonIntegerMap<JsonArray<MenuItem>> visibileWhenExpressions;

   /** Map Expression ID <--> MenuItemPath */
   private final JsonIntegerMap<JsonArray<MenuItem>> enabledWhenExpressions;

   private final EventBus eventBus;

   @Inject
   public MainMenuPresenter(EventBus eventBus)
   {
      this.eventBus = eventBus;
      this.parentMenuBar = new MenuBar();
      this.parentMenuBar.setAnimationEnabled(true);
      this.parentMenuBar.setAutoOpen(true);
      this.menuItems = JsonCollections.createStringMap();
      this.visibileWhenExpressions = JsonCollections.createIntegerMap();
      this.enabledWhenExpressions = JsonCollections.createIntegerMap();
      bind();
   }

   private void bind()
   {
      eventBus.addHandler(ExpressionsChangedEvent.TYPE, new ExpressionsChangedHandler()
      {
         @Override
         public void onExpressionsChanged(ExpressionsChangedEvent event)
         {
            JsonIntegerMap<Boolean> changedExpressions = event.getChangedExpressions();
            changedExpressions.iterate(new IterationCallback<Boolean>()
            {
               @Override
               public void onIteration(int key, Boolean val)
               {
                  // get the list of MenuItems registered with this expression
                  if (visibileWhenExpressions.hasKey(key))
                  {
                     JsonArray<MenuItem> menuItems = visibileWhenExpressions.get(key);
                     for (int i = 0; i < menuItems.size(); i++)
                     {
                        MenuItem menuItem = menuItems.get(i);
                        menuItem.setVisible(val);
                     }
                  }
                  // get the list of MenuItems registered with this expression
                  if (enabledWhenExpressions.hasKey(key))
                  {
                     JsonArray<MenuItem> menuItems = enabledWhenExpressions.get(key);
                     for (int i = 0; i < menuItems.size(); i++)
                     {
                        MenuItem menuItem = menuItems.get(i);
                        menuItem.setEnabled(val);
                     }
                  }

               }
            });
         }
      });
   }

   /**
    * @param path
    * @param command
    */
   public void addMenuItem(String path, Command command)
   {
      addMenuItem(path, command, null, null);
   }

   /**
    * @param path
    * @param command
    * @param visibilityExpression Value of this Core Expression is used to set Visible state of the menu item
    * @param enabledExpression Value of this Core Expression is used to set Enabled state of the menu item
    */
   public void addMenuItem(String path, Command command, Expression visibileWhen, Expression enabledWhen)
   {
      MenuPath menuPath = new MenuPath(path);
      MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, menuPath.getSize() - 1);

      if (command instanceof ExtendedCommand)
      {
         // add icon
      }
      MenuItem newItem = dstMenuBar.addItem(menuPath.getPathElementAt(menuPath.getSize() - 1), command);
      menuItems.put(path, newItem);
      // put MenuItem in relation to Expressions
      if (visibileWhen != null)
      {
         newItem.setVisible(visibileWhen.getValue());
         if (!visibileWhenExpressions.hasKey(visibileWhen.getId()))
         {
            visibileWhenExpressions.put(visibileWhen.getId(), JsonCollections.<MenuItem> createArray());
         }
         visibileWhenExpressions.get(visibileWhen.getId()).add(newItem);
      }
      if (enabledWhen != null)
      {
         newItem.setEnabled(enabledWhen.getValue());
         if (!enabledWhenExpressions.hasKey(enabledWhen.getId()))
         {
            enabledWhenExpressions.put(enabledWhen.getId(), JsonCollections.<MenuItem> createArray());
         }
         enabledWhenExpressions.get(enabledWhen.getId()).add(newItem);
      }
   }

   // Internal methods
   // ========================================================================

   /**
    * Recursively 
    * 
    * @param menuPath
    * @param depth
    * @return
    */
   private MenuBar getOrCreateParentMenuBar(MenuPath menuPath, int depth)
   {
      if (depth == 0)
      {
         return parentMenuBar;
      }
      else
      {
         // try to get parent
         MenuItem menuItem = menuItems.get(menuPath.getParentPath(depth));
         if (menuItem != null)
         {
            MenuBar subMenu = menuItem.getSubMenu();
            if (subMenu == null)
            {
               subMenu = createMenuBar();
               menuItem.setSubMenu(subMenu);
            }
            return subMenu;
         }
         else
         {
            MenuBar dstMenuBar = getOrCreateParentMenuBar(menuPath, depth - 1);
            MenuItem newItem = dstMenuBar.addItem(menuPath.getPathElementAt(depth - 1), createMenuBar());
            menuItems.put(menuPath.getParentPath(depth), newItem);
            return newItem.getSubMenu();
         }
      }
   }

   private MenuBar createMenuBar()
   {
      MenuBar menuBar = new MenuBar(true);
      menuBar.setAnimationEnabled(true);
      menuBar.setAutoOpen(true);
      return menuBar;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void go(HasWidgets container)
   {
      container.add(parentMenuBar);
   }
}
