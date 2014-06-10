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
public class MethodJso extends Jso {

    protected MethodJso() {
    }

    public final native int getModifiers() /*-{
        return this["modifiers"];
    }-*/;

    public final native boolean isConstructor() /*-{
        return this["constructor"];
    }-*/;

    public final native JsoArray<String> getArgumentNames() /*-{
        return this["argumentNames"];
    }-*/;

    public final native JsoArray<AnnotationJso> getAnnotations() /*-{
        return this["annotations"];
    }-*/;

    public final native Jso getDefaultValue() /*-{
        return this["defaultValue"];
    }-*/;

    public final native JsoArray<String> getExceptionTypeNames() /*-{
        return this["exceptionTypeNames"];
    }-*/;

    public final native String getGenericSignature() /*-{
        return this["genericSignature"];
    }-*/;

    public final native String getMethodDescriptor() /*-{
        return this["methodDescriptor"];
    }-*/;

    public final native JsoArray<JsoArray<AnnotationJso>> getParameterAnnotations() /*-{
        return this["parameterAnnotations"];
    }-*/;

    public final native String getSelector() /*-{
        return this["selector"];
    }-*/;

    public final native String getTagBits() /*-{
        return this["tagBits"];
    }-*/;

    public final native boolean isClinit() /*-{
        return this["clinit"];
    }-*/;

}
