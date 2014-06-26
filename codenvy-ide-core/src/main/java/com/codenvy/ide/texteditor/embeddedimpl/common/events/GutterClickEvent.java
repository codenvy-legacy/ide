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
package com.codenvy.ide.texteditor.embeddedimpl.common.events;

import com.google.gwt.event.shared.GwtEvent;

/**
 * Events for clicks on the gutter element of the editor.
 * 
 * @author "Mickaël Leduque"
 */
public class GutterClickEvent extends GwtEvent<GutterClickHandler> {
    /** The type instance for this event. */
    public static Type<GutterClickHandler> TYPE = new Type<>();

    @Override
    public Type<GutterClickHandler> getAssociatedType() {
        return TYPE;
    }

    @Override
    protected void dispatch(final GutterClickHandler handler) {
        handler.onGutterClick(this);
    }

}
