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

import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.ImageUtil;
import org.exoplatform.ide.client.editor.MinMaxControlButton;
import org.exoplatform.ide.client.event.perspective.MaximizeOperationPanelEvent;
import org.exoplatform.ide.client.event.perspective.RestoreOperationPanelEvent;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.ViewHighlightManager;
import org.exoplatform.ide.client.panel.event.ChangePanelTitleEvent;
import org.exoplatform.ide.client.panel.event.ChangePanelTitleHandler;
import org.exoplatform.ide.client.panel.event.PanelClosedEvent;
import org.exoplatform.ide.client.panel.event.PanelDeselectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.SelectPanelEvent;
import org.exoplatform.ide.client.panel.event.SelectPanelHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.TabBarControls;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.layout.Layout;
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

public class TabContainer extends TabSet implements SelectPanelHandler, ChangePanelTitleHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;
   
   private HashMap<String, List<Canvas>> tabColtrolButtons = new HashMap<String, List<Canvas>>();
   
   protected String previousTab = null;
   
   protected MinMaxControlButton minMaxControlButton;
   
   private Layout tabBarColtrols;

   private Tab selectedTab;
   
   public TabContainer(HandlerManager eventBus, String id)
   {
      setID(id);
      setAttribute("paneMargin", 1, false);
            
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
            
      handlers.addHandler(SelectPanelEvent.TYPE, this);
      handlers.addHandler(ChangePanelTitleEvent.TYPE, this);
      
      addTabSelectedHandler(tabSelectedHandler);
      addTabDeselectedHandler(tabDeselectedHandler);
      addCloseClickHandler(closeClickhandler);
      addClickHandler(new ClickHandler()
      {
         
         public void onClick(ClickEvent event)
         {
            if(selectedTab != null)
            {
               View view = (View)selectedTab.getPane();
               ViewHighlightManager.getInstance().selectView(view);
            }
         }
      });
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

   public void addTabPanel(View view, String title, Image image, boolean canClose)
   {
      for (String tabTitle : tabColtrolButtons.keySet())
      {
         List<Canvas> buttons = tabColtrolButtons.get(tabTitle);
         for (Canvas button : buttons)
         {
            button.hide();
         }
      }

      tabColtrolButtons.put(view.getTitle(), view.getColtrolButtons());
      int position = 0;
      for (Canvas button : tabColtrolButtons.get(view.getTitle()))
      {
         tabBarColtrols.addMember(button, position);
         position++;
      }
      
      String imageHTML = ImageUtil.getHTML(image);
      Tab tab = new Tab("<span>" + imageHTML + "&nbsp;" + title);
      tab.setID(view.getViewId());
      tab.setPane(view);
      tab.setCanClose(canClose);
      addTab(tab);
   }

   public void closeTabPanel(String tabId)
   {
      if (isTabPanelExist(tabId))
      {
         removeTab(tabId);
         eventBus.fireEvent(new PanelClosedEvent(tabId));
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
         if (previousTab != null)
         {
            List<Canvas> buttons = tabColtrolButtons.get(previousTab);
            for (Canvas button : buttons)
            {
               button.hide();
            }
         }
         int buttonsWidth = 20;
         List<Canvas> buttonsToShow = tabColtrolButtons.get(event.getTab().getPane().getTitle());
         for (Canvas button : buttonsToShow)
         {
            button.show();
            buttonsWidth += button.getWidth();
         }

         previousTab = event.getTab().getPane().getTitle();
         selectedTab = event.getTab();
         
         for (Canvas c : getChildren())
         {
            if (c.getID().equals(getID() + "_tabBar"))
            {
               if (c.getWidth() > getWidth() - buttonsWidth)
               {
                  c.setWidth(getWidth() - buttonsWidth);
               }
            }
         }
         
         View view = (View)event.getTab().getPane();
         ViewHighlightManager.getInstance().selectView(view);
         eventBus.fireEvent(new PanelSelectedEvent(view.getViewId()));
      }
   };

   private TabDeselectedHandler tabDeselectedHandler = new TabDeselectedHandler()
   {
      public void onTabDeselected(TabDeselectedEvent event)
      {
         View tabPanel = (View)event.getTab().getPane();
         eventBus.fireEvent(new PanelDeselectedEvent(tabPanel.getViewId()));
      }
   };

   private CloseClickHandler closeClickhandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         /*
          * delete all buttons from control bar 
          */
         List<Canvas> buttons = tabColtrolButtons.get(event.getTab().getPane().getTitle());
         for (Canvas button : buttons)
         {
            tabBarColtrols.removeMember(button);
         }
         
         View view = (View)event.getTab().getPane();
//         event.getTab().getPane().destroy();
//         removeTab(event.getTab());
         eventBus.fireEvent(new PanelClosedEvent(view.getViewId()));
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

   /**
    * @see org.exoplatform.ide.client.panel.event.ChangePanelTitleHandler#onChangePanelTitle(org.exoplatform.ide.client.panel.event.ChangePanelTitleEvent)
    */
   public void onChangePanelTitle(ChangePanelTitleEvent event)
   {
      if (isTabPanelExist(event.getPanelId())){
         setTabTitle(event.getPanelId(), event.getTitle());
      }
   }
   
   public void createButtons()
   {
      tabBarColtrols = new Layout();
      tabBarColtrols.setHeight(18);
      tabBarColtrols.setAutoWidth();

      minMaxControlButton =
         new MinMaxControlButton(eventBus, true, new MaximizeOperationPanelEvent(), new RestoreOperationPanelEvent());
      tabBarColtrols.addMember(minMaxControlButton);

      setTabBarControls(TabBarControls.TAB_SCROLLER, TabBarControls.TAB_PICKER, tabBarColtrols);
   }

}
