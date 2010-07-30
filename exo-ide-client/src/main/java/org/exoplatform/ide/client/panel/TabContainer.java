/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.panel;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.ImageUtil;
import org.exoplatform.ide.client.panel.event.PanelClosedEvent;
import org.exoplatform.ide.client.panel.event.PanelDeselectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.SelectPanelEvent;
import org.exoplatform.ide.client.panel.event.SelectPanelHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedEvent;
import com.smartgwt.client.widgets.tab.events.TabDeselectedHandler;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TabContainer extends TabSet implements SelectPanelHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   public TabContainer(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      handlers.addHandler(SelectPanelEvent.TYPE, this);

      addTabSelectedHandler(tabSelectedHandler);
      addTabDeselectedHandler(tabDeselectedHandler);
      addCloseClickHandler(closeClickhandler);
   }

   @Override
   public void destroy()
   {
      handlers.removeHandlers();
      super.destroy();
   }

   public boolean isTabPanelExist(String tabID)
   {
      return getTab(tabID) != null;
   }

   public void addTabPanel(SimpleTabPanel tabPanel, String title, Image image, boolean canClose)
   {
      String imageHTML = ImageUtil.getHTML(image);
      Tab tab = new Tab("<span>" + imageHTML + "&nbsp;" + title);
      tab.setID(tabPanel.getPanelId());
      tab.setPane(tabPanel);
      tab.setCanClose(canClose);
      addTab(tab);
   }

   public void closeTabPanel(String tabId)
   {
      if (isTabPanelExist(tabId))
      {
         removeTab(tabId);
      }
   }

   public void selectTabPanel(String tabId)
   {
      selectTab(tabId);
   }

   private TabSelectedHandler tabSelectedHandler = new TabSelectedHandler()
   {
      public void onTabSelected(TabSelectedEvent event)
      {
         SimpleTabPanel tabPanel = (SimpleTabPanel)event.getTab().getPane();
         eventBus.fireEvent(new PanelSelectedEvent(tabPanel.getPanelId()));
      }
   };

   private TabDeselectedHandler tabDeselectedHandler = new TabDeselectedHandler()
   {
      public void onTabDeselected(TabDeselectedEvent event)
      {
         SimpleTabPanel tabPanel = (SimpleTabPanel)event.getTab().getPane();
         eventBus.fireEvent(new PanelDeselectedEvent(tabPanel.getPanelId()));
      }
   };

   private CloseClickHandler closeClickhandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         SimpleTabPanel tabPanel = (SimpleTabPanel)event.getTab().getPane();
         eventBus.fireEvent(new PanelClosedEvent(tabPanel.getPanelId()));
      }
   };

   public void onSelectPanel(SelectPanelEvent event)
   {
      if (event.getPanelId() == null)
      {
         return;
      }

      selectTab(event.getPanelId());
   }

}
