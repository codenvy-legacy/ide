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

import org.exoplatform.ideall.client.component.ItemTreeGrid;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.Item;

import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.events.HasClickHandlers;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchResultsForm extends Canvas implements SearchResultsPresenter.Display
{

   private ItemTreeGrid<Item> searchItemTreeGrid;

   private HandlerManager eventBus;
   
   private final String FILE_NOT_FOUND_MESSAGE = "No results found!";

   public SearchResultsForm(HandlerManager eventBus, Folder searchResult)
   {
      this.eventBus = eventBus;
      searchItemTreeGrid = new ItemTreeGrid<Item>(true);
      searchItemTreeGrid.setEmptyMessage(FILE_NOT_FOUND_MESSAGE);
      searchItemTreeGrid.setShowHeader(false);
      searchItemTreeGrid.setLeaveScrollbarGap(false);
      
      searchItemTreeGrid.setHeight100();
      searchItemTreeGrid.setWidth100();
      addChild(searchItemTreeGrid);

      SearchResultsPresenter presenter = new SearchResultsPresenter(eventBus, searchResult);
      presenter.bindDsplay(this);
   }

   public HasClickHandlers getSearchResultClickHandler()
   {
      return searchItemTreeGrid;
   }

   public HasDoubleClickHandlers getSearchResultDoubleClickHandler()
   {
      return searchItemTreeGrid;
   }

   public HasSelectionHandlers<Item> getSearchResultSelectionChangeHandler()
   {
      return searchItemTreeGrid;
   }

   public HasValue<Item> getSearchResultTree()
   {
      return searchItemTreeGrid;
   }

}
