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
