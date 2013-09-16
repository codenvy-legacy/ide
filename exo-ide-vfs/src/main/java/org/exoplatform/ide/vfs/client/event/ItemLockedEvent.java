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
package org.exoplatform.ide.vfs.client.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.LockToken;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ItemLockedEvent extends GwtEvent<ItemLockedHandler> {

    public static final GwtEvent.Type<ItemLockedHandler> TYPE = new Type<ItemLockedHandler>();

    private LockToken lockToken;

    private Item item;

    /**
     * @param lockToken
     * @param item
     */
    public ItemLockedEvent(Item item, LockToken lockToken) {
        super();
        this.lockToken = lockToken;
        this.item = item;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<ItemLockedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(ItemLockedHandler handler) {
        handler.onItemLocked(this);
    }

    /** @return the lockToken */
    public LockToken getLockToken() {
        return lockToken;
    }

    /** @return the item */
    public Item getItem() {
        return item;
    }

}
