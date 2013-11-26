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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.rest.AsyncRequestCallback;
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
     * @param project project name
     * @param callback
     * @throws RequestException
     */
    void runApplication(@NotNull String project, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

//    /**
//     * Run application by sending request over WebSocket.
//     *
//     * @param project
//     * @param war
//     * @param useJRebel
//     * @param callback
//     * @throws WebSocketException
//     */
//    void runApplicationWS(@NotNull String project, @NotNull String war, boolean useJRebel,
//                          @NotNull RequestCallback<ApplicationProcessDescriptor> callback) throws WebSocketException;


    /**
     * Gets logs.
     *
     * @param name
     * @param callback
     * @throws RequestException
     */
    void getLogs(@NotNull String name, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

//    /**
//     * Prolong expiration time of the application.
//     *
//     * @param name
//     *         application name
//     * @param time
//     *         time on which need to prolong expiration time of the application
//     * @param callback
//     *         {@link RequestCallback}
//     * @throws WebSocketException
//     */
//    void prolongExpirationTime(@NotNull String name, long time, @NotNull RequestCallback<Object> callback) throws WebSocketException;

//    /**
//     * Update already deployed Java web application.
//     *
//     * @param name
//     *         application name
//     * @param war
//     *         location of .war file. It may be local or remote location. File from this location will be used for update.
//     * @param callback
//     *         {@link AsyncRequestCallback}
//     * @throws RequestException
//     */
//    void updateApplication(@NotNull String name, @NotNull String war, @NotNull AsyncRequestCallback<Object> callback)
//            throws RequestException;

    /**
     * Stops application.
     *
     * @param id
     * @param callback
     * @throws RequestException
     */
    void stopApplication(@NotNull String id, @NotNull AsyncRequestCallback<String> callback) throws RequestException;
}