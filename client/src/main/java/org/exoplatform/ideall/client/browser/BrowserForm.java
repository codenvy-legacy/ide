/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

package org.exoplatform.ideall.client.browser;

import org.exoplatform.ideall.client.browser.event.BrowserPanelDeselectedEvent;
import org.exoplatform.ideall.client.browser.event.BrowserPanelSelectedEvent;
import org.exoplatform.ideall.client.component.ItemTreeGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.navigation.SimpleTabPanel;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;

public class BrowserForm extends SimpleTabPanel implements BrowserPresenter.Display
{

   public static final String TITLE = "Workspace";

   public static final String ID = "Workspace";

   private ItemTreeGrid<Item> treeGridEx;

   private ApplicationContext context;

   private BrowserPresenter presenter;

   public BrowserForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus);

      this.context = context;

      treeGridEx = new ItemTreeGrid<Item>();
      treeGridEx.setShowHeader(false);
      treeGridEx.setLeaveScrollbarGap(false);
      treeGridEx.setShowOpenIcons(true);
      treeGridEx.setEmptyMessage("Root folder not found!");

      treeGridEx.setHeight100();
      treeGridEx.setWidth100();
      addMember(treeGridEx);

      presenter = new BrowserPresenter(eventBus, context);
      presenter.bindDisplay(this);

      addMouseDownHandler(new MouseDownHandler()
      {
         public void onMouseDown(MouseDownEvent event)
         {
            event.cancel();
         }
      });
   }

   @Override
   public void destroy()
   {
      super.destroy();
      presenter.destroy();
   }

   public HasValue<Item> getBrowserTree()
   {
      return treeGridEx;
   }

   public HasOpenHandlers<Item> getBrowserTreeNavigator()
   {
      return treeGridEx;
   }

   public HasSelectionHandlers<Item> getBrowserTreeSelectable()
   {
      return treeGridEx;
   }

   public HasDoubleClickHandlers getBrowserTreeDClickable()
   {
      return treeGridEx;
   }

   public HasClickHandlers getBrowserClickable()
   {
      return treeGridEx;
   }

   @Override
   protected void onSelected()
   {
      eventBus.fireEvent(new BrowserPanelSelectedEvent());
      super.onSelected();
   }

   @Override
   protected void onDeselected()
   {
      eventBus.fireEvent(new BrowserPanelDeselectedEvent());
      super.onDeselected();
   }

   public void selectItem(String path)
   {
      treeGridEx.selectItem(path);
   }

}
