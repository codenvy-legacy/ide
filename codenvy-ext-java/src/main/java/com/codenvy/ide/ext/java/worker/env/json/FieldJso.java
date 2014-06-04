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
import com.codenvy.ide.collections.js.JsoArray;

/**
 * @author Evgen Vidolob
 */
public class FieldJso extends Jso {

    protected FieldJso() {
    }

    public final native int getModifiers() /*-{
        return this["modifiers"];
    }-*/;

    public final native Jso getConstant() /*-{
        return this["constant"];
    }-*/;

    public final native String getGenericSignature() /*-{
        return this["genericSignature"];
    }-*/;

    public final native String getName() /*-{
        return this["name"];
    }-*/;


    public final native String getTagBits() /*-{
        return this["tagBits"];
    }-*/;

    public final native String getTypeName() /*-{
        return this["typeName"];
    }-*/;

    public final native JsoArray<AnnotationJso> getAnnotations() /*-{
        return this["annotations"];
    }-*/;
}
