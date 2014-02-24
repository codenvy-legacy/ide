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
package com.codenvy.ide.extension.builder.client;

import com.codenvy.api.builder.dto.BuildTaskDescriptor;
import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.rest.AsyncRequestCallback;

/**
 * Client service for builder.
 *
 * @author Artem Zatsarynnyy
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
     */
    public void build(String projectName, AsyncRequestCallback<BuildTaskDescriptor> callback);

    /**
     * Cancel previously launched build.
     *
     * @param buildId
     *         ID of build
     * @param callback
     *         callback
     */
    public void cancel(String buildId, AsyncRequestCallback<StringBuilder> callback);

    /**
     * Check current status of previously launched build.
     * <p/>
     * identifier of build
     *
     * @param callback
     *         callback
     */
    public void status(Link link, AsyncRequestCallback<String> callback);

    /** Get build log. */
    public void log(Link link, AsyncRequestCallback<String> callback);

    /**
     * Get build result.
     *
     * @param buildid
     *         ID of build
     * @param callback
     *         callback
     */
    public void result(String buildid, AsyncRequestCallback<String> callback);


}
