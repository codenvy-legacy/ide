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
package org.exoplatform.ide.client.operation.search;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.ui.ItemTree;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SearchResultsViewExtended extends ViewImpl implements org.exoplatform.ide.client.operation.search.SearchResultsPresenter.Display
{
   
   private static final String ID = "ideSearchResultView";
   
   /**
    * Initial width of this view
    */
   private static int WIDTH = 250;

   /**
    * Initial height of this view
    */
   private static int HEIGHT = 450;   

   private static SearchResultsViewExtendedUiBinder uiBinder = GWT.create(SearchResultsViewExtendedUiBinder.class);

   interface SearchResultsViewExtendedUiBinder extends UiBinder<Widget, SearchResultsViewExtended>
   {
   }
   
   @UiField
   ItemTree treeGrid;
   
   private static final String TITLE = IDE.NAVIGATION_CONSTANT.searchResultTitle();

   public SearchResultsViewExtended()
   {
      super(ID, "navigation", TITLE, new Image(IDEImageBundle.INSTANCE.search()), WIDTH, HEIGHT);
      add(uiBinder.createAndBindUi(this));
   }

   @Override
   public TreeGridItem<Item> getSearchResultTree()
   {
      return treeGrid;
   }

   @Override
   public List<Item> getSelectedItems()
   {
      return treeGrid.getSelectedItems();
   }

   @Override
   public void selectItem(String href)
   {
      treeGrid.selectItem(href);
   }

   @Override
   public void deselectAllItems()
   {
      treeGrid.deselectAllRecords();
   }
   
}
