/*
 * Copyright (C) 2012 eXo Platform SAS.
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
