/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.navigation;

import com.google.gwt.user.client.Command;

import com.google.gwt.user.client.DeferredCommand;

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.BrowserForm;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.panel.Panel;
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

   private Panel tabContainer;
   
   private final String ID = "ideNavigationPanel";
   
   private final String TABSET_ID = "ideNavigationTabSet";

   /**
    * @param eventBus
    */
   public NavigationForm(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      
      setID(ID);

      setHeight100();
      setWidth100();
      tabContainer = new Panel(eventBus, TABSET_ID);
      showBrowser();

      addMember(tabContainer);

      NavigationPresenter presenter = new NavigationPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   protected void showBrowser()
   {
      if (!tabContainer.isViewIsOpened(BrowserForm.ID))
      {
         BrowserForm navigatorForm = new BrowserForm(eventBus, context);
         Image tabIcon = new Image(IDEImageBundle.INSTANCE.workspace());
         tabContainer.openView(navigatorForm, BrowserForm.TITLE, tabIcon, false);
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
      tabContainer.closeView(SearchResultPanel.ID);

      SearchResultsForm searchResultForm = new SearchResultsForm(eventBus, context, folder);
      Image tabIcon = new Image(IDEImageBundle.INSTANCE.search());
      tabContainer.openView(searchResultForm, SearchResultsForm.TITLE, tabIcon, true);
      DeferredCommand.addCommand(new Command()
      {
         public void execute()
         {
            tabContainer.selectTabPanel(SearchResultsForm.ID);
         }
      });
   }

   public void selectBrowserPanel()
   {
      //tabContainer.selectTabPanel(BrowserFormNew.ID);
      
      tabContainer.selectTabPanel(BrowserForm.ID);
   }

}
