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
package com.codenvy.ide.ext.java.jdi.client.run;

import com.codenvy.ide.annotations.NotNull;
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
    void runApplication(@NotNull String project, @NotNull String war, boolean useJRebel,
                        @NotNull AsyncRequestCallback<ApplicationInstance> callback) throws RequestException;

    /**
     * Run application by sending request over WebSocket.
     *
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws WebSocketException
     */
    void runApplicationWS(@NotNull String project, @NotNull String war, boolean useJRebel,
                          @NotNull RequestCallback<ApplicationInstance> callback) throws WebSocketException;

    /**
     * Run application in debug mode by sending request over Rest.
     *
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws RequestException
     */
    void debugApplication(@NotNull String project, @NotNull String war, boolean useJRebel,
                          @NotNull AsyncRequestCallback<ApplicationInstance> callback) throws RequestException;

    /**
     * Run application in debug mode by sending request over WebSocket.
     *
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws WebSocketException
     */
    void debugApplicationWS(@NotNull String project, @NotNull String war, boolean useJRebel,
                            @NotNull RequestCallback<ApplicationInstance> callback) throws WebSocketException;

    /**
     * Gets logs.
     *
     * @param name
     * @param callback
     * @throws RequestException
     */
    void getLogs(@NotNull String name, @NotNull AsyncRequestCallback<StringBuilder> callback) throws RequestException;

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
    void prolongExpirationTime(@NotNull String name, long time, @NotNull RequestCallback<Object> callback) throws WebSocketException;

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
    void updateApplication(@NotNull String name, @NotNull String war, @NotNull AsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Stops application.
     *
     * @param runningApp
     * @param callback
     * @throws RequestException
     */
    void stopApplication(@NotNull ApplicationInstance runningApp, @NotNull AsyncRequestCallback<String> callback) throws RequestException;
}