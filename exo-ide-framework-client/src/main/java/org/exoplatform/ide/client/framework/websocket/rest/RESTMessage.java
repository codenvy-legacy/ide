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
package org.exoplatform.ide.client.framework.websocket.rest;

import org.exoplatform.ide.client.framework.websocket.Message;

import java.util.List;

/**
 * RESTful messages.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: RESTMessage.java Nov 8, 2012 6:29:19 PM azatsarynnyy $
 */
public interface RESTMessage extends Message {
    /**
     * Get name of HTTP method specified for resource method, e.g. GET, POST, PUT, etc.
     *
     * @return name of HTTP method
     */
    String getMethod();

    /**
     * Set name of HTTP method specified for resource method, e.g. GET, POST, PUT, etc.
     *
     * @param method
     *         name of HTTP method
     */
    void setMethod(String method);

    /**
     * Get resource path.
     *
     * @return resource path
     */
    String getPath();

    /**
     * Set resource path.
     *
     * @param path
     *         resource path
     */
    void setPath(String path);

    /**
     * Get HTTP headers.
     *
     * @return HTTP headers
     */
    List<Pair> getHeaders();

    /**
     * Set HTTP headers.
     *
     * @param headers
     *         HTTP headers
     */
    void setHeaders(List<Pair> headers);
}
