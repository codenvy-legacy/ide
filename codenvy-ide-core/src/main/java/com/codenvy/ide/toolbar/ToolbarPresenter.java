/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.toolbar;

import com.codenvy.ide.api.event.ExpressionsChangedEvent;
import com.codenvy.ide.api.event.ExpressionsChangedHandler;

import com.codenvy.ide.api.expressions.Expression;
import com.codenvy.ide.api.expressions.ToggleStateExpression;

import com.codenvy.ide.api.mvp.Presenter;

import com.codenvy.ide.api.ui.menu.ToolbarAgent;

import com.codenvy.ide.api.ui.menu.ToggleCommand;

import com.codenvy.ide.api.ui.menu.ExtendedCommand;


import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonIntegerMap;
import com.codenvy.ide.json.JsonIntegerMap.IterationCallback;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * Manages Toolbar items, changes item state and other.
 * 
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class ToolbarPresenter implements Presenter, ToolbarAgent, ToolbarView.ActionDelegate, ExpressionsChangedHandler
{
   private ToolbarView view;

   private final EventBus eventBus;

   /**
    * Map Expression ID <--> MenuItemPath
    */
   private final JsonIntegerMap<JsonArray<String>> visibileWhenExpressions;

   /**
    * Map Expression ID <--> MenuItemPath
    */
   private final JsonIntegerMap<JsonArray<String>> enabledWhenExpressions;

   /**
    * Map Expression ID <--> MenuItemPath
    */
   private final JsonIntegerMap<JsonArray<String>> selectedWhenExpressions;

   /**
    * Create presenter.
    * 
    * @param view
    * @param eventBus
    */
   @Inject
   public ToolbarPresenter(ToolbarView view, EventBus eventBus)
   {
      this.view = view;
      view.setDelegate(this);

      this.eventBus = eventBus;

      this.visibileWhenExpressions = JsonCollections.createIntegerMap();
      this.enabledWhenExpressions = JsonCollections.createIntegerMap();
      this.selectedWhenExpressions = JsonCollections.createIntegerMap();

      bind();
   }

   /**
    * Bind event handlers to Event Bus
    */
   private void bind()
   {
      // Listen to the Expression changed event to update toolbar item state
      eventBus.addHandler(ExpressionsChangedEvent.TYPE, this);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void go(AcceptsOneWidget container)
   {
      container.setWidget(view);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addItem(String path, ExtendedCommand command) throws IllegalStateException
   {
      if (command == null)
      {
         addItem(path, command, null, null);
      }
      else
      {
         addItem(path, command, command.inContext(), command.canExecute());
      }
   }

   /**
    * Adds item to Toolbar.
    * 
    * @param path
    * @param command
    * @param visible
    * @param enabled
    */
   private void addItem(String path, ExtendedCommand command, Expression visibleWhen, Expression enabledWhen)
   {
      view.addItem(path, command, visibleWhen == null ? true : visibleWhen.getValue(), enabledWhen == null ? true
         : enabledWhen.getValue());

      // put Toolbar item in relation to Expressions
      addExpression(visibileWhenExpressions, visibleWhen, path);
      addExpression(enabledWhenExpressions, enabledWhen, path);
   }

   /**
    * Adds expression into map of expressions. 
    * 
    * @param expressions
    * @param expression
    * @param path
    */
   private void addExpression(JsonIntegerMap<JsonArray<String>> expressions, Expression expression, String path)
   {
      if (expression != null)
      {
         if (!expressions.hasKey(expression.getId()))
         {
            expressions.put(expression.getId(), JsonCollections.<String> createArray());
         }
         expressions.get(expression.getId()).add(path);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addToggleItem(String path, ToggleCommand command) throws IllegalStateException
   {
      if (command == null)
      {
         addToggleItem(path, command, null, null, null);
      }
      else
      {
         addToggleItem(path, command, command.inContext(), command.canExecute(), command.getState());
      }
   }

   /**
    * Adds toggle item to Toolbar.
    * 
    * @param path
    * @param command
    * @param visible
    * @param enabled
    */
   private void addToggleItem(String path, ToggleCommand command, Expression visibleWhen, Expression enabledWhen,
      ToggleStateExpression selectedWhen)
   {
      view.addToggleItem(path, command, visibleWhen == null ? true : visibleWhen.getValue(), enabledWhen == null ? true
         : enabledWhen.getValue(), selectedWhen == null ? true : selectedWhen.getValue());

      // put Toolbar item in relation to Expressions
      addExpression(visibileWhenExpressions, visibleWhen, path);
      addExpression(enabledWhenExpressions, enabledWhen, path);
      addExpression(selectedWhenExpressions, selectedWhen, path);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addDropDownItem(String path, ImageResource icon, String tooltip) throws IllegalStateException
   {
      addDropDownItem(path, icon, tooltip, null, null);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void addDropDownItem(String path, ImageResource icon, String tooltip, Expression visibleWhen,
      Expression enabledWhen) throws IllegalStateException
   {
      view.addDropDownItem(path, icon, tooltip, visibleWhen == null ? true : visibleWhen.getValue(),
         enabledWhen == null ? true : enabledWhen.getValue());

      // put Toolbar item in relation to Expressions
      addExpression(visibileWhenExpressions, visibleWhen, path);
      addExpression(enabledWhenExpressions, enabledWhen, path);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void copyMainMenuItem(String toolbarPath, String mainMenuPath) throws IllegalStateException
   {
      view.copyMainMenuItem(toolbarPath, mainMenuPath);
   }

   /**
    * {@inheritDoc}
    */
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
            // get the list of Toolbar items registered with this expression
            if (visibileWhenExpressions.hasKey(key))
            {
               JsonArray<String> itemsPath = visibileWhenExpressions.get(key);
               for (int i = 0; i < itemsPath.size(); i++)
               {
                  view.setVisible(itemsPath.get(i), val);
               }
            }

            // get the list of Toolbar items registered with this expression
            if (enabledWhenExpressions.hasKey(key))
            {
               JsonArray<String> itemsPath = enabledWhenExpressions.get(key);
               for (int i = 0; i < itemsPath.size(); i++)
               {
                  view.setEnabled(itemsPath.get(i), val);
               }
            }

            // get Toolbar item registered with this expression
            if (selectedWhenExpressions.hasKey(key))
            {
               JsonArray<String> itemsPath = selectedWhenExpressions.get(key);
               for (int i = 0; i < itemsPath.size(); i++)
               {
                  view.setSelected(itemsPath.get(i), val);
               }
            }
         }
      });
   }
}