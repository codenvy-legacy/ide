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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for Runner.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunnerClientService.java Jul 3, 2013 12:48:08 PM azatsarynnyy $
 */
public interface RunnerClientService {

    /**
     * Run an app on the application server.
     *
     * @param projectName
     *         name of the project to run
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void run(String projectName, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get status of app.
     *
     * @param link
     *         link to get application's status
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getStatus(Link link, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Retrieve logs from application server where app is launched.
     *
     * @param link
     *         link to retrieve logs
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void getLogs(Link link, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Stop application server where app is launched.
     *
     * @param link
     *         link to stop an app
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void stop(Link link, AsyncRequestCallback<String> callback) throws RequestException;
}
