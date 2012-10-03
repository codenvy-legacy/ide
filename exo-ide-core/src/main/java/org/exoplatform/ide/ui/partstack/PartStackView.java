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
package org.exoplatform.ide.ui.partstack;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.InlineLabel;
import com.google.gwt.user.client.ui.SimplePanel;

import java.util.ArrayList;

/**
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a> 
 */
public class PartStackView extends Composite implements PartStackPresenter.Display
{

   private static PartStackResources clientBundle = GWT.create(PartStackResources.class);

   private SimplePanel contentPanel;

   private FlowPanel tabsPanel;

   private ArrayList<TabButton> tabs = new ArrayList<TabButton>();

   private TabButton activeTab;

   private DockLayoutPanel parent;

   public PartStackView()
   {
      clientBundle.partStack().ensureInjected();
      parent = new DockLayoutPanel(Unit.PX);
      initWidget(parent);
      
      addDomHandler(new MouseDownHandler()
      {
         @Override
         public void onMouseDown(MouseDownEvent event)
         {
            onEnterFocus();
         }
      }, MouseDownEvent.getType());
      
      
      parent.setStyleName(clientBundle.partStack().idePartStack());
      tabsPanel = new FlowPanel();
      tabsPanel.setStyleName(clientBundle.partStack().idePartStackTabs());
      contentPanel = new SimplePanel();
      contentPanel.setStyleName(clientBundle.partStack().idePartStackContent());

      parent.addNorth(tabsPanel, 26);
      parent.add(contentPanel);
   }

   /**
    * 
    */
   protected void onEnterFocus()
   {
      parent.addStyleName(clientBundle.partStack().idePartStackFocused());
   }

   private class TabButton extends Composite implements PartStackPresenter.Display.TabItem
   {

      private FlowPanel tabItem;

      private InlineLabel tabItemTittle;

      private Image image;

      public TabButton(String title, boolean closable)
      {
         tabItem = new FlowPanel();
         initWidget(tabItem);
         this.setStyleName(clientBundle.partStack().idePartStackTab());
         tabItemTittle = new InlineLabel(title);
         tabItem.add(tabItemTittle);
         if (closable)
         {
            image = new Image(clientBundle.close());
            tabItem.add(image);
            addHandlers();
         }
      }

      @Override
      public HandlerRegistration addCloseHandler(CloseHandler<TabItem> handler)
      {
         return addHandler(handler, CloseEvent.getType());
      }

      private void addHandlers()
      {
         image.addClickHandler(new ClickHandler()
         {
            @Override
            public void onClick(ClickEvent event)
            {
               CloseEvent.fire(TabButton.this, TabButton.this);
            }
         });
      }

      @Override
      public HandlerRegistration addClickHandler(ClickHandler handler)
      {
         return addDomHandler(handler, ClickEvent.getType());
      }

   }

   /**
   * {@inheritDoc}
   */
   @Override
   public TabItem addTabButton(Image icon, String title, boolean closable)
   {
      TabButton tabItem = new TabButton(title, closable);
      tabsPanel.add(tabItem);
      tabs.add(tabItem);
      return tabItem;
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void removeTabButton(int index)
   {
      if (index < tabs.size())
      {
         TabButton removed = tabs.remove(index);
         tabsPanel.remove(removed);
      }
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public void setActiveTabButton(int index)
   {
      if (activeTab != null)
      {
         activeTab.removeStyleName(clientBundle.partStack().idePartStackTabSelected());
      }

      if (index >= 0 && index < tabs.size())
      {
         activeTab = tabs.get(index);
         activeTab.addStyleName(clientBundle.partStack().idePartStackTabSelected());
      }
   }

   /**
   * {@inheritDoc}
   */
   @Override
   public HasWidgets getContentPanel()
   {
      return contentPanel;
   }
}
