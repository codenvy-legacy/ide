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
package org.exoplatform.ide.client.operation.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.vfs.client.event.SearchResultReceivedEvent;
import org.exoplatform.ide.vfs.client.event.SearchResultReceivedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchResultsPresenter implements ViewVisibilityChangedHandler,
ViewClosedHandler, SearchResultReceivedHandler
{

   public interface Display extends IsView
   {

      TreeGridItem<Item> getSearchResultTree();

      List<Item> getSelectedItems();

      void selectItem(String href);

      void deselectAllItems();

   }

   private Display display;

   private List<Item> selectedItems;

   private FolderModel searchResult;

   public SearchResultsPresenter()
   {
      IDE.addHandler(SearchResultReceivedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   @Override
   public void onSearchResultReceived(SearchResultReceivedEvent event)
   {
      searchResult = event.getFolder();
      if (display == null)
      {
         Display display = GWT.create(Display.class);
         IDE.getInstance().openView((View)display);
         bindDsplay(display);
      }
      else
      {
         ((View)display).setViewVisible();
         ((View)display).activate();
      }

      refreshSearchResult();
   }

   private void refreshSearchResult()
   {
//      searchResult.setIcon(Images.FileTypes.WORKSPACE);
      if (searchResult.getChildren() != null && !searchResult.getChildren().getItems().isEmpty())
      {
         // sort items in search result list
         Collections.sort(searchResult.getChildren().getItems(), new Comparator<Item>()
         {
            public int compare(Item item1, Item item2)
            {
               return item1.getName().compareTo(item2.getName());
            }
         });

         display.getSearchResultTree().setValue(searchResult);
         display.selectItem(searchResult.getId());
      }
      else
      {
         display.getSearchResultTree().setValue(searchResult);
         display.deselectAllItems();
      }

      selectedItems = display.getSelectedItems();
      onItemSelected();
   }

   public void bindDsplay(Display d)
   {
      this.display = d;

      display.getSearchResultTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent arg0)
         {
            openSelectedFile();
         }
      });

      display.getSearchResultTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Item> event)
         {
            onItemSelected();
         }
      });
      
      display.getSearchResultTree().addOpenHandler(new OpenHandler<Item>()
      {
         
         @Override
         public void onOpen(OpenEvent<Item> event)
         {
            refreshSearchResult();
         }
      });
   }

   public void destroy()
   {
      if (updateSelectionTimer != null)
      {
         updateSelectionTimer.cancel();
      }

      updateSelectionTimer = null;
   }

   /**
    * Handling of mouse double clicking
    */
   protected void openSelectedFile()
   {
      if (selectedItems == null || selectedItems.size() != 1)
      {
         return;
      }

      Item item = selectedItems.get(0);
      if (item instanceof File)
      {
         IDE.fireEvent(new OpenFileEvent((FileModel)item));
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
         if (display == null) {
            return;
         }
         
         selectedItems = display.getSelectedItems();
         IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
      }
   };

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (display == null)
      {
         return;
      }

      if (event.getView() instanceof Display)
      {
         if (event.getView().isViewVisible())
         {
            onItemSelected();
         }
         else
         {
            updateSelectionTimer.cancel();
         }
      }
   }
   
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (display != null && event.getView() instanceof Display)
      {
         display = null;
         updateSelectionTimer.cancel();
      }

   }

}
