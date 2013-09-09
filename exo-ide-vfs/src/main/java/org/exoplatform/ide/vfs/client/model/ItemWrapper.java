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
package org.exoplatform.ide.vfs.client.model;

import org.exoplatform.ide.vfs.shared.Item;

/**
 * Wrapper for {@link ItemWrapper} to help unmarshall item unknown type
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 29, 2011 evgen $
 */
public class ItemWrapper {
    private Item item;

    /**
     *
     */
    public ItemWrapper() {
    }

    /** @param item */
    public ItemWrapper(Item item) {
        this.item = item;
    }

    /** @return the item */
    public Item getItem() {
        return item;
    }

    /**
     * @param item
     *         the item to set
     */
    public void setItem(Item item) {
        this.item = item;
    }

}
