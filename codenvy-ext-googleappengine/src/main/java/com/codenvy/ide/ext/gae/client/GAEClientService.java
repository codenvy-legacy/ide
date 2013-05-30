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
    void configureBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    void cronInfo(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<CronEntry>> callback)
            throws RequestException;

    void deleteBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    void getResourceLimits(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<ResourceLimit>> callback)
            throws RequestException;

    void listBackends(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<Backend>> callback)
            throws RequestException;

    void requestLogs(String vfsId, String projectId, int numDays, String logSeverity,
                     GAEAsyncRequestCallback<StringBuilder> callback) throws RequestException;

    void rollback(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void rollbackBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    void rollbackAllBackends(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void setBackendState(String vfsId, String projectId, String backendName, String backendState,
                         GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void update(String vfsId, Project project, String bin, GAEAsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException;

    void updateAllBackends(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void updateBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;

    void updateCron(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void updateDos(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void updateIndexes(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void updatePagespeed(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void updateQueues(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    void vacuumIndexes(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException;

    String getAuthUrl();

    void logout(AsyncRequestCallback<Object> callback) throws RequestException;

    void getLoggedUser(GAEAsyncRequestCallback<GaeUser> callback) throws RequestException;

    void setApplicationId(String vfsId, String projectId, String appId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException;
}