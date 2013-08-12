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
package com.codenvy.ide.api.event;

import com.codenvy.ide.api.selection.Selection;
import com.google.gwt.event.shared.GwtEvent;


/**
 * Event that notifies of changed Core Expressions
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
public class SelectionChangedEvent extends GwtEvent<SelectionChangedHandler> {
    public static Type<SelectionChangedHandler> TYPE = new Type<SelectionChangedHandler>();

    private final Selection<?> selection;

    /**
     * @param expressions
     *         the map of ID's and current values
     */
    public SelectionChangedEvent(Selection<?> selection) {
        this.selection = selection;
    }

    @Override
    public Type<SelectionChangedHandler> getAssociatedType() {
        return TYPE;
    }

    /** @return current selection */
    public Selection<?> getSelection() {
        return selection;
    }

    @Override
    protected void dispatch(SelectionChangedHandler handler) {
        handler.onSelectionChanged(this);
    }
}
