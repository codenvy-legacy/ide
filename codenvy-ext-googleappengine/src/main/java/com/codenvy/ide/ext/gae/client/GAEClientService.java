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
package com.codenvy.ide.ext.gae.client;

import com.codenvy.ide.ext.gae.shared.ApplicationInfo;
import com.codenvy.ide.ext.gae.shared.GaeUser;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;

import com.codenvy.ide.ext.gae.shared.Backend;
import com.codenvy.ide.ext.gae.shared.CronEntry;
import com.codenvy.ide.ext.gae.shared.ResourceLimit;

/**
 * Client service for managing Google App Engine.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 15, 2012 5:28:26 PM anya $
 */
public interface GAEClientService {
    /**
     * Configure Google App Engine backend.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param backendName
     *         backend name to configure.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void configureBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * View information about crons entry.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void cronInfo(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<CronEntry>> callback)
            throws RequestException;

    /**
     * Delete Google App Engine backend.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param backendName
     *         backend name to delete.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void deleteBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Get information about resource limits.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void getResourceLimits(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<ResourceLimit>> callback)
            throws RequestException;

    /**
     * Get list of Google App Engine backends.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void listBackends(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<Backend>> callback)
            throws RequestException;

    /**
     * Get logs for deployed Google App Engine application.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param numDays
     *         number of days to retrieve logs.
     * @param logSeverity
     *         type of logs.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void requestLogs(String vfsId, String projectId, int numDays, String logSeverity,
                     GAEAsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Rollback deployed Google App Engine application.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void rollback(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Rollback Google App Engine backend.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param backendName
     *         backend name to rollback.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void rollbackBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Rollback all Google App Engine backends.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void rollbackAllBackends(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Set backend state. Start or Stop.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param backendName
     *         backend name to configure.
     * @param backendState
     *         backend state.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void setBackendState(String vfsId, String projectId, String backendName, String backendState,
                         GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Update deployed Google App Engine application.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param project
     *         project that opened in current moment.
     * @param bin
     *         compiled application, if exist(e.g. War).
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void update(String vfsId, Project project, String bin, GAEAsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException;

    /**
     * Update all Google App Engine backends.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void updateAllBackends(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Update Google App Engine backend.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param backendName
     *         backend name to update.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void updateBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    /**
     * Update Google App Engine cron entry.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void updateCron(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Update Google App Engine DoS.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void updateDos(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Update Google App Engine indexes.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void updateIndexes(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Update Google App Engine page speed.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void updatePagespeed(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Update Google App Engine queues.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void updateQueues(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Vacuum Google App Engine indexes.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void vacuumIndexes(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Do logout from Google Services.
     *
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void logout(AsyncRequestCallback<Object> callback) throws RequestException;

    /**
     * Get information about logged user on Google Services.
     *
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void getLoggedUser(GAEAsyncRequestCallback<GaeUser> callback) throws RequestException;

    /**
     * Set exist application id in Google App Engine configuration file.
     *
     * @param vfsId
     *         virtual fie system id.
     * @param projectId
     *         project that opened in current moment.
     * @param appId
     *         existed application id.
     * @param callback
     *         callback function.
     * @throws RequestException
     */
    void setApplicationId(String vfsId, String projectId, String appId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;
}