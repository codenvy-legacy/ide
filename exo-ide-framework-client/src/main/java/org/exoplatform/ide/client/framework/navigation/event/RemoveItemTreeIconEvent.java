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

import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: RemoveItemTreeIconEvent Apr 6, 2011 9:50:25 AM evgen $
 */
public class RemoveItemTreeIconEvent extends GwtEvent<RemoveItemTreeIconHandler> {

    public static GwtEvent.Type<RemoveItemTreeIconHandler> TYPE = new Type<RemoveItemTreeIconHandler>();

    private Map<Item, TreeIconPosition>                    iconsToRemove;

    /** @param iconsToRemove */
    public RemoveItemTreeIconEvent(Map<Item, TreeIconPosition> iconsToRemove) {
        super();
        this.iconsToRemove = iconsToRemove;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#getAssociatedType() */
    @Override
    public GwtEvent.Type<RemoveItemTreeIconHandler> getAssociatedType() {
        return TYPE;
    }

    /** @see com.google.gwt.event.shared.GwtEvent#dispatch(com.google.gwt.event.shared.EventHandler) */
    @Override
    protected void dispatch(RemoveItemTreeIconHandler handler) {
        handler.onRemoveItemTreeIcon(this);
    }

    /** @return the iconsToRemove */
    public Map<Item, TreeIconPosition> getIconsToRemove() {
        return iconsToRemove;
    }

}
