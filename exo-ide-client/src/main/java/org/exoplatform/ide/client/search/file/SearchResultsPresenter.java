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
package org.exoplatform.ide.client.search.file;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.module.navigation.event.selection.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;

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

      void selectItem(String href);

      void deselectAllItems();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Display display;

   private List<Item> selectedItems;

   private Folder searchResult;

   private Handlers handlers;

   public SearchResultsPresenter(HandlerManager eventBus, ApplicationContext context, Folder searchResult)
   {
      this.eventBus = eventBus;
      this.context = context;
      this.searchResult = searchResult;

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

      searchResult.setIcon(Images.FileTypes.WORKSPACE);
            
      if (searchResult.getChildren() != null && !searchResult.getChildren().isEmpty())
      {
         // sort items in search result list
         Collections.sort(searchResult.getChildren(), new Comparator<Item>()
            {
               public int compare(Item item1, Item item2)
               {
                  return item1.getName().compareTo(item2.getName());
               }
            }
         );

         display.getSearchResultTree().setValue(searchResult);
         display.selectItem(searchResult.getHref());
      }
      else
      {
         display.getSearchResultTree().setValue(searchResult);
         display.deselectAllItems();
      }

      selectedItems = display.getSelectedItems();
      onItemSelected();
   }

   public void destroy()
   {
      if (updateSelectionTimer != null)
      {
         updateSelectionTimer.cancel();
      }

      updateSelectionTimer = null;
      handlers.removeHandlers();
   }

   /**
    * Handling of mouse double clicking
    */
   protected void onBrowserDoubleClicked()
   {
      if (selectedItems == null || selectedItems.size() != 1)
      {
         return;
      }

      Item item = selectedItems.get(0);
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
         selectedItems = display.getSelectedItems();

         //         context.getSelectedItems(context.getSelectedNavigationPanel()).clear();
         //         context.getSelectedItems(context.getSelectedNavigationPanel()).addAll(selectedItems);

         eventBus.fireEvent(new ItemsSelectedEvent(selectedItems, SearchResultPanel.ID));
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
