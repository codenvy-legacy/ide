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
package org.exoplatform.ide.client.navigation;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.BrowserForm;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.framework.module.vfs.api.Folder;
import org.exoplatform.ide.client.panel.TabContainer;
import org.exoplatform.ide.client.search.file.SearchResultPanel;
import org.exoplatform.ide.client.search.file.SearchResultsForm;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.widgets.layout.Layout;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class NavigationForm extends Layout implements NavigationPresenter.Display
{

   private HandlerManager eventBus;

   private ApplicationContext context;

   private TabContainer tabContainer;
   
   private final String ID = "ideNavigationTabSet";

   /**
    * @param eventBus
    */
   public NavigationForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      setHeight100();
      setWidth100();
      tabContainer = new TabContainer(eventBus, ID);
      showBrowser();

      addMember(tabContainer);

      NavigationPresenter presenter = new NavigationPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   protected void showBrowser()
   {
      if (!tabContainer.isTabPanelExist(BrowserForm.ID))
      {
         BrowserForm navigatorForm = new BrowserForm(eventBus, context);
         Image tabIcon = new Image(IDEImageBundle.INSTANCE.workspace());
         tabContainer.addTabPanel(navigatorForm, BrowserForm.TITLE, tabIcon, false);
      }
      
//      if (!tabContainer.isTabPanelExist(BrowserFormNew.ID))
//      {
//         BrowserFormNew navigatorForm = new BrowserFormNew(eventBus, context);
//         tabContainer.addTabPanel(navigatorForm, BrowserFormNew.TITLE, Images.BrowserPanel.ICON, false);
//      }
   }

   /**
    * {@inheritDoc}
    */
   public void showSearchResult(Folder folder)
   {
      tabContainer.closeTabPanel(SearchResultPanel.ID);

      SearchResultsForm searchResultForm = new SearchResultsForm(eventBus, context, folder);
      Image tabIcon = new Image(IDEImageBundle.INSTANCE.search());
      tabContainer.addTabPanel(searchResultForm, SearchResultsForm.TITLE, tabIcon, true);
      tabContainer.selectTabPanel(SearchResultsForm.ID);
   }

   public void selectBrowserPanel()
   {
      //tabContainer.selectTabPanel(BrowserFormNew.ID);
      
      tabContainer.selectTabPanel(BrowserForm.ID);
   }

}
