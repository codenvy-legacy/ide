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
package org.exoplatform.ide.client.framework.navigation.event;

import com.google.gwt.event.shared.GwtEvent;

import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SelectItemEvent extends GwtEvent<SelectItemHandler> {

    public static final GwtEvent.Type<SelectItemHandler> TYPE = new GwtEvent.Type<SelectItemHandler>();

    //private String itemId;

    private Item item;

    public SelectItemEvent(Item item) {
        this.item = item;
    }

    //   public String getItemId()
    //   {
    //      return itemId;
    //   }

    @Override
    protected void dispatch(SelectItemHandler handler) {
        handler.onSelectItem(this);
    }

    public Item getItem() {
        return item;
    }

    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<SelectItemHandler> getAssociatedType() {
        return TYPE;
    }

}
