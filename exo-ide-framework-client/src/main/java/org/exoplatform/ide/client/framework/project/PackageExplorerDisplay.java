/*
 * Copyright (C) 2012 eXo Platform SAS.
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
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.project.api.IDEProject;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public interface PackageExplorerDisplay extends IsView {

    /**
     * @return
     */
    TreeGridItem<Item> getBrowserTree();

    /**
     * Set new project in Package Explorer tree.
     * 
     * @param project project
     */
    void setProject(IDEProject project);

    /**
     * Search and select specified item in Project Explorer tree.
     * 
     * @param item
     * @return
     */
    boolean selectItem(Item item);

    /**
     * Returns selected item in Project Explorer tree.
     * 
     * @return
     */
    Item getSelectedItem();

    /**
     * Returns children of specified filder.
     * 
     * @param folder folder
     * @return folder's children
     */
    List<Item> getTreeChildren(FolderModel folder);

    /**
     * Returns all visible items from Project Explorer tree.
     * 
     * @return
     */
    List<Item> getVisibleItems();

    /**
     * Refreshes Package Explorer tree.
     */
    void refreshTree();

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
     * Add additional icons to tree items.
     * 
     * @param itemsIcons
     */
    void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons);

    /**
     * Remove additional icons from tree items.
     * 
     * @param itemsIcons
     */
    void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons);

}
