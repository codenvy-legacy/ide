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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.api.TreeGridItem;
import org.exoplatform.ideall.client.browser.event.BrowserPanelDeselectedEvent;
import org.exoplatform.ideall.client.browser.event.BrowserPanelSelectedEvent;
import org.exoplatform.ideall.client.component.ItemTreeGrid;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.navigation.SimpleTabPanel;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.events.MouseDownEvent;
import com.smartgwt.client.widgets.events.MouseDownHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.grid.events.SelectionChangedHandler;
import com.smartgwt.client.widgets.grid.events.SelectionEvent;

public class BrowserForm extends SimpleTabPanel implements BrowserPresenter.Display
{

   public static final String TITLE = "Workspace";

   public static final String ID = "Workspace";

   private ItemTreeGrid<Item> treeGrid;

   private ApplicationContext context;

   private BrowserPresenter presenter;

   public BrowserForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus);

      this.context = context;

      treeGrid = new ItemTreeGrid<Item>();
      treeGrid.setShowHeader(false);
      treeGrid.setLeaveScrollbarGap(false);
      treeGrid.setShowOpenIcons(true);
      treeGrid.setEmptyMessage("Root folder not found!");
      
      treeGrid.setSelectionType(SelectionStyle.MULTIPLE);

      treeGrid.setHeight100();
      treeGrid.setWidth100();
      addMember(treeGrid);

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

   public TreeGridItem<Item> getBrowserTree()
   {
      return treeGrid;
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
      treeGrid.selectItem(path);
   }

   public List<Item> getSelectedItems() {
      return treeGrid.getSelectedItems();
   }
   
}
