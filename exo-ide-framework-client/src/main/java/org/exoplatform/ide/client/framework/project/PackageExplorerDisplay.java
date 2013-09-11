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
