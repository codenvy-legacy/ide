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
 * @version $Id: Dec 12, 2011 5:52:59 PM anya $
 */
public interface NavigatorDisplay extends IsView {
    /** @return {@link TreeGridItem} */
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
     * @param itemId
     *         item's path
     */
    void selectItem(String itemId);

    /**
     * Deselect item in browser tree by path.
     *
     * @param itemId
     *         item's path
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

    /** Add info icons to main item icon */
    void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons);

    /** Remove info icon from item */
    void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons);
}
