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
import com.google.gwt.resources.client.ImageResource;

import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: AddItemTreeIcon Apr 6, 2011 9:40:05 AM evgen $
 */
public class AddItemTreeIconEvent extends GwtEvent<AddItemTreeIconHandler> {

    public static GwtEvent.Type<AddItemTreeIconHandler> TYPE = new Type<AddItemTreeIconHandler>();

    private Map<Item, Map<TreeIconPosition, ImageResource>> treeItemIcons;

    /** @param treeItemIcons */
    public AddItemTreeIconEvent(Map<Item, Map<TreeIconPosition, ImageResource>> treeItemsIcons) {
        super();
        this.treeItemIcons = treeItemsIcons;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public com.google.gwt.event.shared.GwtEvent.Type<AddItemTreeIconHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(AddItemTreeIconHandler handler) {
        handler.onAddItemTreeIcon(this);
    }

    /** @return the treeItemIcons */
    public Map<Item, Map<TreeIconPosition, ImageResource>> getTreeItemIcons() {
        return treeItemIcons;
    }

}
