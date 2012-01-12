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
package org.exoplatform.ide.client.framework.project;

import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id:  Dec 12, 2011 5:52:59 PM anya $
 *
 */
public interface NavigatorDisplay extends IsView
{
   /**
    * @return {@link TreeGridItem}
    */
   TreeGridItem<Item> getBrowserTree();

   /**
    * Get selected items in the tree.
    * 
    * @return {@link List} selected items
    */
   List<Item> getSelectedItems();

   /**
    * Select item in browser tree by path.
    * 
    * @param itemId item's path
    */
   void selectItem(String itemId);

   /**
    * Deselect item in browser tree by path.
    * 
    * @param itemId item's path
    */
   void deselectItem(String itemId);

   /**
    * Update the state of the item in the tree.
    * 
    * @param file
    */
   void updateItemState(FileModel file);

   /**
    * Set lock tokens to the items in the tree.
    * 
    * @param locktokens
    */
   void setLockTokens(Map<String, String> locktokens);

   /**
    * Add info icons to main item icon
    */
   void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons);

   /**
    * Remove info icon from item
    */
   void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons);
}
