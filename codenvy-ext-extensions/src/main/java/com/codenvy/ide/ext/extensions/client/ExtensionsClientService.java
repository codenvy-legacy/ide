/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extensions.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import javax.validation.constraints.NotNull;

/**
 * Client service to work with Codenvy extensions.
 *
 * @author Artem Zatsarynnyy
 */
public interface ExtensionsClientService {

    /**
     * Run a specified extension project.
     *
     * @param projectName
     *         name of the extension project to launch
     * @param callback
     *         callback
     * @throws RequestException
     */
    void launch(@NotNull String projectName, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    void getStatus(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get logs of launched Codenvy application.
     *
     * @param link
     * @param callback
     *         callback
     * @throws RequestException
     */
    void getLogs(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Stop Codenvy application.
     *
     * @param link
     * @param callback
     *         callback
     * @throws RequestException
     */
    void stop(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException;
}