/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.ext.java.worker.env.json;

import com.codenvy.ide.collections.Jso;

/**
 * @author Evgen Vidolob
 */
public class MemberTypesJso extends Jso{

    protected MemberTypesJso() {
    }

    public final native String getEnclosingTypeName() /*-{
        return this["enclosingTypeName"];
    }-*/;


    public final native int getModifiers() /*-{
        return this["modifiers"];
    }-*/;

    public final native String getName() /*-{
        return this["name"];
    }-*/;

}
