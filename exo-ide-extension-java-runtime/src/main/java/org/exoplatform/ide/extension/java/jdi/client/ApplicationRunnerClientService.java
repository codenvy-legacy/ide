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
package org.exoplatform.ide.extension.java.jdi.client;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONString;

import org.exoplatform.gwtframework.commons.loader.Loader;
import org.exoplatform.gwtframework.commons.rest.AsyncRequest;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.HTTPHeader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.component.GWTLoader;
import org.exoplatform.ide.client.framework.websocket.MessageBus;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessage;
import org.exoplatform.ide.client.framework.websocket.rest.RequestMessageBuilder;
import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class ApplicationRunnerClientService {

    public static final String                    RUN      = "/run";
    public static final String                    DEBUG    = "/debug";
    public static final String                    PROLONG  = "/prolong";
    private static final String                   BASE_URL = "/java/runner";
    private static ApplicationRunnerClientService instance;

    private MessageBus                            wsMessageBus;

    private final String                          wsName;

    private final String                          restContext;

    public ApplicationRunnerClientService(MessageBus wsMessageBus, String wsName, String restContext) {
        this.wsName = wsName;
        this.restContext = restContext;
        this.wsMessageBus = wsMessageBus;
        instance = this;
    }

    public static ApplicationRunnerClientService getInstance() {
        return instance;
    }

    public void runApplication(String project, String war, boolean useJRebel,
                               AsyncRequestCallback<ApplicationInstance> callback) throws RequestException {
        String requestUrl = restContext + wsName + BASE_URL + "/run?war=" + war;

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectName", new JSONString(project));
        if (useJRebel) {
            jsonObject.put("jrebel", new JSONString("true"));
        }
        String data = jsonObject.toString();

        AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
                    .requestStatusHandler(new RunningAppStatusHandler(project))
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * Run application by sending request over WebSocket.
     * 
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws WebSocketException
     */
    public void runApplicationWS(String project, String war, boolean useJRebel,
                                 RequestCallback<ApplicationInstance> callback) throws WebSocketException {
        String params = "?war=" + war;

        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectName", new JSONString(project));
        if (useJRebel) {
            jsonObject.put("jrebel", new JSONString("true"));
        }
        String data = jsonObject.toString();

        callback.setStatusHandler(new RunningAppStatusHandler(project));
        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName + BASE_URL + RUN + params)
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data)
                                                      .getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    public void debugApplication(String project, String war, boolean useJRebel,
                                 AsyncRequestCallback<ApplicationInstance> callback) throws RequestException {
        
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectName", new JSONString(project));
        if (useJRebel) {
            jsonObject.put("jrebel", new JSONString("true"));
        }
        String data = jsonObject.toString();

        String requestUrl = restContext + wsName + BASE_URL + "/debug?war=" + war + "&suspend=false";
        AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
                    .requestStatusHandler(new RunningAppStatusHandler(project))
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
    }

    /**
     * Run application in debug mode by sending request over WebSocket.
     * 
     * @param project
     * @param war
     * @param useJRebel
     * @param callback
     * @throws WebSocketException
     */
    public void debugApplicationWS(String project, String war, boolean useJRebel,
                                   RequestCallback<ApplicationInstance> callback) throws WebSocketException {
        String param = "?war=" + war + "&suspend=false";

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("projectName", new JSONString(project));
        if (useJRebel) {
            jsonObject.put("jrebel", new JSONString("true"));
        }
        String data = jsonObject.toString();

        callback.setStatusHandler(new RunningAppStatusHandler(project));
        RequestMessage message =
                                 RequestMessageBuilder.build(RequestBuilder.POST, wsName +  BASE_URL + DEBUG + param)
                                                      .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data)
                                                      .getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    public void getLogs(String name, AsyncRequestCallback<StringBuilder> callback) throws RequestException {
        String url = restContext + wsName + BASE_URL  + "/logs";
        StringBuilder params = new StringBuilder("?name=");
        params.append(name);

        Loader loader = new GWTLoader();
        loader.setMessage("Retrieving logs.... ");

        AsyncRequest.build(RequestBuilder.GET, url + params.toString()).loader(loader).send(callback);
    }

    /**
     * Prolong expiration time of the application.
     * 
     * @param name application name
     * @param time time on which need to prolong expiration time of the application
     * @param callback {@link RESTfulRequestCallback}
     * @throws WebSocketException
     */
    public void prolongExpirationTime(String name, long time, RequestCallback<Object> callback) throws WebSocketException {
        StringBuilder params = new StringBuilder("?name=").append(name).append("&time=").append(time);
        RequestMessage message = RequestMessageBuilder.build(RequestBuilder.GET, wsName + BASE_URL + PROLONG + params).getRequestMessage();
        wsMessageBus.send(message, callback);
    }

    /**
     * Update already deployed Java web application.
     * 
     * @param name application name
     * @param war location of .war file. It may be local or remote location. File from this location will be used for update.
     * @param callback {@link AsyncRequestCallback}
     * @throws RequestException
     */
    public void updateApplication(String name, String war, AsyncRequestCallback<Object> callback) throws RequestException {
        String url = restContext + wsName + BASE_URL + "/update";
        StringBuilder params = new StringBuilder("?name=").append(name).append("&war=").append(war);

        Loader loader = new GWTLoader();
        loader.setMessage("Updating application...");

        AsyncRequest.build(RequestBuilder.GET, url + params.toString()).loader(loader).send(callback);
    }

}
