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
package org.exoplatform.ideall.client.navigation;

import com.smartgwt.client.widgets.Canvas;
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

public class TabContainer extends TabSet
{

   public TabContainer()
   {
      addTabSelectedHandler(tabSelectedHandler);
      addTabDeselectedHandler(tabDeselectedHandler);
      addCloseClickHandler(closeClickhandler);
   }

   public boolean isTabPanelExist(String tabID)
   {
      return getTab(tabID) != null;
   }

   public void addTabPanel(Canvas tabPanel, String title, String id, String icon, boolean canClose)
   {
      Tab tab = new Tab("<span>" + Canvas.imgHTML(icon) + "&nbsp;" + title);
      tab.setID(id);
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
         System.out.println("tab selected > " + event.getTab().getID());
         SimpleTabPanel tabPanel = (SimpleTabPanel)event.getTab().getPane();
         tabPanel.setSelected();
      }
   };

   private TabDeselectedHandler tabDeselectedHandler = new TabDeselectedHandler()
   {
      public void onTabDeselected(TabDeselectedEvent event)
      {
         System.out.println("tab deselected > " + event.getTab().getID());
         SimpleTabPanel tabPanel = (SimpleTabPanel)event.getTab().getPane();
         tabPanel.setDeselected();
      }
   };

   private CloseClickHandler closeClickhandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         System.out.println("tab closed > " + event.getTab().getID());
         SimpleTabPanel tabPanel = (SimpleTabPanel)event.getTab().getPane();
         tabPanel.setClosed();
      }
   };

}
