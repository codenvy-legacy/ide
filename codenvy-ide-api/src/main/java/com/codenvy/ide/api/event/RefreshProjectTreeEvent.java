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

import com.google.gwt.event.shared.GwtEvent;

/**
 * Event for refreshing project tree.
 * <p/>
 * Note that refresh will be performed for the selected node's parent node.
 *
 * @author Artem Zatsarynnyy
 */
public class RefreshProjectTreeEvent extends GwtEvent<RefreshProjectTreeHandler> {

    public static Type<RefreshProjectTreeHandler> TYPE = new Type<>();

    @Override
    public Type<RefreshProjectTreeHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(RefreshProjectTreeHandler handler) {
        handler.onRefresh(this);
    }
}
