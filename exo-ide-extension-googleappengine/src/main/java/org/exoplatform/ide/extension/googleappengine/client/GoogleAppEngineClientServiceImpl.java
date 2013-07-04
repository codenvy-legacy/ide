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

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
import org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendStatusHandler;
import org.exoplatform.ide.extension.googleappengine.client.backends.UpdateBackendsStatusHandler;
import org.exoplatform.ide.extension.googleappengine.client.deploy.DeployRequestStatusHandler;
import org.exoplatform.ide.extension.googleappengine.client.model.Backend;
import org.exoplatform.ide.extension.googleappengine.client.model.CronEntry;
import org.exoplatform.ide.extension.googleappengine.client.model.ResourceLimit;
import org.exoplatform.ide.extension.googleappengine.shared.ApplicationInfo;
import org.exoplatform.ide.extension.googleappengine.shared.GaeUser;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Implementation of {@link GoogleAppEngineClientService}.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 15, 2012 5:23:28 PM anya $
 */
public class GoogleAppEngineClientServiceImpl extends GoogleAppEngineClientService {

    /** Loader to be displayed. */
    private Loader loader;
    
    private final String AUTH_URL = "/ide/oauth/authenticate";

    private final String LOGOUT =  "/ide/oauth/invalidate";

    private final String APP_ENGINE = "/appengine/";

    private final String USER = APP_ENGINE + "user";

    private final String BACKEND_CONFIGURE = APP_ENGINE + "backend/configure";

    private final String CRON_INFO = APP_ENGINE + "cron/info";

    private final String BACKEND_DELETE = APP_ENGINE + "backend/delete";

    private final String RESOURCE_LIMITS = APP_ENGINE + "resource_limits";

    private final String BACKENDS_LIST = APP_ENGINE + "backends/list";

    private final String LOGS = APP_ENGINE + "logs";

    private final String ROLLBACK = APP_ENGINE + "rollback";

    private final String BACKEND_ROLLBACK = APP_ENGINE + "backend/rollback";

    private final String BACKENDS_ROLLBACK = APP_ENGINE + "backends/rollback";

    private final String BACKEND_UPDATE = APP_ENGINE + "backend/update";

    private final String BACKENDS_UPDATE_ALL = APP_ENGINE + "backends/update_all";

    private final String BACKEND_SET_STATE = APP_ENGINE + "backend/set_state";

    private final String UPDATE = APP_ENGINE + "update";

    private final String CRON_UPDATE = APP_ENGINE + "cron/update";

    private final String DOS_UPDATE = APP_ENGINE + "dos/update";

    private final String INDEXES_UPDATE = APP_ENGINE + "indexes/update";

    private final String PAGE_SPEED_UPDATE = APP_ENGINE + "pagespeed/update";

    private final String QUEUES_UPDATE = APP_ENGINE + "queues/update";

    private final String VACUUM_INDEXES = APP_ENGINE + "vacuum_indexes";

    private final String SET_APP_ID = APP_ENGINE + "change-appid";

    private final MessageBus wsMessageBus;

    private final String restContext;

    private final String wsName;

    /**
     * @param restService
     *         REST service context
     * @param loader
     *         loader to be displayed on request
     */
    public GoogleAppEngineClientServiceImpl(String restContext, String wsName, Loader loader,  MessageBus wsMessageBus) {
        this.restContext = restContext + wsName;
        this.wsName = wsName;
        this.loader = loader;
        this.wsMessageBus = wsMessageBus;
       
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#configureBackend(String,
     *      String, String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void configureBackend(String vfsId, String projectId, String backendName,
                                 GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restContext + BACKEND_CONFIGURE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#cronInfo(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void cronInfo(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<List<CronEntry>> callback)
            throws RequestException {
        String url = restContext + CRON_INFO;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#deleteBackend(String,
     *      String, String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void deleteBackend(String vfsId, String projectId, String backendName,
                              GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restContext + BACKEND_DELETE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);

    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#getResourceLimits(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void getResourceLimits(String vfsId, String projectId,
                                  GoogleAppEngineAsyncRequestCallback<List<ResourceLimit>> callback) throws RequestException {
        String url = restContext + RESOURCE_LIMITS;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#listBackends(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void listBackends(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<List<Backend>> callback)
            throws RequestException {
        String url = restContext + BACKENDS_LIST;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#requestLogs(String,
     *      String, int, String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void requestLogs(String vfsId, String projectId, int numDays, String logSeverity,
                            GoogleAppEngineAsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restContext + LOGS;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&num_days=")
              .append(numDays);

        if (logSeverity != null && !logSeverity.isEmpty()) {
            params.append("&log_severity=").append(logSeverity);
        }

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).send(callback);

    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#rollback(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void rollback(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + ROLLBACK;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#rollbackBackend(String,
     *      String, String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void rollbackBackend(String vfsId, String projectId, String backendName,
                                GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restContext + BACKEND_ROLLBACK;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#rollbackAllBackends(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void rollbackAllBackends(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + BACKENDS_ROLLBACK;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#setBackendState(String,
     *      String, String, String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void setBackendState(String vfsId, String projectId, String backendName, String backendState,
                                GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restContext + BACKEND_SET_STATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName).append("&backend_state=").append(backendState);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#update(String,
     *      ProjectModel, String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void update(String vfsId, ProjectModel project, String bin,
                       GoogleAppEngineWsRequestCallback<ApplicationInfo> callback) throws RequestException {
        String url = wsName + UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(project.getId());
        if (bin != null && !bin.isEmpty()) {
            params.append("&bin=").append(bin);
        }

        callback.setStatusHandler(new DeployRequestStatusHandler(project.getName()));
        RequestMessage message = RequestMessageBuilder.build(RequestBuilder.GET, url + params)
                                                      .getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateAllBackends(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void updateAllBackends(String vfsId, String projectId, GoogleAppEngineWsRequestCallback<Object> callback)
            throws RequestException {
        String url = wsName + BACKENDS_UPDATE_ALL;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);
        
        callback.setStatusHandler(new UpdateBackendsStatusHandler());
        RequestMessage message = RequestMessageBuilder.build(RequestBuilder.GET, url + params)
                                                      .getRequestMessage();
        wsMessageBus.send(message, callback);

    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateBackend(String,
     *      String, String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void updateBackend(String vfsId, String projectId, String backendName,
                              GoogleAppEngineWsRequestCallback<Object> callback) throws RequestException {
        String url = wsName + BACKEND_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);
        
        callback.setStatusHandler(new UpdateBackendStatusHandler(backendName));
        RequestMessage message = RequestMessageBuilder.build(RequestBuilder.GET, url + params)
                                                      .getRequestMessage();
        wsMessageBus.send(message, callback);

        
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateCron(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void updateCron(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + CRON_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateDos(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void updateDos(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + DOS_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateIndexes(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void updateIndexes(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + INDEXES_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updatePagespeed(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void updatePagespeed(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + PAGE_SPEED_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#updateQueues(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void updateQueues(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + QUEUES_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#vacuumIndexes(String,
     *      String, GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void vacuumIndexes(String vfsId, String projectId, GoogleAppEngineAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restContext + VACUUM_INDEXES;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#getAuthUrl() */
    @Override
    public String getAuthUrl() {
        return Utils.getRestContext() + AUTH_URL +
               "?oauth_provider=google&scope=https://www.googleapis.com/auth/appengine.admin&redirect_after_login=/success_oauth.html";
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#logout(AsyncRequestCallback) */
    @Override
    public void logout(AsyncRequestCallback<Object> callback) throws RequestException {
        String url = Utils.getRestContext() + LOGOUT + "?oauth_provider=google";

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /**
     * @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#setApplicationId(String,
     *      String, String,
     *      org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback)
     */
    @Override
    public void setApplicationId(String vfsId, String projectId, String appId,
                                 GoogleAppEngineAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restContext + SET_APP_ID + "/" + vfsId + "/" + projectId;

        StringBuilder params = new StringBuilder("?");
        params.append("app_id=").append("s~").append(appId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** @see org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService#getLoggedUser(GoogleAppEngineAsyncRequestCallback) */
    @Override
    public void getLoggedUser(GoogleAppEngineAsyncRequestCallback<GaeUser> callback) throws RequestException {
        String url = restContext + USER;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }
}
