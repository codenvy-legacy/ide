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

import com.codenvy.ide.ext.gae.client.backends.UpdateBackendStatusHandler;
import com.codenvy.ide.ext.gae.client.backends.UpdateBackendsStatusHandler;
import com.codenvy.ide.ext.gae.client.deploy.DeployRequestStatusHandler;
import com.codenvy.ide.ext.gae.shared.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Implementation of {@link GAEClientService}.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 15, 2012 5:23:28 PM anya $
 */
@Singleton
public class GAEClientServiceImpl implements GAEClientService {
    /** REST service context. */
    private String restServiceContext;
    /** Loader to be displayed. */
    private Loader loader;
    private final String LOGOUT              = "rest/ide/oauth/invalidate";
    private final String APP_ENGINE          = "/appengine/";
    private final String USER                = APP_ENGINE + "user";
    private final String BACKEND_CONFIGURE   = APP_ENGINE + "backend/configure";
    private final String CRON_INFO           = APP_ENGINE + "cron/info";
    private final String BACKEND_DELETE      = APP_ENGINE + "backend/delete";
    private final String RESOURCE_LIMITS     = APP_ENGINE + "resource_limits";
    private final String BACKENDS_LIST       = APP_ENGINE + "backends/list";
    private final String LOGS                = APP_ENGINE + "logs";
    private final String ROLLBACK            = APP_ENGINE + "rollback";
    private final String BACKEND_ROLLBACK    = APP_ENGINE + "backend/rollback";
    private final String BACKENDS_ROLLBACK   = APP_ENGINE + "backends/rollback";
    private final String BACKEND_UPDATE      = APP_ENGINE + "backend/update";
    private final String BACKENDS_UPDATE_ALL = APP_ENGINE + "backends/update_all";
    private final String BACKEND_SET_STATE   = APP_ENGINE + "backend/set_state";
    private final String UPDATE              = APP_ENGINE + "update";
    private final String CRON_UPDATE         = APP_ENGINE + "cron/update";
    private final String DOS_UPDATE          = APP_ENGINE + "dos/update";
    private final String INDEXES_UPDATE      = APP_ENGINE + "indexes/update";
    private final String PAGE_SPEED_UPDATE   = APP_ENGINE + "pagespeed/update";
    private final String QUEUES_UPDATE       = APP_ENGINE + "queues/update";
    private final String VACUUM_INDEXES      = APP_ENGINE + "vacuum_indexes";
    private final String SET_APP_ID          = APP_ENGINE + "change-appid";
    private       EventBus        eventBus;
    private       GAELocalization constant;
    private final String          wsName;

    /**
     * Create client service.
     */
    @Inject
    protected GAEClientServiceImpl(@Named("restContext") String restContext, Loader loader, EventBus eventBus,
                                   GAELocalization constant) {
        this.restServiceContext = restContext;
        this.loader = loader;
        this.eventBus = eventBus;
        this.constant = constant;
        this.wsName = Utils.getWorkspaceName();
    }

    /** {@inheritDoc} */
    @Override
    public void configureBackend(String vfsId, String projectId, String backendName,
                                 GAEAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKEND_CONFIGURE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void cronInfo(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<CronEntry>> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + CRON_INFO;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteBackend(String vfsId, String projectId, String backendName,
                              GAEAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKEND_DELETE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);

    }

    /** {@inheritDoc} */
    @Override
    public void getResourceLimits(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<ResourceLimit>> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + RESOURCE_LIMITS;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void listBackends(String vfsId, String projectId, GAEAsyncRequestCallback<JsonArray<Backend>> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKENDS_LIST;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void requestLogs(String vfsId, String projectId, int numDays, String logSeverity,
                            GAEAsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + LOGS;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&num_days=")
              .append(numDays);

        if (logSeverity != null && !logSeverity.isEmpty()) {
            params.append("&log_severity=").append(logSeverity);
        }

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.TEXT_PLAIN).send(callback);

    }

    /** {@inheritDoc} */
    @Override
    public void rollback(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + ROLLBACK;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params, true).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void rollbackBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKEND_ROLLBACK;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void rollbackAllBackends(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKENDS_ROLLBACK;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void setBackendState(String vfsId, String projectId, String backendName, String backendState,
                                GAEAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKEND_SET_STATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName).append("&backend_state=").append(backendState);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void update(String vfsId, Project project, String bin, GAEAsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(project.getId());
        if (bin != null && !bin.isEmpty()) {
            params.append("&bin=").append(bin);
        }

        AsyncRequest.build(RequestBuilder.GET, url + params, true).delay(2000)
                    .requestStatusHandler(new DeployRequestStatusHandler(project.getName(), eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateAllBackends(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKENDS_UPDATE_ALL;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params, true).delay(2000)
                    .requestStatusHandler(new UpdateBackendsStatusHandler(eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateBackend(String vfsId, String projectId, String backendName, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + BACKEND_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId).append("&backend_name=")
              .append(backendName);

        AsyncRequest.build(RequestBuilder.GET, url + params, true).delay(2000)
                    .requestStatusHandler(new UpdateBackendStatusHandler(backendName, eventBus, constant)).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateCron(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + CRON_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateDos(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + DOS_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateIndexes(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + INDEXES_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updatePagespeed(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + PAGE_SPEED_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateQueues(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + QUEUES_UPDATE;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void vacuumIndexes(String vfsId, String projectId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + VACUUM_INDEXES;

        StringBuilder params = new StringBuilder("?");
        params.append("vfsid=").append(vfsId).append("&projectid=").append(projectId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void logout(AsyncRequestCallback<Object> callback) throws RequestException {
        String url = LOGOUT + "?oauth_provider=google";

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void setApplicationId(String vfsId, String projectId, String appId, GAEAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + '/' + wsName + SET_APP_ID + "/" + vfsId + "/" + projectId;

        StringBuilder params = new StringBuilder("?");
        params.append("app_id=").append("s~").append(appId);

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLoggedUser(GAEAsyncRequestCallback<GaeUser> callback) throws RequestException {
        String url = restServiceContext + '/' + wsName + USER;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }
}