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
package com.codenvy.ide.websocket.rest;

import com.codenvy.ide.collections.Jso;

/**
 * Pair that may be used to emulate headers of HTTP request/response.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: Pair.java Nov 6, 2012 12:34:44 PM azatsarynnyy $
 */
public class Pair extends Jso {

    public static Pair create() {
        return Jso.create().cast();
    }

    protected Pair() {
    }

    public final String getName() {
        return getStringField("name");
    }

    public final void setName(String name) {
        addField("name", name);
    }

    public final String getValue() {
        return getStringField("value");
    }

    public final void setValue(String value) {
        addField("value", value);
    }
}