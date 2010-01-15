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
package org.exoplatform.ideall.client.browser;

import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.search.SearchResultsForm;

import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.tab.Tab;
import com.smartgwt.client.widgets.tab.TabSet;

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
   private static final String SEARCH = "Search";

   /**
    * 
    */
   private static final String WORKSPACE = "Workspace";

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
   }

   protected void showBrowser()
   {
      Tab browseTab = new Tab("<span>" + Canvas.imgHTML(Images.BrowserPanel.ICON) + "&nbsp;" + WORKSPACE);
      browseTab.setID("Browser");
      NavigatorForm browserForm = new NavigatorForm(eventBus, context);
      browseTab.setPane(browserForm);
      tabSet.addTab(browseTab);
   }

   /**
    * {@inheritDoc}
    */
   public void showSearchResult(Folder folder)
   {
      if (tabSet.getTab(SEARCH) != null)
      {
         tabSet.removeTab(SEARCH);
      }
      tabSet.addTab(createSearchTab(folder));
      tabSet.selectTab(SEARCH);
   }

   /**
    * @param item
    * @return
    */
   private Tab createSearchTab(Folder item)
   {
      Tab searchTab = new Tab(SEARCH);
      SearchResultsForm form = new SearchResultsForm(eventBus, item);
      searchTab.setPane(form);
      searchTab.setID(SEARCH);
      searchTab.setCanClose(true);
      return searchTab;
   }

}
