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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.HTTPHeader;
import com.codenvy.ide.rest.MimeType;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.codenvy.ide.websocket.MessageBus;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.google.gwt.http.client.RequestBuilder.GET;

/**
 * The implementation of {@link ApplicationRunnerClientService}.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
@Singleton
public class ApplicationRunnerClientServiceImpl implements ApplicationRunnerClientService {
    public static final String RUN     = "/run";
    public static final String DEBUG   = "/debug";
    public static final String PROLONG = "/prolong";
    private final String                          BASE_URL;
    private       MessageBus                      wsMessageBus;
    private       EventBus                        eventBus;
    private       RunnerLocalizationConstant constant;
    private       Loader                          loader;
    private       String                          restContext;

    /**
     * Create client service.
     *
     * @param wsMessageBus
     * @param eventBus
     * @param constant
     * @param loader
     * @param restContext
     */
    @Inject
    protected ApplicationRunnerClientServiceImpl(MessageBus wsMessageBus, EventBus eventBus, RunnerLocalizationConstant constant,
                                                 Loader loader, @Named("restContext") String restContext) {
        BASE_URL = '/' + Utils.getWorkspaceName() + "/runner";
        this.wsMessageBus = wsMessageBus;
        this.eventBus = eventBus;
        this.constant = constant;
        this.loader = loader;
        this.restContext = "/api/";
    }

    /** {@inheritDoc} */
    @Override
    public void runApplication(@NotNull String project, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String requestUrl = restContext + BASE_URL + "/run?project=" + project;
        AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
                    .requestStatusHandler(new RunningAppStatusHandler(project, eventBus, constant))
                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).send(callback);
    }

//    /** {@inheritDoc} */
//    @Override
//    public void runApplicationWS(@NotNull String project, @NotNull String war, boolean useJRebel,
//                                 @NotNull RequestCallback<ApplicationProcessDescriptor> callback) throws WebSocketException {
//        String params = "?war=" + war;
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("projectName", new JSONString(project));
//        if (useJRebel) {
//            jsonObject.put("jrebel", new JSONString("true"));
//        }
//        String data = jsonObject.toString();
//
//        callback.setStatusHandler(new RunningAppStatusHandler(project, eventBus, constant));
//
//        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, BASE_URL + RUN + params);
//        builder.data(data)
//               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data);
//        Message message = builder.build();
//
//        wsMessageBus.send(message, callback);
//    }

//    /** {@inheritDoc} */
//    @Override
//    public void debugApplication(@NotNull String project, @NotNull String war, boolean useJRebel,
//                                 @NotNull AsyncRequestCallback<ApplicationProcessDescriptor> callback) throws RequestException {
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("projectName", new JSONString(project));
//        if (useJRebel) {
//            jsonObject.put("jrebel", new JSONString("true"));
//        }
//        String data = jsonObject.toString();
//
//        String requestUrl = restContext + BASE_URL + "/debug?war=" + war + "&suspend=false";
//        AsyncRequest.build(RequestBuilder.POST, requestUrl, true)
//                    .requestStatusHandler(new RunningAppStatusHandler(project, eventBus, constant))
//                    .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data).send(callback);
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void debugApplicationWS(@NotNull String project, @NotNull String war, boolean useJRebel,
//                                   @NotNull RequestCallback<ApplicationInstance> callback) throws WebSocketException {
//        String param = "?war=" + war + "&suspend=false";
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("projectName", new JSONString(project));
//        if (useJRebel) {
//            jsonObject.put("jrebel", new JSONString("true"));
//        }
//        String data = jsonObject.toString();
//
//        callback.setStatusHandler(new RunningAppStatusHandler(project, eventBus, constant));
//
//        MessageBuilder builder = new MessageBuilder(RequestBuilder.POST, BASE_URL + DEBUG + param);
//        builder.data(data)
//               .header(HTTPHeader.CONTENTTYPE, MimeType.APPLICATION_JSON).data(data);
//        Message message = builder.build();
//
//        wsMessageBus.send(message, callback);
//    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(@NotNull String name, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        String url = restContext + BASE_URL + "/logs";
        String params = "?name=" + name;

        loader.setMessage("Retrieving logs.... ");

        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
    }

//    /** {@inheritDoc} */
//    @Override
//    public void prolongExpirationTime(@NotNull String name, long time, @NotNull RequestCallback<Object> callback)
//            throws WebSocketException {
//        String params = "?name=" + name + "&time=" + time;
//
//        MessageBuilder builder = new MessageBuilder(RequestBuilder.GET, BASE_URL + PROLONG + params);
//        Message message = builder.build();
//
//        wsMessageBus.send(message, callback);
//    }
//
//    /** {@inheritDoc} */
//    @Override
//    public void updateApplication(@NotNull String name, @NotNull String war, @NotNull AsyncRequestCallback<Object> callback)
//            throws RequestException {
//        String url = restContext + BASE_URL + "/update";
//        String params = "?name=" + name + "&war=" + war;
//
//        loader.setMessage("Updating application...");
//
//        AsyncRequest.build(RequestBuilder.GET, url + params).loader(loader).send(callback);
//    }

    /** {@inheritDoc} */
    @Override
    public void stopApplication(@NotNull String runningApp, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException {
        AsyncRequest.build(GET, runningApp).loader(loader).send(callback);
    }
}