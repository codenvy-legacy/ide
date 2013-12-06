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
package com.codenvy.ide.ext.java.client.projecttemplate.maven;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.resources.model.Property;
import com.codenvy.ide.rest.AsyncRequest;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.ui.loader.Loader;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

import static com.codenvy.ide.resources.marshal.JSONSerializer.PROPERTY_SERIALIZER;
import static com.codenvy.ide.rest.HTTPHeader.CONTENT_TYPE;
import static com.google.gwt.http.client.RequestBuilder.POST;

/**
 * The implementation of {@link CreateMavenProjectClientService}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class CreateMavenProjectClientServiceImpl implements CreateMavenProjectClientService {
    private static final String BASE_URL              = '/' + Utils.getWorkspaceName() + "/maven/create";
    private static final String CREATE_WAR_PROJECT    = BASE_URL + "/project/war";
    private static final String CREATE_JAVA_PROJECT   = BASE_URL + "/project/java";
    private static final String CREATE_SPRING_PROJECT = BASE_URL + "/project/spring";
    private String           restContext;
    private Loader           loader;
    private ResourceProvider resourceProvider;

    /**
     * Create service.
     *
     * @param restContext
     * @param loader
     * @param resourceProvider
     */
    @Inject
    protected CreateMavenProjectClientServiceImpl(@Named("restContext") String restContext, Loader loader,
                                                  ResourceProvider resourceProvider) {
        this.restContext = restContext;
        this.loader = loader;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void createWarProject(String projectName, Array<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        String requestUrl = restContext + CREATE_WAR_PROJECT;

        String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName;
        String url = requestUrl + param;

        loader.setMessage("Creating new project...");

        AsyncRequest.build(POST, url)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createSpringProject(String projectName, Array<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        String requestUrl = restContext + CREATE_SPRING_PROJECT;

        String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName;
        String url = requestUrl + param;

        loader.setMessage("Creating new project...");

        AsyncRequest.build(POST, url)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }

    /** {@inheritDoc} */
    @Override
    public void createJavaProject(String projectName, Array<Property> properties, AsyncRequestCallback<Void> callback)
            throws RequestException {
        String requestUrl = restContext + CREATE_JAVA_PROJECT;
        String param = "?vfsid=" + resourceProvider.getVfsId() + "&name=" + projectName;
        String url = requestUrl + param;

        loader.setMessage("Creating new project...");

        AsyncRequest.build(POST, url)
                    .data(PROPERTY_SERIALIZER.fromCollection(properties).toString())
                    .header(CONTENT_TYPE, "application/json").loader(loader).send(callback);
    }
}