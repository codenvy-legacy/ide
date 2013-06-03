/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.java.jdi.client.run;

import com.codenvy.ide.ext.java.jdi.shared.ApplicationInstance;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * The client service for run java application.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
public interface ApplicationRunnerClientService {
    /**
     * Run application by sending request over Rest.
     *
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws RequestException
     */
    void runApplication(String project, String war, boolean useJRebel, AsyncRequestCallback<ApplicationInstance> callback)
            throws RequestException;

    /**
     * Run application by sending request over WebSocket.
     *
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws WebSocketException
     */
    void runApplicationWS(String project, String war, boolean useJRebel, RequestCallback<ApplicationInstance> callback)
            throws WebSocketException;

    /**
     * Run application in debug mode by sending request over Rest.
     *
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws RequestException
     */
    void debugApplication(String project, String war, boolean useJRebel, AsyncRequestCallback<ApplicationInstance> callback)
            throws RequestException;

    /**
     * Run application in debug mode by sending request over WebSocket.
     *
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws WebSocketException
     */
    void debugApplicationWS(String project, String war, boolean useJRebel, RequestCallback<ApplicationInstance> callback)
            throws WebSocketException;

    /**
     * Gets logs.
     *
     * @param name
     * @param callback
     * @throws RequestException
     */
    void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Prolong expiration time of the application.
     *
     * @param name
     *         application name
     * @param time
     *         time on which need to prolong expiration time of the application
     * @param callback
     *         {@link RequestCallback}
     * @throws WebSocketException
     */
    void prolongExpirationTime(String name, long time, RequestCallback<Object> callback) throws WebSocketException;

    /**
     * Update already deployed Java web application.
     *
     * @param name
     *         application name
     * @param war
     *         location of .war file. It may be local or remote location. File from this location will be used for update.
     * @param callback
     *         {@link AsyncRequestCallback}
     * @throws RequestException
     */
    void updateApplication(String name, String war, AsyncRequestCallback<Object> callback) throws RequestException;
}