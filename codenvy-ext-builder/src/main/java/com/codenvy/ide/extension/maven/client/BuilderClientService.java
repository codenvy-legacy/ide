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
package com.codenvy.ide.extension.maven.client;

import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Client service for Maven builder.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: BuilderClientService.java Feb 17, 2012 12:36:01 PM azatsarynnyy $
 */
public interface BuilderClientService {
    /**
     * Start new build.
     *
     * @param projectId
     *         identifier of the project we want to send for build
     * @param vfsId
     *         identifier of the virtual file system
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void build(String projectName, AsyncRequestCallback<String> callback)
            throws RequestException;

//    /**
//     * Start new build and publish.
//     *
//     * @param projectId
//     *         identifier of project we want to send for build
//     * @param vfsId
//     *         identifier of virtual file system
//     * @param callback
//     *         callback
//     * @throws RequestException
//     */
//    public void buildAndPublish(String projectId, String vfsId, String projectName, String projectType,
//                                AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Cancel previously launched build.
     *
     * @param buildid
     *         ID of build
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void cancel(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Check current status of previously launched build.
     *
     * @param buildid
     *         identifier of build
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void status(String link, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get build log.
     *
     * @param buildid
     *         identifier of build
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void log(String buildid, AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Get build result.
     *
     * @param buildid
     *         ID of build
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void result(String buildid, AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Check is URL for download artifact is valid.
     *
     * @param url
     *         URL for checking
     * @param callback
     *         callback
     * @throws RequestException
     */
    public void checkArtifactUrl(String url, AsyncRequestCallback<Object> callback) throws RequestException;
}
