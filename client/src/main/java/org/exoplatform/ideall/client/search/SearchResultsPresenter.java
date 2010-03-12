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

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.model.data.DataService;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchResultsPresenter
{

   interface Display
   {

      TreeGridItem<Item> getSearchResultTree();

   }

   private HandlerManager eventBus;

   private Display display;

   private Item selectedItem;

   private Folder searchresult;

   public SearchResultsPresenter(HandlerManager eventBus, Folder searchResult)
   {
      this.eventBus = eventBus;
      this.searchresult = searchResult;
   }

   public void bindDsplay(Display d)
   {
      this.display = d;

      display.getSearchResultTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent arg0)
         {
            openFile(selectedItem);
         }
      });

      display.getSearchResultTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         public void onSelection(com.google.gwt.event.logical.shared.SelectionEvent<Item> event)
         {
            selectedItem = event.getSelectedItem();
         }
      });

      if (searchresult.getChildren() != null && !searchresult.getChildren().isEmpty())
      {
         searchresult.setIcon(Images.FileTypes.WORKSPACE);
         display.getSearchResultTree().setValue(searchresult);
      }
   }

   private void openFile(Item item)
   {
      if (!(item instanceof File))
      {
         return;
      }

      DataService.getInstance().getFileContent((File)item);
   }

}
