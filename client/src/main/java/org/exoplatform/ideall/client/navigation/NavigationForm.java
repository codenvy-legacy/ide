/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client.navigation;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.browser.BrowserForm;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.search.SearchResultsForm;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;
import com.smartgwt.client.widgets.tab.events.CloseClickHandler;
import com.smartgwt.client.widgets.tab.events.TabCloseClickEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedEvent;
import com.smartgwt.client.widgets.tab.events.TabSelectedHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class NavigationForm extends Layout implements NavigationPresenter.Display
{

   /**
    * 
    */
   private HandlerManager eventBus;

   private ApplicationContext context;

   /**
    * 
    */
   private TabSet tabSet;

   /**
    * @param eventBus
    */
   public NavigationForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      setHeight100();
      setWidth100();
      tabSet = new TabSet();
      showBrowser();

      addMember(tabSet);

      NavigationPresenter presenter = new NavigationPresenter(eventBus);
      presenter.bindDisplay(this);
      
      tabSet.addTabSelectedHandler(tabSelectedHandler);
      tabSet.addCloseClickHandler(closeClickhandler);
   }
   
   private TabSelectedHandler tabSelectedHandler = new TabSelectedHandler()
   {
      public void onTabSelected(TabSelectedEvent event)
      {
         System.out.println("switching tab........");
         
         System.out.println("selecetd tab: " + event.getTab().getTitle());
         
//         if (previousTab != null)
//         {
//            List<Canvas> buttons = tabColtrolButtons.get(previousTab);
//            for (Canvas button : buttons)
//            {
//               button.hide();
//            }
//         }
//
//         int buttonsWidth = 0;
//         List<Canvas> buttonsToShow = tabColtrolButtons.get(event.getTab().getTitle());
//         for (Canvas button : buttonsToShow)
//         {
//            button.show();
//            buttonsWidth += button.getWidth();
//         }
//
//         previousTab = event.getTab().getTitle();
//         
//         for (Canvas c : tabSet.getChildren())
//         {
//            if (c.getID().equals(tabSet.getID() + "_tabBar"))
//            {
//               if (c.getWidth() > tabSet.getWidth() - buttonsWidth)
//               {
//                  c.setWidth(tabSet.getWidth() - buttonsWidth);
//               }
//            }
//         }

      }
   };

   
   /**
    * Closing tab click handler
    */
   private CloseClickHandler closeClickhandler = new CloseClickHandler()
   {
      public void onCloseClick(TabCloseClickEvent event)
      {
         System.out.println("closing tab.........");
         
         System.out.println("closing tab: " + event.getTab().getTitle());
         
//         /*
//          * delete all buttons from control bar 
//          */
//         List<Canvas> buttons = tabColtrolButtons.get(event.getTab().getTitle());
//         for (Canvas button : buttons)
//         {
//            tabBarColtrols.removeMember(button);
//         }
//
//         ((TabPanel)event.getTab().getPane()).onCloseTab();
      }
   };
   
   
   
   

   protected void showBrowser()
   {
      Tab browseTab = new Tab("<span>" + Canvas.imgHTML(Images.BrowserPanel.ICON) + "&nbsp;" + BrowserForm.TITLE);
      browseTab.setID(BrowserForm.TITLE);
      BrowserForm navigatorForm = new BrowserForm(eventBus, context);
      browseTab.setPane(navigatorForm);
      tabSet.addTab(browseTab);
   }

   /**
    * {@inheritDoc}
    */
   public void showSearchResult(Folder folder)
   {
      if (tabSet.getTab(SearchResultsForm.TITLE) != null)
      {
         tabSet.removeTab(SearchResultsForm.TITLE);
      }
      tabSet.addTab(createSearchTab(folder));
      tabSet.selectTab(SearchResultsForm.TITLE);
   }

   /**
    * @param item
    * @return
    */
   private Tab createSearchTab(Folder item)
   {
      Tab searchTab = new Tab("<span>" + Canvas.imgHTML(Images.SearchPanel.ICON) + "</span>&nbsp;" + SearchResultsForm.TITLE);
      SearchResultsForm form = new SearchResultsForm(eventBus, item);
      searchTab.setPane(form);
      searchTab.setID(SearchResultsForm.TITLE);
      searchTab.setCanClose(true);
      return searchTab;
   }

}
