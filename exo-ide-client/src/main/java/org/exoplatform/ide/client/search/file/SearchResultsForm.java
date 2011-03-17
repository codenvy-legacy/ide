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
package org.exoplatform.ide.client.search.file;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.ScrollPanel;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.browser.ItemTree;
import org.exoplatform.ide.client.component.ItemTreeGrid;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.ApplicationContext;

import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class SearchResultsForm extends View implements SearchResultPanel, SearchResultsPresenter.Display
{

   private final String TREE_ID = "ideSearchResultItemTreeGrid";

   public static final String TITLE = "Search";

   private ItemTree searchItemTreeGrid;

   private final String FILE_NOT_FOUND_MESSAGE = "No results found!";

   private SearchResultsPresenter presenter;

   public SearchResultsForm(HandlerManager eventBus, ApplicationContext context, Folder searchResult)
   {
      super(ID, eventBus);
      searchItemTreeGrid = new ItemTree();
      ScrollPanel treeWrapper = new ScrollPanel(searchItemTreeGrid);
      treeWrapper.ensureDebugId("Tree-itemTree-Wrapper");
      treeWrapper.setSize("100%", "100%");
//      searchItemTreeGrid = new ItemTreeGrid<Item>(TREE_ID, true);
//      searchItemTreeGrid.setEmptyMessage(FILE_NOT_FOUND_MESSAGE);
//      searchItemTreeGrid.setShowHeader(false);
//      searchItemTreeGrid.setLeaveScrollbarGap(false);
//
//      searchItemTreeGrid.setHeight100();
//      searchItemTreeGrid.setWidth100();
      addMember(treeWrapper);

      presenter = new SearchResultsPresenter(eventBus, context, searchResult);
      presenter.bindDsplay(this);
   }

   @Override
   public void destroy()
   {
      presenter.destroy();
      super.destroy();
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
