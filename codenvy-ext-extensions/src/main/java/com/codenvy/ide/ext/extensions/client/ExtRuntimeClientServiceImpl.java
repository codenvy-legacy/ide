/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.extensions.client;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.resources.marshal.JSONSerializer.PROPERTY_SERIALIZER;
import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * Implementation of {@link ExtRuntimeClientService} service.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ExtRuntimeClientServiceImpl.java Jul 3, 2013 12:50:30 PM azatsarynnyy $
 */
@Singleton
public class ExtRuntimeClientServiceImpl implements ExtRuntimeClientService {
    /** Base url. */
    private static final String BASE_URL      = '/' + Utils.getWorkspaceName() + "/extension/create";
    /** Create empty project method's path. */
    private static final String CREATE_EMPTY  = "/empty";
    /** Create sample project method's path. */
    private static final String CREATE_SAMPLE = "/sample";
    /** Run method's path. */
    private static final String LAUNCH        = "/run";
    /** REST-service context. */
    private String           restContext;
    /** Loader to be displayed. */
    private Loader           loader;
    /** Provider of IDE resources. */
    private ResourceProvider resourceProvider;

    /**
     * Create service.
     *
     * @param restContext
     *         REST-service context
     * @param loader
     *         loader to show on server request
     * @param resourceProvider
     *         provider of IDE resources
     */
    @Inject
    protected ExtRuntimeClientServiceImpl(@Named("restContext") String restContext,
                                          Loader loader,
                                          ResourceProvider resourceProvider) {
        this.loader = loader;
        this.restContext = restContext;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void createSampleCodenvyExtensionProject(@NotNull String projectName,
                                                    @NotNull JsonArray<Property> properties,
                                                    @NotNull String groupId,
                                                    @NotNull String artifactId,
                                                    @NotNull String version,
                                                    @NotNull AsyncRequestCallback<Void> callback) throws RequestException {
        final String requestUrl = restContext + BASE_URL + CREATE_SAMPLE;
        final String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName + "&rootid=" +
                             resourceProvider.getRootId()
                             + "&groupid=" + groupId + "&artifactid=" + artifactId + "&version=" + version;
        loader.setMessage("Creating new project...");
        AsyncRequest.build(POST, requestUrl + param)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void launch(@NotNull String projectName, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        final String requestUrl = "/api/" + Utils.getWorkspaceName() + "/runner/run";

        String params = "project=" + projectName;
        AsyncRequest.build(RequestBuilder.POST, requestUrl + "?" + params).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getStatus(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void getLogs(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        loader.setMessage("Retrieving logs...");
        AsyncRequest.build(RequestBuilder.GET, link.getHref()).loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void stop(@NotNull Link link, @NotNull AsyncRequestCallback<String> callback) throws RequestException {
        loader.setMessage("Stopping an application...");
        AsyncRequest.build(RequestBuilder.POST, link.getHref()).loader(loader).send(callback);
    }
}