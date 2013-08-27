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
package org.exoplatform.ide.vfs.shared;

import java.util.List;

/**
 * Set of abstract items for paging view.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ItemList<T extends Item> {
    /** @return set of items */
    List<T> getItems();

    void setItems(List<T> list);

    /**
     * @return total number of items. It is not need to be equals to number of items in current list {@link #getItems()}.
     *         It may be equals to number of items in current list only if this list contains all requested items and no
     *         more pages available. This method must return -1 if total number of items in unknown.
     */
    int getNumItems();

    void setNumItems(int numItems);

    /** @return <code>false</code> if this is last sub-set of items in paging */
    boolean isHasMoreItems();

    void setHasMoreItems(boolean hasMoreItems);
}
