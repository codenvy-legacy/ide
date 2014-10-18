/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/

package com.codenvy.ide.api.event;

import com.codenvy.ide.api.projecttree.generic.ItemNode;
import com.google.gwt.event.shared.GwtEvent;

/**
 * Event that describes the fact that item operations is going.
 *
 * @author Evgen Vidolob
 */
public class ItemEvent extends GwtEvent<ItemHandler> {
    public static Type<ItemHandler> TYPE = new Type<ItemHandler>();

    private ItemNode      item;
    private ItemOperation operation;

    /**
     * Instantiates a new ItemEvent.
     *
     * @param item
     *         the item
     * @param operation
     *         the operation
     */
    public ItemEvent(ItemNode item, ItemOperation operation) {
        this.item = item;
        this.operation = operation;
    }

    public Type<ItemHandler> getAssociatedType() {
        return TYPE;
    }

    protected void dispatch(ItemHandler handler) {
        handler.onItem(this);
    }

    public ItemNode getItem() {
        return item;
    }

    public ItemOperation getOperation() {
        return operation;
    }

    public enum ItemOperation {
        CREATE, DELETE
    }
}
