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
package com.codenvy.ide.jseditor.client.popup;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/** Resources for the popup window component. */
public interface PopupResources extends ClientBundle {

    /** The CSS resource for the popup window component. */
    @Source({"popup.css", "com/codenvy/ide/api/ui/style.css"})
    PopupStyle popupStyle();

    /** The CSS resource interface for the popup window component. */
    public interface PopupStyle extends CssResource {
        String window();
        String item();
    }
}
