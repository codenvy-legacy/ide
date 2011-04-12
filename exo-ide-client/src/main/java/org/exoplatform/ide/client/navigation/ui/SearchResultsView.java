/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.navigation.ui;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.vfs.Item;

import com.google.gwt.user.client.ui.Image;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SearchResultsView extends ViewImpl implements
   org.exoplatform.ide.client.navigation.SearchResultsPresenter.Display
{

   private static final String TREE_ID = "ideSearchResultItemTreeGrid";

   private static final String TREE_PREFIX_ID = "search";

   private ItemTree searchItemTreeGrid;

   private final String FILE_NOT_FOUND_MESSAGE = "No results found!";

   public SearchResultsView()
   {
      super(ID, "navigation", "Search", new Image(IDEImageBundle.INSTANCE.search()));

      searchItemTreeGrid = new ItemTree(TREE_ID, TREE_PREFIX_ID);
      add(searchItemTreeGrid, true);
   }

   public TreeGridItem<Item> getSearchResultTree()
   {
      return searchItemTreeGrid;
   }

   public List<Item> getSelectedItems()
   {
      return searchItemTreeGrid.getSelectedItems();
   }

   public void selectItem(String href)
   {
      searchItemTreeGrid.selectItem(href);
   }

   public void deselectAllItems()
   {
      searchItemTreeGrid.deselectAllRecords();
   }

}
