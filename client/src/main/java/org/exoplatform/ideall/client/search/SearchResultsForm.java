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
import org.exoplatform.ideall.client.component.ItemTreeGrid;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.navigation.SimpleTabPanel;
import org.exoplatform.ideall.client.search.event.SearchResultPanelSelectedEvent;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchResultsForm extends SimpleTabPanel implements SearchResultsPresenter.Display
{

   public static final String TITLE = "Search";

   public static final String ID = "Search";

   private ItemTreeGrid<Item> searchItemTreeGrid;

   private final String FILE_NOT_FOUND_MESSAGE = "No results found!";

   public SearchResultsForm(HandlerManager eventBus, Folder searchResult)
   {
      super(eventBus);

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

   public TreeGridItem<Item> getSearchResultTree()
   {
      return searchItemTreeGrid;
   }

   @Override
   protected void onSelected()
   {
      eventBus.fireEvent(new SearchResultPanelSelectedEvent());
      super.onSelected();
   }

}
