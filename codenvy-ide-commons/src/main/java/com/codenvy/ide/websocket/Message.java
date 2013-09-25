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