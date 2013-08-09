/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.ext.cloudbees.client;

import com.codenvy.ide.ext.cloudbees.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.ext.cloudbees.shared.CloudBeesAccount;
import com.codenvy.ide.ext.cloudbees.shared.CloudBeesUser;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Implementation for {@link CloudBeesClientService}.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesServiceImpl.java Jun 23, 2011 10:11:33 AM vereshchaka $
 */
@Singleton
public class CloudBeesClientServiceImpl implements CloudBeesClientService {
    private static final String BASE_URL    = '/' + Utils.getWorkspaceName() + "/cloudbees";
    private static final String DOMAINS     = BASE_URL + "/domains";
    private static final String DEPLOY_WAR  = BASE_URL + "/apps/create";
    private static final String ACCOUNTS    = BASE_URL + "/accounts";
    private static final String USERS       = "/users";
    private static final String APPS_INFO   = BASE_URL + "/apps/info";
    private static final String APPS_DELETE = BASE_URL + "/apps/delete";
    private static final String APPS_UPDATE = BASE_URL + "/apps/update";
    private static final String LOGIN       = BASE_URL + "/login";
    private static final String LOGOUT      = BASE_URL + "/logout";
    private static final String INITIALIZE  = BASE_URL + "/apps/create";
    private static final String APP_LIST    = BASE_URL + "/apps/all";
    /** REST service context. */
    private String                        restServiceContext;
    /** Loader to be displayed. */
    private Loader                        loader;
    /** WebSocket message bus. */
    private MessageBus                    wsMessageBus;
    private EventBus                      eventBus;
    private CloudBeesLocalizationConstant constant;

    /**
     * Create client service.
     *
     * @param restContext
     * @param loader
     * @param wsMessageBus
     * @param eventBus
     * @param constant
     */
    @Inject
    protected CloudBeesClientServiceImpl(@Named("restContext") String restContext, Loader loader, MessageBus wsMessageBus,
                                         EventBus eventBus, CloudBeesLocalizationConstant constant) {
        this.loader = loader;
        this.restServiceContext = restContext;
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
    }

    /** {@inheritDoc} */
    @Override
    public void deployWar(String appId, String warFile, String message, CloudBeesAsyncRequestCallback<Map<String, String>> callback)
            throws RequestException {
        final String url = restServiceContext + DEPLOY_WAR;

        String params = "appid=" + appId + "&";
        params += "war=" + warFile;
        if (message != null && !message.isEmpty()) {
            params += "&message=" + message;
        }

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getDomains(CloudBeesAsyncRequestCallback<JsonArray<String>> callback) throws RequestException {
        final String url = restServiceContext + DOMAINS;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void login(String email, String password, AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + LOGIN;

        DtoClientImpls.CredentialsImpl credentials = DtoClientImpls.CredentialsImpl.make();
        credentials.setEmail(email);
        credentials.setPassword(password);
        String serialize = credentials.serialize();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(serialize)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void logout(AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + LOGOUT;

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getApplicationInfo(String appId, String vfsId, String projectId, CloudBeesAsyncRequestCallback<ApplicationInfo> callback)
            throws RequestException {
        final String url = restServiceContext + APPS_INFO;

        String params = (appId != null) ? "appid=" + appId + "&" : "";
        params += "vfsid=" + vfsId;
        params += (projectId != null) ? "&projectid=" + projectId : "";

        AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void deleteApplication(String appId, String vfsId, String projectId,
                                  CloudBeesAsyncRequestCallback<String> callback) throws RequestException {
        final String url = restServiceContext + APPS_DELETE;

        List<String> paramList = new ArrayList<String>();
        if (appId != null)
            paramList.add("appid=" + appId);
        if (vfsId != null)
            paramList.add("vfsid=" + vfsId);
        if (projectId != null)
            paramList.add("projectid=" + projectId);

        String params;

        if (paramList.size() < 1) {
            params = "";
        } else {
            params = "?" + paramList.get(0);
            if (paramList.size() > 1) {
                for (int i = 1; i < paramList.size(); i++) {
                    params += "&" + paramList.get(i);
                }
            }
        }

        AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void initializeApplication(String appId, String vfsId, String projectId, String warFile, String message,
                                      CloudBeesAsyncRequestCallback<ApplicationInfo> callback) throws RequestException {
        final String url = restServiceContext + INITIALIZE;

        String params = "appid=" + appId + "&";
        params += "war=" + warFile;
        params += "&vfsid=" + vfsId;
        params += (projectId != null) ? "&projectid=" + projectId : "";
        if (message != null && !message.isEmpty())
            params += "&message=" + message;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params, true)
                    .requestStatusHandler(new CreateApplicationRequestHandler(appId, eventBus, constant))
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void initializeApplicationWS(String appId, String vfsId, String projectId, String warFile, String message,
                                        CloudBeesRESTfulRequestCallback<ApplicationInfo> callback) throws WebSocketException {
        String params = "?appid=" + appId + "&";
        params += "war=" + warFile;
        params += "&vfsid=" + vfsId;
        params += (projectId != null) ? "&projectid=" + projectId : "";
        if (message != null && !message.isEmpty()) {
            params += "&message=" + message;
        }
        callback.setStatusHandler(new CreateApplicationRequestHandler(appId, eventBus, constant));
        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, INITIALIZE + params);
        builder.header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON);

        Message requestMessage = builder.build();
        wsMessageBus.send(requestMessage, callback);
    }

    /** {@inheritDoc} */
    @Override
    public void applicationList(CloudBeesAsyncRequestCallback<JsonArray<ApplicationInfo>> callback) throws RequestException {
        final String url = restServiceContext + APP_LIST;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void updateApplication(String appId, String vfsId, String projectId, String warFile, String message,
                                  CloudBeesAsyncRequestCallback<ApplicationInfo> callback) throws RequestException {
        final String url = restServiceContext + APPS_UPDATE;

        String params = "appid=" + appId + "&";
        params += "war=" + warFile;
        params += "&vfsid=" + vfsId;
        params += (projectId != null) ? "&projectid=" + projectId : "";
        if (message != null && !message.isEmpty())
            params += "&message=" + message;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createAccount(CloudBeesAccount account, AsyncRequestCallback<CloudBeesAccount> callback) throws RequestException {
        String url = restServiceContext + ACCOUNTS;

        DtoClientImpls.CloudBeesAccountImpl json = (DtoClientImpls.CloudBeesAccountImpl)account;
        String data = json.serialize();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void addUserToAccount(String account, CloudBeesUser user, boolean isExisting, AsyncRequestCallback<CloudBeesUser> callback)
            throws RequestException {
        StringBuilder url = new StringBuilder(restServiceContext);
        url.append(ACCOUNTS).append("/").append(account).append(USERS);
        url.append("?existing_user=").append(isExisting);

        DtoClientImpls.CloudBeesUserImpl json = (DtoClientImpls.CloudBeesUserImpl)user;
        String data = json.serialize();

        AsyncRequest.build(RequestBuilder.POST, url.toString()).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).data(data).send(callback);
    }
}