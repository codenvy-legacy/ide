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
package com.codenvy.ide.websocket;

import com.codenvy.ide.json.js.Jso;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.websocket.rest.Pair;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: Message.java Dec 4, 2012 3:07:48 PM azatsarynnyy $
 */
public class Message extends Jso {

    public static Message create() {
        return Jso.create().cast();
    }

    protected Message() {
    }

    /**
     * Get message body.
     *
     * @return message body
     */
    public final String getBody() {
        return getStringField("body");
    }

    /**
     * Set message body.
     *
     * @param body
     *         message body
     */
    public final void setBody(String body) {
        addField("body", body);
    }

    /**
     * Get name of HTTP method specified for resource method, e.g. GET, POST, PUT, etc.
     *
     * @return name of HTTP method
     */
    public final String getMethod() {
        return getStringField("method");
    }

    /**
     * Set name of HTTP method specified for resource method, e.g. GET, POST, PUT, etc.
     *
     * @param method
     *         name of HTTP method
     */
    public final void setMethod(String method) {
        addField("method", method);
    }

    /**
     * Get resource path.
     *
     * @return resource path
     */
    public final String getPath() {
        return getStringField("path");
    }

    /**
     * Set resource path.
     *
     * @param path
     *         resource path
     */
    public final void setPath(String path) {
        addField("path", path);
    }

    /**
     * Get HTTP headers.
     *
     * @return HTTP headers
     */
    public final JsoArray<Pair> getHeaders() {
        return getArrayField("headers").cast();
    }

    /**
     * Set HTTP headers.
     *
     * @param headers
     *         HTTP headers
     */
    public final void setHeaders(JsoArray<Pair> headers) {
        addField("headers", headers);
    }

    /**
     * Get response code.
     *
     * @return response code
     */
    public final int getResponseCode() {
        return getIntField("responseCode");
    }

    /**
     * Get response code.
     *
     * @param responseCode
     *         response code
     */
    public final void setResponseCode(int responseCode) {
        addField("responseCode", responseCode);
    }
}