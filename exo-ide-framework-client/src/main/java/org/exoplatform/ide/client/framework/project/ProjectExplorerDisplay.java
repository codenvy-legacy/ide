/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.framework.project;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 12, 2011 5:45:42 PM anya $
 */
public interface ProjectExplorerDisplay extends IsView {
    
    /**
     * Get Project Tree
     *
     * @return {@link TreeGridItem}
     */
    TreeGridItem<Item> getProjectTree();

    /**
     * Set project.
     * 
     * @param project
     */
    void setProject(ProjectModel project);
    
    /**
     * Select item.
     *
     * @param item
     *         item
     * @return <b>true</b> is item was found and selected, <b>false</b> otherwise
     */
    boolean selectItem(Item item);
    
    /**
     * Get selected item in Project tree.
     *
     * @return {@link List} selected items
     */
    Item getSelectedItem();

    /**
     * Get list of visible items in Project tree.
     * 
     * @return
     */
    List<Item> getVisibleItems();
    
    /**
     * Refresh project tree.
     */
    void refreshTree();
    
    /** 
     * Add info icons to main item icon.
     */
    void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons);

    /**
     * Remove additional icons from items.
     */
    void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons);
    
    
    
//    /**
//     * Deselect item in browser tree by path.
//     *
//     * @param path
//     *         item's path
//     */
//    void deselectItem(String path);
//
//    /**
//     * Update the state of the item in the tree.
//     *
//     * @param file
//     */
//    void updateItemState(FileModel file);


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
     * @param enabled
     *         <b>true</b> makes Link with Editor button enabled, <b>false</b> makes disabled
     */
    void setLinkWithEditorButtonEnabled(boolean enabled);

    /**
     * Adds or removes selection of Link with Editor button.
     *
     * @param selected
     *         <b>true</b> makes button selected, <b>false</b> otherwise
     */
    void setLinkWithEditorButtonSelected(boolean selected);

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
    
}
