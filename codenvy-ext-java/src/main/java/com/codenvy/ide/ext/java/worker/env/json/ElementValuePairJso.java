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
package com.codenvy.ide.ext.java.worker.env.json;

import com.codenvy.ide.collections.Jso;

/**
 * @author Evgen Vidolob
 */
public class ElementValuePairJso extends Jso {
    protected ElementValuePairJso() {
    }

    public final native String getName() /*-{
        return this["name"];
    }-*/;

    public final native Jso getValue() /*-{
        return this["value"];
    }-*/;
}
