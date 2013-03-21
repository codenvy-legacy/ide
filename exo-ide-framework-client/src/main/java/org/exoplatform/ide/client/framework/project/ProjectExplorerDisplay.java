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

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 12, 2011 5:45:42 PM anya $
 * 
 */
public interface ProjectExplorerDisplay extends IsView
{
   /**
    * Change tree visibility.
    * 
    * @param visible <code>true</code> if visible
    */
   void setProjectExplorerTreeVisible(boolean visible);

   /**
    * @return {@link TreeGridItem}
    */
   TreeGridItem<Item> getBrowserTree();
   
   void setProject(ProjectModel project);
   
   void navigateToItem(Item item);

   /**
    * Get selected items in the tree.
    * 
    * @return {@link List} selected items
    */
   List<Item> getSelectedItems();
   
   List<Item> getVisibleItems();
   
   /**
    * Select item.
    * 
    * @param item item
    * @return <b>true</b> is item was found and selected, <b>false</b> otherwise
    */
   boolean selectItem(Item item);

   /**
    * Deselect item in browser tree by path.
    * 
    * @param path item's path
    */
   void deselectItem(String path);

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
    * Add info icons to main item icon.
    */
   void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons);

   /**
    * Remove additional icons from items.
    */
   void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons);
   
   /**
    * Linking with Editor
    */

   /**
    * Returns Link with Editor button.
    * 
    * @return Link with Editor button
    */
   HasClickHandlers getLinkWithEditorButton();

   /**
    * Enables or disables Link with Editor button.
    * 
    * @param enabled <b>true</b> makes Link with Editor button enabled, <b>false</b> makes disabled
    */
   void setLinkWithEditorButtonEnabled(boolean enabled);

   /**
    * Adds or removes selection of Link with Editor button.
    * 
    * @param selected <b>true</b> makes button selected, <b>false</b> otherwise
    */
   void setLinkWithEditorButtonSelected(boolean selected);

   /**
    * Change projects list grid visibility.
    * 
    * @param visible <code>true</code> if visible
    */
   void setProjectsListGridVisible(boolean visible);

   /**
    * Change ProjectNotOpenedPanel visibility.
    * 
    * @param visible <code>true</code> if visible
    */
   void setProjectNotOpenedPanelVisible(boolean visible);

   /**
    * Get projects list grid.
    * 
    * @return projects list grid
    */
   ListGrid<ProjectModel> getProjectsListGrid();

   /**
    * Returns selected projects in the projects list grid.
    * 
    * @return {@link List} of selected projects
    */
   List<ProjectModel> getSelectedProjects();
   
   void refreshTree();

}
