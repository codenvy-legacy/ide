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

import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.browser.BrowserForm;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.panel.Panel;
import org.exoplatform.ide.client.search.file.SearchResultPanel;
import org.exoplatform.ide.client.search.file.SearchResultsForm;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.Image;
import com.smartgwt.client.types.Overflow;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class NavigationForm extends Panel implements NavigationPresenter.Display
{

   private HandlerManager eventBus;

   private ApplicationContext context;
   
   private final String ID = "ideNavigationPanel";
   
   private final static String TABSET_ID = "ideNavigationTabSet";

   /**
    * @param eventBus
    */
   public NavigationForm(HandlerManager eventBus, ApplicationContext context)
   {
      super(eventBus, TABSET_ID);
      this.eventBus = eventBus;
      this.context = context;
      

      setHeight100();
      setWidth100();

      showBrowser();


      NavigationPresenter presenter = new NavigationPresenter(eventBus);
      presenter.bindDisplay(this);
   }

   protected void showBrowser()
   {
      if (!isViewIsOpened(BrowserForm.ID))
      {
         BrowserForm navigatorForm = new BrowserForm(eventBus, context);
//         navigatorForm.setOverflow(Overflow.AUTO);
//         navigatorForm.setHeight100();
         Image tabIcon = new Image(IDEImageBundle.INSTANCE.workspace());
         openView(navigatorForm, BrowserForm.TITLE, tabIcon, false);
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
      closeView(SearchResultPanel.ID);

      SearchResultsForm searchResultForm = new SearchResultsForm(eventBus, context, folder);
      Image tabIcon = new Image(IDEImageBundle.INSTANCE.search());
      openView(searchResultForm, SearchResultsForm.TITLE, tabIcon, true);
      DeferredCommand.addCommand(new Command()
      {
         public void execute()
         {
            selectTabPanel(SearchResultsForm.ID);
         }
      });
   }

   public void selectBrowserPanel()
   {
      
      selectTabPanel(BrowserForm.ID);
   }

}
