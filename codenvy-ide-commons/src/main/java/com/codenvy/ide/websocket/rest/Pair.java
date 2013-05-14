/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.websocket.rest;

import com.codenvy.ide.json.js.Jso;

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