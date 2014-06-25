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
package com.codenvy.ide.texteditor.embeddedimpl.orion.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class OrionFindIteratorOverlay extends JavaScriptObject {

    protected OrionFindIteratorOverlay() {
    }

    public final native boolean hasNext() /*-{
        return this.hasNext();
    }-*/;

    public final native OrionSelectionOverlay next() /*-{
        return this.next();
    }-*/;
}
