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
package org.exoplatform.ideall.client.search;

import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.event.file.OpenFileEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchResultsPresenter implements PanelSelectedHandler
{

   interface Display
   {

      TreeGridItem<Item> getSearchResultTree();

      List<Item> getSelectedItems();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Display display;

   private List<Item> selectedItem;

   private Folder searchresult;

   private Handlers handlers;

   public SearchResultsPresenter(HandlerManager eventBus, ApplicationContext context, Folder searchResult)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.searchresult = searchResult;

      handlers = new Handlers(eventBus);
   }

   public void bindDsplay(Display d)
   {
      this.display = d;

      handlers.addHandler(PanelSelectedEvent.TYPE, this);
      display.getSearchResultTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent arg0)
         {
            onBrowserDoubleClicked();
         }
      });

      display.getSearchResultTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Item> event)
         {
            onItemSelected();
         }
      });

      if (searchresult.getChildren() != null && !searchresult.getChildren().isEmpty())
      {
         searchresult.setIcon(Images.FileTypes.WORKSPACE);
         display.getSearchResultTree().setValue(searchresult);
      }
   }

   public void destroy()
   {
      updateSelectionTimer = null;
      handlers.removeHandlers();
   }

   /**
    * Handling of mouse double clicking
    */
   protected void onBrowserDoubleClicked()
   {

      if (selectedItem == null || context.getSelectedItems(context.getSelectedNavigationPanel()).size() != 1)
      {
         return;
      }

      Item item = selectedItem.get(0);
      if (item instanceof File)
      {
         context.setSelectedEditorDescription(null);
         eventBus.fireEvent(new OpenFileEvent((File)item));
      }
   }

   /**
    * 
    * Handling item selected event from panel
    * @param item
    */
   protected void onItemSelected()
   {
      if (updateSelectionTimer == null)
      {
         return;
      }

      updateSelectionTimer.cancel();
      updateSelectionTimer.schedule(10);
   }

   private Timer updateSelectionTimer = new Timer()
   {

      @Override
      public void run()
      {
         selectedItem = display.getSelectedItems();

         context.getSelectedItems(context.getSelectedNavigationPanel()).clear();
         context.getSelectedItems(context.getSelectedNavigationPanel()).addAll(selectedItem);

         eventBus.fireEvent(new SelectedItemsEvent(selectedItem));
      }

   };

   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (SearchResultPanel.ID.equals(event.getPanelId()))
      {
         onItemSelected();
      }
   }

}
