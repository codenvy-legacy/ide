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
package org.exoplatform.ide.extension.appfog.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBeanCodex;
import com.google.web.bindery.autobean.shared.AutoBeanUtils;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
import org.exoplatform.ide.extension.appfog.shared.*;
import org.exoplatform.ide.extension.cloudfoundry.shared.Credentials;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogClientService {
    private static final String BASE_URL = Utils.getWorkspaceName() + "/appfog";

    private static final String CREATE = BASE_URL + "/apps/create";

    private static final String FRAMEWORKS = BASE_URL + "/info/frameworks";

    private static final String START = BASE_URL + "/apps/start";

    private static final String RESTART = BASE_URL + "/apps/restart";

    private static final String DELETE = BASE_URL + "/apps/delete";

    private static final String STOP = BASE_URL + "/apps/stop";

    private static final String LOGIN = BASE_URL + "/login";

    private static final String LOGOUT = BASE_URL + "/logout";

    private static final String APPS_INFO = BASE_URL + "/apps/info";

    private static final String UPDATE = BASE_URL + "/apps/update";

    private static final String RENAME = BASE_URL + "/apps/rename";

    private static final String MAP_URL = BASE_URL + "/apps/map";

    private static final String SYSTEM_INFO_URL = BASE_URL + "/info/system";

    private static final String UNMAP_URL = BASE_URL + "/apps/unmap";

    private static final String UPDATE_MEMORY = BASE_URL + "/apps/mem";

    private static final String UPDATE_INSTANCES = BASE_URL + "/apps/instances";

    private static final String VALIDATE_ACTION = BASE_URL + "/apps/validate-action";

    private static final String APPS = BASE_URL + "/apps";

    private static final String TARGETS = BASE_URL + "/target/all";

    private static final String TARGET = BASE_URL + "/target";

    private static final String SERVICES = BASE_URL + "/services";

    private static final String SERVICES_CREATE = SERVICES + "/create";

    private static final String SERVICES_DELETE = SERVICES + "/delete";

    private static final String SERVICES_BIND = SERVICES + "/bind";

    private static final String SERVICES_UNBIND = SERVICES + "/unbind";

    private static final String LOGS = BASE_URL + "/apps/logs";

    private static final String INFRAS = BASE_URL + "/infras";

    private Loader loader;

    private String restServiceContext;

    private MessageBus wsMessageBus;

    //------------------------------------------------------------------
    private static AppfogClientService instance;

    private  String wsName;

    public static AppfogClientService getInstance() {
        return instance;
    }

    protected AppfogClientService() {
        instance = this;
    }

    //------------------------------------------------------------------

    public AppfogClientService(String restContext, String wsName, Loader loader, MessageBus wsMessageBus) {
        this.wsName = wsName;
        this.loader = loader;
        this.restServiceContext = restContext;
        this.wsMessageBus = wsMessageBus;
        instance = this;
    }

    //-------------------------------------------------------------------

    public void getFrameworks(AsyncRequestCallback<List<Framework>> callback, String server) throws RequestException {
        String url = restServiceContext + FRAMEWORKS;
        url += (server != null) ? "?server=" + server : "";
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void create(String server, String name, String type, String url, int instances, int memory, boolean nostart,
                       String vfsId, String projectId, String war, String infra, AppfogAsyncRequestCallback<AppfogApplication> callback)
            throws RequestException {
        final String requestUrl = restServiceContext + CREATE;

        server = checkServerUrl(server);

        CreateAppfogApplicationRequest createApplicationRequest =
                AppfogExtension.AUTO_BEAN_FACTORY.createAppfogApplicationRequest().as();
        createApplicationRequest.setName(name);
        createApplicationRequest.setServer(server);
        createApplicationRequest.setType(type);
        createApplicationRequest.setUrl(url);
        createApplicationRequest.setInstances(instances);
        createApplicationRequest.setMemory(memory);
        createApplicationRequest.setNostart(nostart);
        createApplicationRequest.setVfsid(vfsId);
        createApplicationRequest.setProjectid(projectId);
        createApplicationRequest.setWar(war);
        createApplicationRequest.setInfra(infra);

        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(createApplicationRequest)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
                    .requestStatusHandler(new CreateApplicationRequestStatusHandler(name)).data(data)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void createWS(String server, String name, String type, String url, int instances, int memory,
                         boolean nostart, String vfsId, String projectId, String war, String infra,
                         AppfogRESTfulRequestCallback<AppfogApplication> callback) throws WebSocketException {
        server = checkServerUrl(server);

        CreateAppfogApplicationRequest createApplicationRequest =
                AppfogExtension.AUTO_BEAN_FACTORY.createAppfogApplicationRequest().as();
        createApplicationRequest.setName(name);
        createApplicationRequest.setServer(server);
        createApplicationRequest.setType(type);
        createApplicationRequest.setUrl(url);
        createApplicationRequest.setInstances(instances);
        createApplicationRequest.setMemory(memory);
        createApplicationRequest.setNostart(nostart);
        createApplicationRequest.setVfsid(vfsId);
        createApplicationRequest.setProjectid(projectId);
        createApplicationRequest.setWar(war);
        createApplicationRequest.setInfra(infra);

        String data = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(createApplicationRequest)).getPayload();
        callback.setStatusHandler(new CreateApplicationRequestStatusHandler(name));
        RequestMessage message =
                RequestMessageBuilder.build(RequestBuilder.POST, CREATE).data(data)
                                     .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    public void login(String server, String email, String password, AsyncRequestCallback<String> callback)
            throws RequestException {
        String url = restServiceContext + LOGIN;

        server = checkServerUrl(server);

        Credentials credentialsBean = AppfogExtension.AUTO_BEAN_FACTORY.credentials().as();
        credentialsBean.setServer(server);
        credentialsBean.setEmail(email);
        credentialsBean.setPassword(password);
        String credentials = AutoBeanCodex.encode(AutoBeanUtils.getAutoBean(credentialsBean)).getPayload();

        AsyncRequest.build(RequestBuilder.POST, url).loader(loader).data(credentials)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void logout(String server, AsyncRequestCallback<String> callback) throws RequestException {
        String url = restServiceContext + LOGOUT;

        server = checkServerUrl(server);

        String params = (server != null) ? "?server=" + server : "";

        AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader).send(callback);
    }

    public void getApplicationInfo(String vfsId, String projectId, String appId, String server,
                                   AppfogAsyncRequestCallback<AppfogApplication> callback) throws RequestException {
        final String url = restServiceContext + APPS_INFO;

        server = checkServerUrl(server);

        String params = (appId != null) ? "name=" + appId : "";
        params += (vfsId != null) ? "&vfsid=" + vfsId : "";
        params += (projectId != null) ? "&projectid=" + projectId : "";
        params += (server != null) ? "&server=" + server : "";
        params = (params.startsWith("&")) ? params.substring(1) : params;

        AsyncRequest.build(RequestBuilder.GET, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void deleteApplication(String vfsId, String projectId, String appId, String server, boolean deleteServices,
                                  AppfogAsyncRequestCallback<String> callback) throws RequestException {
        final String url = restServiceContext + DELETE;

        server = checkServerUrl(server);

        String params = (appId != null) ? "name=" + appId + "&" : "";
        params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
        params += (projectId != null) ? "projectid=" + projectId + "&" : "";
        params += (server != null) ? "server=" + server + "&" : "";
        params += "delete-services=" + String.valueOf(deleteServices);

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void startApplication(String vfsId, String projectId, String name, String server,
                                 AppfogAsyncRequestCallback<AppfogApplication> callback) throws RequestException {
        final String url = restServiceContext + START;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name : "";
        params += (vfsId != null) ? "&vfsid=" + vfsId : "";
        params += (projectId != null) ? "&projectid=" + projectId : "";
        params += (server != null) ? "&server=" + server : "";
        params = (params.startsWith("&")) ? params.substring(1) : params;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void stopApplication(String vfsId, String projectId, String name, String server,
                                AppfogAsyncRequestCallback<String> callback) throws RequestException {
        final String url = restServiceContext + STOP;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name : "";
        params += (vfsId != null) ? "&vfsid=" + vfsId : "";
        params += (projectId != null) ? "&projectid=" + projectId : "";
        params += (server != null) ? "&server=" + server : "";
        params = (params.startsWith("&")) ? params.substring(1) : params;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void restartApplication(String vfsId, String projectId, String name, String server,
                                   AppfogAsyncRequestCallback<AppfogApplication> callback) throws RequestException {
        final String url = restServiceContext + RESTART;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name : "";
        params += (vfsId != null) ? "&vfsid=" + vfsId : "";
        params += (projectId != null) ? "&projectid=" + projectId : "";
        params += (server != null) ? "&server=" + server : "";
        params = (params.startsWith("&")) ? params.substring(1) : params;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void updateApplication(String vfsId, String projectId, String name, String server, String war,
                                  AppfogAsyncRequestCallback<String> callback) throws RequestException {
        final String url = restServiceContext + UPDATE;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name : "";
        params += (vfsId != null) ? "&vfsid=" + vfsId : "";
        params += (projectId != null) ? "&projectid=" + projectId : "";
        params += (server != null) ? "&server=" + server : "";
        params += (war != null) ? "&war=" + war : "";
        params = (params.startsWith("&")) ? params.substring(1) : params;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void renameApplication(String vfsId, String projectId, String name, String server, String newName,
                                  AppfogAsyncRequestCallback<String> callback) throws RequestException {
        //Doesn't realized in rest
        final String url = restServiceContext + RENAME;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name + "&" : "";
        params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
        params += (projectId != null) ? "projectid=" + projectId + "&" : "";
        params += (server != null) ? "server=" + server + "&" : "";
        params += "newname=" + newName;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void mapUrl(String vfsId, String projectId, String name, String server, String url,
                       AppfogAsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = restServiceContext + MAP_URL;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name + "&" : "";
        params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
        params += (projectId != null) ? "projectid=" + projectId + "&" : "";
        params += (server != null) ? "server=" + server + "&" : "";
        params += "url=" + url;

        AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void unmapUrl(String vfsId, String projectId, String name, String server, String url,
                         AppfogAsyncRequestCallback<Object> callback) throws RequestException {
        final String requestUrl = restServiceContext + UNMAP_URL;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name + "&" : "";
        params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
        params += (projectId != null) ? "projectid=" + projectId + "&" : "";
        params += (server != null) ? "server=" + server + "&" : "";
        params += "url=" + url;

        AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void updateMemory(String vfsId, String projectId, String name, String server, int mem,
                             AppfogAsyncRequestCallback<String> callback) throws RequestException {
        final String url = restServiceContext + UPDATE_MEMORY;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name + "&" : "";
        params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
        params += (projectId != null) ? "projectid=" + projectId + "&" : "";
        params += (server != null) ? "server=" + server + "&" : "";
        params += "mem=" + mem;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void updateInstances(String vfsId, String projectId, String name, String server, String expression,
                                AppfogAsyncRequestCallback<StringBuilder> callback) throws RequestException {
        final String url = restServiceContext + UPDATE_INSTANCES;

        server = checkServerUrl(server);

        String params = (name != null) ? "name=" + name + "&" : "";
        params += (vfsId != null) ? "vfsid=" + vfsId + "&" : "";
        params += (projectId != null) ? "projectid=" + projectId + "&" : "";
        params += (server != null) ? "server=" + server + "&" : "";
        params += "expr=" + expression;

        AsyncRequest.build(RequestBuilder.POST, url + "?" + params).loader(loader)
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

    public void validateAction(String action, String server, String appName, String framework, String url, String vfsId,
                               String projectId, int instances, int memory, boolean nostart, AppfogAsyncRequestCallback<String> callback)
            throws RequestException {
        final String postUrl = restServiceContext + VALIDATE_ACTION;

        server = checkServerUrl(server);

        String params = "action=" + action;
        params += (appName == null) ? "" : "&name=" + appName;
        params += (framework == null) ? "" : "&type=" + framework;
        params += (url == null) ? "" : "&url=" + url;
        params += "&instances=" + instances;
        params += "&mem=" + memory;
        params += "&nostart=" + nostart;
        params += (vfsId != null) ? "&vfsid=" + vfsId : "";
        params += (projectId != null) ? "&projectid=" + projectId : "";
        params += (server != null) ? "&server=" + server : "";

        AsyncRequest.build(RequestBuilder.POST, postUrl + "?" + params).loader(loader).send(callback);
    }

    public void getApplicationList(String server, AppfogAsyncRequestCallback<List<AppfogApplication>> callback)
            throws RequestException {
        String url = restServiceContext + APPS;

        server = checkServerUrl(server);

        if (server != null && !server.isEmpty()) {
            url += "?server=" + server;
        }

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    public void getSystemInfo(String server, AsyncRequestCallback<SystemInfo> callback) throws RequestException {
        final String url = restServiceContext + SYSTEM_INFO_URL;

        server = checkServerUrl(server);

        String params = (server == null) ? "" : "?server=" + server;

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    public void getTargets(AsyncRequestCallback<List<String>> callback) throws RequestException {
        String url = restServiceContext + TARGETS;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON)
                    .send(callback);
    }

    public void getTarget(AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restServiceContext + TARGET;

        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    public void getLogs(String vfsId, String projectId, AsyncRequestCallback<StringBuilder> callback)
            throws RequestException {
        String url = restServiceContext + LOGS + "?projectid=" + projectId + "&vfsid=" + vfsId;
        AsyncRequest.build(RequestBuilder.GET, url).loader(loader).send(callback);
    }

    public void services(String server, AsyncRequestCallback<AppfogServices> callback) throws RequestException {
        String url = restServiceContext + SERVICES;
        String params = (server != null) ? "?server=" + server : "";

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    public void createService(String server, String type, String name, String application, String vfsId,
                              String projectId, String infra, AppfogAsyncRequestCallback<AppfogProvisionedService> callback)
            throws RequestException {
        String url = restServiceContext + SERVICES_CREATE;
        StringBuilder params = new StringBuilder("?");
        params.append("type=").append(type).append("&");
        if (server != null && !server.isEmpty()) {
            params.append("server=").append(server).append("&");
        }
        if (application != null && !application.isEmpty()) {
            params.append("app=").append(application).append("&");
        }
        if (vfsId != null && !vfsId.isEmpty()) {
            params.append("vfsid=").append(vfsId).append("&");
        }
        if (projectId != null && !projectId.isEmpty()) {
            params.append("projectid=").append(projectId).append("&");
        }
        if (infra != null && !infra.isEmpty()) {
            params.append("infra=").append(infra).append("&");
        }
        params.append("name=").append(name);

        AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    public void deleteService(String server, String name, AppfogAsyncRequestCallback<Object> callback)
            throws RequestException {
        String url = restServiceContext + SERVICES_DELETE + "/" + name;
        String params = (server != null) ? "?server=" + server : "";

        AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    public void bindService(String server, String name, String application, String vfsId, String projectId,
                            AppfogAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + SERVICES_BIND + "/" + name;
        StringBuilder params = new StringBuilder("?");
        if (server != null && !server.isEmpty()) {
            params.append("server=").append(server).append("&");
        }
        if (application != null && !application.isEmpty()) {
            params.append("app=").append(application).append("&");
        }
        if (vfsId != null && !vfsId.isEmpty()) {
            params.append("vfsid=").append(vfsId).append("&");
        }
        if (projectId != null && !projectId.isEmpty()) {
            params.append("projectid=").append(projectId).append("&");
        }
        params.append("name=").append(name);

        AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    public void unbindService(String server, String name, String application, String vfsId, String projectId,
                              AppfogAsyncRequestCallback<Object> callback) throws RequestException {
        String url = restServiceContext + SERVICES_UNBIND + "/" + name;
        StringBuilder params = new StringBuilder("?");
        if (server != null && !server.isEmpty()) {
            params.append("server=").append(server).append("&");
        }
        if (application != null && !application.isEmpty()) {
            params.append("app=").append(application).append("&");
        }
        if (vfsId != null && !vfsId.isEmpty()) {
            params.append("vfsid=").append(vfsId).append("&");
        }
        if (projectId != null && !projectId.isEmpty()) {
            params.append("projectid=").append(projectId).append("&");
        }
        params.append("name=").append(name);

        AsyncRequest.build(RequestBuilder.POST, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    public void infras(String server, String vfsId, String projectId, AsyncRequestCallback<List<InfraDetail>> callback)
            throws RequestException {
        String url = restServiceContext + INFRAS;
        StringBuilder params = new StringBuilder("?");

        if (server != null && !server.isEmpty()) {
            params.append("server=").append(server).append("&");
        }
        if (vfsId != null && !vfsId.isEmpty()) {
            params.append("vfsid=").append(vfsId).append("&");
        }
        if (projectId != null && !projectId.isEmpty()) {
            params.append("projectid=").append(projectId).append("&");
        }

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader)
                    .header(HTTPHeader.ACCEPT, MimeType.APPLICATION_JSON).send(callback);
    }

    //------------------------------------------------

    private String checkServerUrl(String server) {
        if (server != null && !server.startsWith("http") && !server.startsWith("https")) {
            server = "http://" + server;
        }
        return server;
    }
}
