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
package org.exoplatform.ide.extension.googleappengine.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.model.Backend;
import org.exoplatform.ide.extension.googleappengine.client.model.CronEntry;
import org.exoplatform.ide.extension.googleappengine.client.model.ResourceLimit;
import org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo;
import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Client service for managing Google App Engine.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 15, 2012 5:28:26 PM anya $
 */
public abstract class GoogleAppEngineClientService {
    /** Instance of the service. */
    private static GoogleAppEngineClientService instance;

    /**
     * Returns the instance of {@link GoogleAppEngineClientService}.
     *
     * @return {@link GoogleAppEngineClientService} instance
     */
    public static GoogleAppEngineClientService getInstance() {
        return instance;
    }

    protected GoogleAppEngineClientService() {
        instance = this;
    }

    public abstract void configureBackend(String vfsId, String projectId, String backendName,
                                          GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void cronInfo(String vfsId, String projectId,
                                  GoogleAppEngineAsyncRequestCallback<List<CronEntry>> callback) throws RequestException;

    public abstract void deleteBackend(String vfsId, String projectId, String backendName,
                                       GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void getResourceLimits(String vfsId, String projectId,
                                           GoogleAppEngineAsyncRequestCallback<List<ResourceLimit>> callback) throws RequestException;

    public abstract void listBackends(String vfsId, String projectId,
                                      GoogleAppEngineAsyncRequestCallback<List<Backend>> callback) throws RequestException;

    public abstract void requestLogs(String vfsId, String projectId, int numDays, String logSeverity,
                                     GoogleAppEngineAsyncRequestCallback<StringBuilder> callback) throws RequestException;

    public abstract void rollback(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException;

    public abstract void rollbackBackend(String vfsId, String projectId, String backendName,
                                         GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void rollbackAllBackends(String vfsId, String projectId,
                                             GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void setBackendState(String vfsId, String projectId, String backendName, String backendState,
                                         GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void update(String vfsId, ProjectModel project, String bin,
                                GoogleAppEngineWsRequestCallback<ApplicationInfo> callback) throws RequestException;

    public abstract void updateAllBackends(String vfsId, String projectId,
                                           GoogleAppEngineWsRequestCallback<Object> callback) throws RequestException;

    public abstract void updateBackend(String vfsId, String projectId, String backendName,
                                       GoogleAppEngineWsRequestCallback<Object> callback) throws RequestException;

    public abstract void updateCron(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException;

    public abstract void updateDos(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException;

    public abstract void updateIndexes(String vfsId, String projectId,
                                       GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void updatePagespeed(String vfsId, String projectId,
                                         GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void updateQueues(String vfsId, String projectId,
                                      GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void vacuumIndexes(String vfsId, String projectId,
                                       GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;

    public abstract String getAuthUrl();

    public abstract void logout(AsyncRequestCallback<Object> callback) throws RequestException;

    public abstract void getLoggedUser(GoogleAppEngineAsyncRequestCallback<GaeUser> callback) throws RequestException;

    public abstract void setApplicationId(String vfsId, String projectId, String appId,
                                          GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException;
}
